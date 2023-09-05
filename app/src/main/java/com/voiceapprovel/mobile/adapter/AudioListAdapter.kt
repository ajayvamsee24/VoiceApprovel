package com.voiceapprovel.mobile.adapter

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.voiceapprovel.mobile.view.MainActivity
import com.voiceapprovel.mobile.R
import com.voiceapprovel.mobile.constants.DropdownOptions
import com.voiceapprovel.mobile.databinding.AudioItemBinding
import com.voiceapprovel.mobile.api.model.AudioPrediction
import com.voiceapprovel.mobile.utilty.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Ajay Vamsee on 8/2/2023.
 * Time : 17:28
 */
class AudioListAdapter(
    private val audioItems: MutableList<AudioPrediction>,
    private val onItemClickListener: MainActivity
) :
    RecyclerView.Adapter<AudioListAdapter.AudioViewHolder>() {


    private var mediaPlayer: MediaPlayer? = null
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var itemClicked: OnItemClicked

    private var userLabel = DropdownOptions.options[1]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioViewHolder {
        //Log.d(TAG, "onCreateViewHolder: $audioItems")
        val inflater = LayoutInflater.from(parent.context)
        val binding = AudioItemBinding.inflate(inflater, parent, false)

        binding.circlePb.setProgress(0F)
        handler = Handler(Looper.myLooper()!!)

        // initial Drop down items
        val spinnerOptions = DropdownOptions.options

        val defaultAdapter =
            ArrayAdapter(
                parent.context,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerOptions
            )

        binding.spinner.adapter = defaultAdapter

        val defaultOption = spinnerOptions.firstOrNull()

        defaultOption?.let {
            val defaultIndex = spinnerOptions.indexOf(defaultOption)
            // binding.spinner.setSelection(defaultIndex+1)
        }

        return AudioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audioItem = audioItems[position]
        holder.bind(audioItem, position)
    }

    override fun getItemCount(): Int {
        return audioItems.size
    }


    inner class AudioViewHolder(private val binding: AudioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set the OnItemSelectedListener to the Spinner
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // The 'position' variable holds the index of the selected item
                    // Use 'position' to get the selected item from your data list if needed
                    userLabel = DropdownOptions.options[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This is called when no item is selected. You can handle this case if needed.
                }
            }
        }

        fun bind(audioItem: AudioPrediction, position: Int) {
            Log.d(TAG, "bind:  ID ${audioItem.id} ")


            // play audio
            binding.ivAudio.setOnClickListener {
                Log.d(TAG, "bind: ${audioItem.publicurl}")

                val publicUrl = audioItem.publicurl

                publicUrl.let { url ->
                    try {
                        if (mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.seekTo(0)
                        } else {
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(url)
                                prepare()
                            }
                        }
                        mediaPlayer?.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                //handler.removeCallbacks(updateProgressBar)
                handler.postDelayed(updateProgressBar, 3)


            }


            // spinner label setup
            val spinnerLabel = DropdownOptions.options.indexOf(audioItem.label)
            Log.d(TAG, "bind: spinner Label $spinnerLabel")
            if (spinnerLabel == -1) {
                binding.spinner.setSelection(4)
            } else {
                binding.spinner.setSelection(spinnerLabel)
            }


            // Time and date
            CoroutineScope(Dispatchers.Main).launch {
                val (time, date) = withContext(Dispatchers.Default) {
                    DateTimeUtils.formatDateTime(audioItem.createdAt.date)
                }
                Log.d(TAG, "bind: TIME $time and $date")

                binding.tvTime.text = time
                binding.tvDate.text = date
            }


            // approved listener
            binding.ivApproved.setOnClickListener { view ->

                val slideOutAnimation = AnimationUtils.loadAnimation(view.context, R.anim.slide_in)
                view.animation = slideOutAnimation

                onItemClickListener.audioItem(audioItem = audioItem, newLabel = userLabel)

                /*val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_out)
                animation.duration = 400
                view.startAnimation(animation)*/

                removeItem(position, view)
            }
        }


        private val updateProgressBar = object : Runnable {
            var progress = 1
            override fun run() {

                binding.circlePb.setProgress(progress = progress.toFloat())

                // Increment the progress
                progress += 1

                if (progress <= 100) {
                    // Run this Runnable again after a short delay (e.g., 1000ms)
                    handler.postDelayed(this, 3)
                } else {
                    progress = 1
                    binding.circlePb.setProgress(progress = progress.toFloat())
                }

            }

        }


        private fun removeItem(position: Int, view: View) {
            Log.d(TAG, "removeItem: Position $position and SIZE ${audioItems.size}")
            if (position in 0 until audioItems.size) {
                audioItems.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
            }
        }

    }

    fun stopMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(runnable)
    }

    companion object {
        private val TAG = AudioListAdapter::class.java.name
    }

    interface OnItemClicked {
        fun audioItem(audioItem: AudioPrediction, newLabel: String)
    }

    fun setOnItemClickListener(listener: OnItemClicked) {
        itemClicked = listener
    }
}