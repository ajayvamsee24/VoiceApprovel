package com.voiceapprovel.mobile.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.voiceapprovel.mobile.api.model.UwbOldDataModel
import com.voiceapprovel.mobile.constants.DropdownOptions
import com.voiceapprovel.mobile.databinding.MotionDataItemBinding
import com.voiceapprovel.mobile.model.VideoItem
import com.voiceapprovel.mobile.utilty.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Ajay Vamsee on 8/10/2023.
 * Time : 11:18
 */
class VideoListAdapter(
    private val videoItemList: MutableList<UwbOldDataModel>,
    private val mClickListener: ItemClickListener,
    private val context: Context
) : RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MotionDataItemBinding.inflate(inflater, parent, false)

        val uwbAdapterSpinner = ArrayAdapter(
            parent.context,
            android.R.layout.simple_spinner_dropdown_item,
            DropdownOptions.uwbOptions
        )

        binding.spinner3.adapter = uwbAdapterSpinner

        return VideoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videoItemList.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem = videoItemList[position]
        holder.bind(videoItem, position)
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        //player?.release()
        super.onViewRecycled(holder)
    }

    inner class VideoViewHolder(
        private val binding: MotionDataItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        init {
            Log.d(TAG, "$videoItemList ")

            binding.pvMedia.setOnClickListener {
                Log.d(TAG, "pvMedia ")
            }

            binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(TAG, "onItemSelected: ${DropdownOptions.uwbOptions[position]}")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d(TAG, "onNothingSelected: ")
                }
            }

            binding.ivApproved.setOnClickListener {
                Log.d(TAG, "ivApproved: ")
            }

        }

        private var player: ExoPlayer? = null


        fun bind(uwbOldData: UwbOldDataModel, position: Int) {
            Log.d(TAG, "bind: position $position")
            releasePlayer() // / Release the previous player


            player = ExoPlayer.Builder(context).build()
            val mediaItem = MediaItem.fromUri(Uri.parse(uwbOldData.publicUrl))
            player?.setMediaItem(mediaItem)
            player?.prepare()

            binding.pvMedia.player = player

            // Time and date setup
            CoroutineScope(Dispatchers.Main).launch {
                val (time, date) = withContext(Dispatchers.Main) {
                    DateTimeUtils.formatDateTime(uwbOldData.createdAt.date)
                }

                binding.tvTime.text = time
                binding.tvDate.text = date
            }

            // spinner label setup
            val spinnerLabel = DropdownOptions.uwbOptions.indexOf(uwbOldData.label)
            if (spinnerLabel == -1) {
                binding.spinner3.setSelection(4)
            } else {
                binding.spinner3.setSelection(spinnerLabel)
            }

        }

        private fun releasePlayer() {
            player?.pause()
            player?.release()
            player = null
        }

    }


    interface ItemClickListener {
        fun itemClick(v: View?, item: VideoItem?, position: Int)
    }

    companion object {
        private val TAG = VideoListAdapter::class.java.simpleName
    }
}