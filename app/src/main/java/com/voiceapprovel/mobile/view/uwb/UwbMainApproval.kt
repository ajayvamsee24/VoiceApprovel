package com.voiceapprovel.mobile.view.uwb

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.voiceapprovel.mobile.constants.DropdownDoorNames
import com.voiceapprovel.mobile.databinding.UwbApprovalLayoutBinding

class UwbMainApproval : AppCompatActivity() {
    private lateinit var binding: UwbApprovalLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UwbApprovalLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()

        val optionsForDoor = DropdownDoorNames.doorNameOptions

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, optionsForDoor)

        binding.selectionLayout.selectDoorSpinner.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        var doorName = ""

        binding.selectionLayout.selectDoorSpinner.onItemSelectedListener = object : OnItemSelectedListener {
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



       /* binding.btnAnnotation.setOnClickListener {
            Intent(this, MotionIntent::class.java).apply {
                startActivity(this)
            }
        }*/

        binding.selectionLayout.btnSelectDoor.setOnClickListener {

            Intent(this, LiveApproval::class.java).apply {
                putExtra("door", doorName)
                startActivity(this)
            }

        }

    }
}