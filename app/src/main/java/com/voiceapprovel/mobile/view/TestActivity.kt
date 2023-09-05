package com.voiceapprovel.mobile.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.voiceapprovel.mobile.constants.DropdownDoorNames
import com.voiceapprovel.mobile.databinding.LiveApprovalLayoutBinding
import com.voiceapprovel.mobile.databinding.LiveFeedbackApprovalBinding
import com.voiceapprovel.mobile.databinding.SelectionLayoutBinding
import com.voiceapprovel.mobile.viewmodel.UwbViewModel

class TestActivity : AppCompatActivity() {

    private lateinit var binding: LiveFeedbackApprovalBinding
    private lateinit var viewModel: UwbViewModel
    private var doorName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_test)

        binding = LiveFeedbackApprovalBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()

        binding.selectionLayout.root.visibility = View.GONE
        binding.feedbackLayout.root.visibility = View.VISIBLE

        viewModel = ViewModelProvider(this)[UwbViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        viewModel.startTimer()



        // selection layout code
        val doorNames = DropdownDoorNames.doorNameOptions

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doorNames)

        binding.selectionLayout.selectDoorSpinner.adapter = adapter
        binding.feedbackLayout.changeDoorSpinner.adapter = adapter

        binding.selectionLayout.selectDoorSpinner.onItemSelectedListener = object : OnItemSelectedListener{
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

        binding.selectionLayout.btnSelectDoor.setOnClickListener {
            binding.selectionLayout.root.visibility = View.GONE
            binding.feedbackLayout.root.visibility = View.VISIBLE
        }



        // feedback layout code
        viewModel.timer.observe(this){
            binding.feedbackLayout.tvCountdownTimer.text = it.toString()
        }

        viewModel.progressPercentage.observe(this){
            Log.d("PB", "progressPercentage: $it")
            binding.feedbackLayout.pb.setProgress(it.toFloat())
        }

    }
}