package com.voiceapprovel.mobile.view.uwb

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.voiceapprovel.mobile.R
import com.voiceapprovel.mobile.api.model.UwbLiveDataModel
import com.voiceapprovel.mobile.constants.DropdownDoorNames
import com.voiceapprovel.mobile.databinding.LiveApprovalLayoutBinding
import com.voiceapprovel.mobile.viewmodel.UwbViewModel

class LiveApproval : AppCompatActivity() {

    private lateinit var viewModel: UwbViewModel

    private var id: String? = ""
    private var doorName: String? = ""
    private var inference: String? = ""
    private val delayMillis: Long = 1000
    private val autoSendDelayMillis: Long = 20000
    private var i = 0

    private val handler = Handler(Looper.myLooper()!!)

    private lateinit var binding: LiveApprovalLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LiveApprovalLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(this)[UwbViewModel::class.java]

        val doorName = intent.getStringExtra("door")

        val displayDoorNameText = doorName ?: DropdownDoorNames.doorNameOptions[0]

//        binding.tvDoorName.text = displayDoorNameText

        binding.liveFeedbackLayout.btnNotOpened.visibility = View.GONE
        binding.liveFeedbackLayout.btnOpened.visibility = View.GONE
        binding.liveFeedbackLayout.ivTick.visibility = View.GONE
        binding.liveFeedbackLayout.pb.visibility = View.GONE
        binding.liveFeedbackLayout.tvCountdownTimer.visibility = View.GONE

        binding.liveFeedbackLayout.tvQuery.visibility = View.VISIBLE

        binding.liveFeedbackLayout.tvQuery.text = "No Intent"
        binding.liveFeedbackLayout.tvQuery.setTextColor(
            ContextCompat.getColor(
                this@LiveApproval,
                R.color.no_intent_text_color
            )
        )
    }

    override fun onResume() {
        super.onResume()

        // selection layout code
        val doorNames = DropdownDoorNames.doorNameOptions

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doorNames)


        binding.liveFeedbackLayout.changeDoorSpinner.adapter = adapter

        binding.liveFeedbackLayout.changeDoorSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                doorName = DropdownDoorNames.doorNameOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                doorName = DropdownDoorNames.doorNameOptions[0]
            }

        }

        showGIFCustom(
            binding.liveFeedbackLayout.ivDooropenGif,
            R.raw.anim_door_open,
            R.drawable.ic_door_close_glass,
            ""
        )


        // viewModel.fetchUwVideo()

        handler.postDelayed(runnableRetry, delayMillis)


        viewModel.uwbResponseLiveData.observe(this) { uwbModel ->
            Log.d(TAG, "uwbResponseLiveData: $uwbModel")


            id = uwbModel?.id
            doorName = uwbModel?.doorName
            inference = uwbModel?.inference

            binding.liveFeedbackLayout.ivTick.visibility = View.GONE

            // Check if the inference result is "No Intent"
            if (inference?.equals("No Intent") == true) {
                // Hide the 'Not Opened' and 'Opened 'button
                binding.liveFeedbackLayout.btnNotOpened.visibility = View.GONE
                binding.liveFeedbackLayout.btnOpened.visibility = View.GONE
                binding.liveFeedbackLayout.ivDooropenGif.visibility = View.VISIBLE

                binding.liveFeedbackLayout.clCard.setBackgroundResource(R.color.card_layout_bg_color_when_no_intent)

                binding.liveFeedbackLayout.tvQuery.text = "No Intent"
                binding.liveFeedbackLayout.tvQuery.setTextColor(
                    ContextCompat.getColor(
                        this@LiveApproval,
                        R.color.no_intent_text_color
                    )
                )

                handler.removeCallbacks(noIntentRunnable)

                // Schedule a Runnable to be executed after a delay
                handler.postDelayed(runnableRetry, delayMillis)

                viewModel.stopTimer()
                binding.liveFeedbackLayout.tvCountdownTimer.visibility = View.GONE
                binding.liveFeedbackLayout.pb.visibility = View.GONE

            } else {
                binding.liveFeedbackLayout.btnNotOpened.visibility = View.VISIBLE
                binding.liveFeedbackLayout.btnOpened.visibility = View.VISIBLE
                binding.liveFeedbackLayout.ivDooropenGif.visibility = View.VISIBLE

                binding.liveFeedbackLayout.tvQuery.visibility = View.VISIBLE
                // binding.tvIntentLabel.visibility = View.VISIBLE

                binding.liveFeedbackLayout.tvQuery.text = "Did you expect the door to open?"
                binding.liveFeedbackLayout.tvQuery.setTextColor(
                    ContextCompat.getColor(
                        this@LiveApproval,
                        R.color.black
                    )
                )


                binding.liveFeedbackLayout.clCard.setBackgroundResource(R.color.card_layout_bg_color_when_non_no_intent)

                handler.removeCallbacks(runnableRetry)
                handler.removeCallbacks(noIntentRunnable)

                handler.postDelayed(noIntentRunnable, autoSendDelayMillis)

                viewModel.startTimer()
                binding.liveFeedbackLayout.tvCountdownTimer.visibility = View.VISIBLE
                binding.liveFeedbackLayout.pb.visibility = View.VISIBLE

            }
        }

        viewModel.apiSuccessLiveData.observe(this) {
            Log.d(TAG, "apiSuccessLiveData: $it")
            if (it) {
                // Re run Every 1 sec auto send approval and 2 sec delay for showing feedback
                handler.postDelayed(runnableRetry, delayMillis + 2000)
            } else {
                // handler.removeCallbacks(runnable)
            }
        }

        viewModel.timer.observe(this) { sec ->
            binding.liveFeedbackLayout.tvCountdownTimer.text = sec.toString()
            if (sec == 10) {
                binding.liveFeedbackLayout.tvQuery.text = "Did the door open by itself?"
            }
        }

        viewModel.progressPercentage.observe(this) {
            binding.liveFeedbackLayout.pb.setProgress(it.toFloat())
        }


        binding.liveFeedbackLayout.btnOpened.setOnClickListener {
            Toast.makeText(this, "OPENED", Toast.LENGTH_SHORT).show()
            viewModel.postUwbVideoData(id = id!!, inference = inference!!, feedback = "Accepted")

            handleUIAfterFeedback()
        }

        binding.liveFeedbackLayout.btnNotOpened.setOnClickListener {
            Toast.makeText(this, "NOT OPENED", Toast.LENGTH_SHORT).show()
            viewModel.postUwbVideoData(
                id = id!!,
                inference = inference!!,
                feedback = "Not Accepted"
            )

            handleUIAfterFeedback()
        }
    }

    private fun handleUwbModel(uwbModel: UwbLiveDataModel) {
        id = uwbModel.id
        doorName = uwbModel.doorName
        inference = uwbModel.inference

        if (inference == "No Intent") {
            handleNoIntent()
        } else {
            handleNonNoIntent()
        }

    }

    private fun handleNoIntent() {
        with(binding.liveFeedbackLayout) {
            btnNotOpened.visibility = View.GONE
            btnOpened.visibility = View.GONE
            ivDooropenGif.visibility = View.GONE

            // tvIntentLabel.visibility = View.GONE

            //tvQuery.text = "AI PREDICTION: $inference"

            handler.removeCallbacks(noIntentRunnable)
            handler.postDelayed(runnableRetry, delayMillis)

            viewModel.stopTimer()
            tvCountdownTimer.visibility = View.GONE
        }
    }

    private fun handleNonNoIntent() {
        with(binding.liveFeedbackLayout) {
            btnNotOpened.visibility = View.VISIBLE
            btnOpened.visibility = View.VISIBLE
            ivDooropenGif.visibility = View.VISIBLE

            //tvQuery.visibility = View.VISIBLE
            // tvIntentLabel.visibility = View.VISIBLE

            //tvQuery.text = "Did the door open automatically for you ?"

            handler.removeCallbacks(runnableRetry)
            handler.removeCallbacks(noIntentRunnable)

            handler.postDelayed(noIntentRunnable, autoSendDelayMillis)

            viewModel.startTimer()
            tvCountdownTimer.visibility = View.VISIBLE

            /* val intentLabel = when (inference) {
                 "False Intent" -> {
                     tvIntentLabel.setTextColor(
                         ContextCompat.getColor(
                             this@LiveApproval,
                             R.color.green
                         )
                     )
                     "Ai Prediction: Walk Towards"
                 }

                 "Walk Towards" -> {
                     tvIntentLabel.setTextColor(
                         ContextCompat.getColor(
                             this@LiveApproval,
                             R.color.red
                         )
                     )
                     "AI Prediction: Walk Nearby"
                 }

                 else -> {
                     tvIntentLabel.setTextColor(
                         ContextCompat.getColor(
                             this@LiveApproval,
                             R.color.black
                         )
                     )
                     "AI Prediction: No Intent"
                 }
             }*/

            // tvIntentLabel.text = intentLabel
        }
    }

    private val runnableRetry = object : Runnable {
        override fun run() {
            Log.d(TAG, "runnableRetry: ${i++}")
            handler.removeCallbacksAndMessages(null)
            handler.removeCallbacks(noIntentRunnable)

            viewModel.fetchUwVideo()

            handler.postDelayed(this, delayMillis)
        }
    }

    private val noIntentRunnable = object : Runnable {
        override fun run() {
            Log.d(TAG, "noIntentRunnable : Sending Accepted after $autoSendDelayMillis Delay")
            // viewModel.postUwbVideoData(id = id!!, inference=inference!!, feedback = "Accepted")
            handler.removeCallbacksAndMessages(null)

            viewModel.fetchUwVideo()
            handler.postDelayed(this, delayMillis)
        }
    }

    private fun handleUIAfterFeedback() {
        viewModel.stopTimer()
        binding.liveFeedbackLayout.tvCountdownTimer.visibility = View.GONE

        binding.liveFeedbackLayout.btnOpened.visibility = View.GONE
        binding.liveFeedbackLayout.btnNotOpened.visibility = View.GONE
        binding.liveFeedbackLayout.tvCountdownTimer.visibility = View.GONE


        binding.liveFeedbackLayout.tvQuery.visibility = View.VISIBLE
        binding.liveFeedbackLayout.tvQuery.text = "Thank you for the feedback, this helps us"

        binding.liveFeedbackLayout.ivTick.visibility = View.VISIBLE
    }

    private fun showGIFCustom(src: ImageView, resource: Int, placeholder: Int, show: String) {
        //clearGIFs();
        Glide.with(this@LiveApproval)
            .asGif()
            .load(resource)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<GifDrawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<GifDrawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any,
                    target: Target<GifDrawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (show == "once") {
                        resource?.setLoopCount(1)
                    }
                    return false
                }
            })
            .into(src)
    }


    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnableRetry)
        viewModel.stopTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val TAG = "LiveApproval"
    }
}