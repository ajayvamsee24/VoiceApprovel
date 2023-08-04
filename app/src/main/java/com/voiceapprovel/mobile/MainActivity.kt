package com.voiceapprovel.mobile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.voiceapprovel.mobile.adapter.AudioListAdapter
import com.voiceapprovel.mobile.animation.RemoveItemAnimator
import com.voiceapprovel.mobile.databinding.ActivityMainBinding
import com.voiceapprovel.mobile.model.AudioItem
import com.voiceapprovel.mobile.model.AudioPrediction
import com.voiceapprovel.mobile.viewmodel.AudioPredictionViewModel
import com.voiceapprovel.mobile.viewmodel.AudioPredictionViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity(), AudioListAdapter.OnItemClicked {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: AudioPredictionViewModel

    private lateinit var adapter1: AudioListAdapter

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rvOldList.layoutManager = LinearLayoutManager(this)
        binding.rvNewList.layoutManager = LinearLayoutManager(this)

        val animator = RemoveItemAnimator()
        binding.rvOldList.itemAnimator = animator



        // Initialize ViewModel
        val factory = AudioPredictionViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[AudioPredictionViewModel::class.java]

        val animation = AnimationUtils.loadAnimation(this, R.anim.fall_down)


        viewModel.audioPredictions.observe(this) { audioPredictions ->

            val mutableDataList = audioPredictions.toMutableList()

            val (oldList, newList) = splitArrayByCondition(mutableDataList)

            val newDataAdapter = AudioListAdapter(oldList, this)
            val oldDataAdapter = AudioListAdapter(newList, this)


            lifecycleScope.launch(Dispatchers.Main) {

                binding.rvOldList.adapter = oldDataAdapter
                binding.rvNewList.adapter = newDataAdapter



                adapter1 = newDataAdapter
            }

            binding.rvOldList.animation = animation
            binding.rvOldList.scheduleLayoutAnimation()


            mutableDataList.forEach { audioObject ->
                Log.d("TAG", "onCreate: ${audioObject.createdAt.date}")
            }

        }


        viewModel.fetchAudioPredictions()

        //viewModel.postAudioApproval("64cb8bd2c74f975ca75b0b6e", "door_open")

        viewModel.apiResponseLiveData.observe(this) {
            Log.d(TAG, "apiResponseLiveData: $it")
        }


    }

    private fun filterAudioObjectsByTime(audioPredictions: List<AudioPrediction>):
            Pair<MutableList<AudioPrediction>, MutableList<AudioPrediction>> {
        val oldList = mutableListOf<AudioPrediction>()
        val newList = mutableListOf<AudioPrediction>()

        // Get the current time
        val currentTime = Date()

        val currentDate = dateFormatter.format(currentTime)


        Log.d(TAG, "Current ****************$currentDate} ")

        val thirtyMinutesAgo = Date(currentTime.time - (30 * 60 * 1000)) // 30 minutes ago

        Log.d(TAG, "thirtyMinutesAgo: $thirtyMinutesAgo")



        lifecycleScope.launch(Dispatchers.Default) {

            audioPredictions.forEach { audioObject ->
                val createdAtDate = dateFormatter.parse(audioObject.createdAt.date)
                Log.d(TAG, "createdAtDate: $createdAtDate")
                if (createdAtDate != null) {

                    // Check if the audio object's date is before 30 minutes ago
                    if (createdAtDate.before(thirtyMinutesAgo)) {
                        oldList.add(audioObject)
                    } else {
                        newList.add(audioObject)
                    }
                }
            }
        }

        return Pair(oldList, newList)
    }

    private fun splitArrayByCondition(sortedArray: List<AudioPrediction>): Pair<MutableList<AudioPrediction>, MutableList<AudioPrediction>> {
        //val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

        // Set the time zone to India
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")

        // Get the current date in India time zone
        val currentDate = Date()

        val someMinutesInterval: Long = 30 * 60 * 1000 // 30 minutes in milliseconds

        val newArray = mutableListOf<AudioPrediction>()
        val oldArray = mutableListOf<AudioPrediction>()

        for (audioPrediction in sortedArray) {
            val date = dateFormatter.parse(audioPrediction.createdAt.date)
            if (date != null) {
                val timeInterval = currentDate.time - date.time
                if (timeInterval <= someMinutesInterval) {
                    newArray.add(audioPrediction)
                } else {
                    oldArray.add(audioPrediction)
                }
            }
        }
        return Pair(newArray, oldArray)
    }

    override fun onResume() {
        super.onResume()

        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.d("TAG", "setOnRefreshListener: ")
            simulateDataLoading()
        }

        binding.rvNewList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged: $newState")
                if (newState == RecyclerView.SCROLL_INDICATOR_END) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter1.itemCount - 1) {
                        binding.rvOldList.scrollToPosition(0)
                    }
                }
            }
        })

        //adapter1.setOnItemClickListener(this)


    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun createDummyAudioList(): MutableList<AudioItem> {
        val dummyList = mutableListOf<AudioItem>()
        dummyList.add(AudioItem("https://example.com/audio1.mp3", "Audio 1", "3:45", ""))
        dummyList.add(AudioItem("https://example.com/audio2.mp3", "Audio 2", "2:30", ""))
        dummyList.add(AudioItem("https://example.com/audio3.mp3", "Audio 3", "4:15", ""))
        dummyList.add(AudioItem("https://example.com/audio1.mp3", "Audio 1", "3:45", ""))
        dummyList.add(AudioItem("https://example.com/audio2.mp3", "Audio 2", "2:30", ""))
        dummyList.add(AudioItem("https://example.com/audio3.mp3", "Audio 3", "4:15", ""))
        dummyList.add(AudioItem("https://example.com/audio1.mp3", "Audio 1", "3:45", ""))
        dummyList.add(AudioItem("https://example.com/audio2.mp3", "Audio 2", "2:30", ""))
        dummyList.add(AudioItem("https://example.com/audio3.mp3", "Audio 3", "4:15", ""))
        dummyList.add(AudioItem("https://example.com/audio1.mp3", "Audio 1", "3:45", ""))
        dummyList.add(AudioItem("https://example.com/audio2.mp3", "Audio 2", "2:30", ""))
        dummyList.add(AudioItem("https://example.com/audio3.mp3", "Audio 3", "4:15", ""))
        dummyList.add(AudioItem("https://example.com/audio1.mp3", "Audio 1", "3:45", ""))
        dummyList.add(AudioItem("https://example.com/audio2.mp3", "Audio 2", "2:30", ""))
        dummyList.add(AudioItem("https://example.com/audio3.mp3", "Audio 3", "4:15", ""))
        dummyList.add(AudioItem("https://example.com/audio1.mp3", "Audio 1", "3:45", ""))
        dummyList.add(AudioItem("https://example.com/audio2.mp3", "Audio 2", "2:30", ""))
        dummyList.add(AudioItem("https://example.com/audio3.mp3", "Audio 3", "4:15", ""))
        dummyList.add(AudioItem("https://example.com/audio1.mp3", "Audio 1", "3:45", ""))
        dummyList.add(AudioItem("https://example.com/audio2.mp3", "Audio 2", "2:30", ""))
        dummyList.add(AudioItem("https://example.com/audio3.mp3", "Audio 3", "4:15", ""))
        dummyList.add(AudioItem("https://example.com/audio1.mp3", "Audio 1", "3:45", ""))
        dummyList.add(AudioItem("https://example.com/audio2.mp3", "Audio 2", "2:30", ""))
        dummyList.add(AudioItem("https://example.com/audio3.mp3", "Audio 3", "4:15", ""))
        // Add more dummy items as needed
        return dummyList
    }

    private fun simulateDataLoading() {
        // Load the layout animation from XML
        viewModel.fetchAudioPredictions()
        binding.mainLayout.visibility = View.GONE
        // Simulate data loading with a delay
        binding.swipeRefreshLayout.postDelayed({
            // Once the data is loaded, stop the loading animation
            binding.swipeRefreshLayout.isRefreshing = false
            binding.mainLayout.visibility = View.VISIBLE
        }, 2000) // Replace 2000ms with your actual data loading time.
    }

    companion object {
        private val TAG = "MainActivityLogs"
    }

    override fun audioItem(audioItem: AudioPrediction, newLabel: String) {
        Log.d(TAG, "audioItem: ID ${audioItem.id} LABEL $newLabel")
        val modelID = audioItem.id.toString()
        viewModel.postAudioApproval(id = modelID, newLabel = newLabel)
    }


}