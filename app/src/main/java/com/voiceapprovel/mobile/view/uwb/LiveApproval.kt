package com.voiceapprovel.mobile.view.uwb

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.voiceapprovel.mobile.R
import com.voiceapprovel.mobile.constants.DropdownDoorNames
import com.voiceapprovel.mobile.databinding.LiveApprovalLayoutBinding
import com.voiceapprovel.mobile.viewmodel.AudioPredictionViewModel

class LiveApproval : AppCompatActivity() {

    private lateinit var viewModel: AudioPredictionViewModel

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

        viewModel = ViewModelProvider(this)[AudioPredictionViewModel::class.java]

        val doorName = intent.getStringExtra("door")

        val displayDoorNameText = doorName ?: DropdownDoorNames.doorNameOptions[0]

        binding.tvDoorName.text = displayDoorNameText

        binding.btnNotOpened.visibility = View.GONE
        binding.btnOpened.visibility = View.GONE
        binding.doorgif.visibility = View.GONE
        binding.tvQuery.visibility = View.VISIBLE

        binding.tvQuery.text = "MODEL PREDICTION: \n No Intent"
    }

    override fun onResume() {
        super.onResume()


        // viewModel.fetchUwVideo()

        handler.postDelayed(runnableRetry, delayMillis)


        viewModel.uwbResponseLiveData.observe(this) { uwbModel ->
            Log.d(TAG, "uwbResponseLiveData: $uwbModel")
            id = uwbModel?.id
            doorName = uwbModel?.doorName
            inference = uwbModel?.inference


            // Check if the inference result is "No Intent"
            if (inference?.equals("No Intent") == true) {
                // Hide the 'Not Opened' and 'Opened 'button
                binding.btnNotOpened.visibility = View.GONE
                binding.btnOpened.visibility = View.GONE
                binding.doorgif.visibility = View.GONE
                //binding.tvQuery.visibility = View.GONE
                binding.tvIntentLabel.visibility = View.GONE

                binding.tvQuery.text = "AI PREDICTION: $inference"

                handler.removeCallbacks(noIntentRunnable)

                // Schedule a Runnable to be executed after a delay
                handler.postDelayed(runnableRetry, delayMillis)

                viewModel.stopTimer()
                binding.tvTimer.visibility = View.GONE

            } else {
                binding.btnNotOpened.visibility = View.VISIBLE
                binding.btnOpened.visibility = View.VISIBLE
                binding.doorgif.visibility = View.VISIBLE
                binding.tvQuery.visibility = View.VISIBLE
                binding.tvIntentLabel.visibility = View.VISIBLE

                binding.tvQuery.text = "Did the dooropen Automatically for You ?"

                handler.removeCallbacks(runnableRetry)
                handler.removeCallbacks(noIntentRunnable)

                handler.postDelayed(noIntentRunnable, autoSendDelayMillis)

                viewModel.startTimer()
                binding.tvTimer.visibility = View.VISIBLE


                with(binding) {
                    if (inference?.equals("False Intent") == true) {
                        tvIntentLabel.text = "AI Prediction: Walk Towards"
                        tvIntentLabel.setTextColor(
                            ContextCompat.getColor(
                                this@LiveApproval,
                                R.color.green
                            )
                        )
                    } else if (inference?.equals("Walk Towards") == true) {
                        tvIntentLabel.text = "AI Prediction: Walk Nearby"
                        tvIntentLabel.setTextColor(
                            ContextCompat.getColor(
                                this@LiveApproval,
                                R.color.red
                            )
                        )
                    } else {
                        tvIntentLabel.text = "AI Prediction: No Intent"
                        tvIntentLabel.setTextColor(
                            ContextCompat.getColor(
                                this@LiveApproval,
                                R.color.black
                            )
                        )
                    }
                }

            }


        }

        viewModel.apiSuccessLiveData.observe(this) {
            Log.d(TAG, "apiSuccessLiveData: $it")
            if (it) {
                // Re run Every 1 sec auto send approval
                handler.postDelayed(runnableRetry, delayMillis)
            } else {
                // handler.removeCallbacks(runnable)
            }
        }

        viewModel.timer.observe(this) { sec ->
            binding.tvTimer.text = sec.toString()
        }


        binding.btnOpened.setOnClickListener {
            Toast.makeText(this, "OPENED $id $doorName $inference", Toast.LENGTH_SHORT).show()
            viewModel.postUwbVideoData(id = id!!, inference = inference!!, feedback = "Accepted")

            viewModel.stopTimer()
            binding.tvTimer.visibility = View.GONE
        }

        binding.btnNotOpened.setOnClickListener {
            Toast.makeText(this, "NOT OPENED", Toast.LENGTH_SHORT).show()
            viewModel.postUwbVideoData(
                id = id!!,
                inference = inference!!,
                feedback = "Not Accepted"
            )

            viewModel.stopTimer()
            binding.tvTimer.visibility = View.GONE
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