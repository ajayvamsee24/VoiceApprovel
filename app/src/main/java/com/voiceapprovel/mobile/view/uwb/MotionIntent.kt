package com.voiceapprovel.mobile.view.uwb

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.voiceapprovel.mobile.adapter.VideoListAdapter
import com.voiceapprovel.mobile.api.model.UwbOldDataModel
import com.voiceapprovel.mobile.databinding.MotionIntentLayoutBinding
import com.voiceapprovel.mobile.model.VideoItem
import com.voiceapprovel.mobile.utilty.VideoUrl
import com.voiceapprovel.mobile.viewmodel.UwbViewModel

class MotionIntent : AppCompatActivity(), VideoListAdapter.ItemClickListener {

    private lateinit var binding: MotionIntentLayoutBinding

    private lateinit var videoListAdapter: VideoListAdapter

    private lateinit var viewModel: UwbViewModel

    private var uwbOldData: MutableList<UwbOldDataModel> =
        emptyList<UwbOldDataModel>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MotionIntentLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[UwbViewModel::class.java]

        viewModel.fetchOldUwbVideos()
        binding.pbUwb.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        binding.rvMotionData.layoutManager = LinearLayoutManager(this)


        viewModel.uwbOldVideoData.observe(this) {
            uwbOldData = it.toMutableList()
            Log.d("uwbOldVideoData", "onResume: $uwbOldData")
            binding.pbUwb.visibility = View.GONE

            videoListAdapter = VideoListAdapter(uwbOldData, this, this)
            binding.rvMotionData.adapter = videoListAdapter
        }


    }

    private fun dummyData(): MutableList<VideoItem> {
        val dummyList = mutableListOf<VideoItem>()

        dummyList.add(VideoItem("video 1", VideoUrl.url1))
        dummyList.add(VideoItem("video 2", VideoUrl.url1))
        dummyList.add(VideoItem("video 2", VideoUrl.url1))


        return dummyList
    }

    override fun itemClick(v: View?, item: VideoItem?, position: Int) {
        TODO("Not yet implemented")
    }
}