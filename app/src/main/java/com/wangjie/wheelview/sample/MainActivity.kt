package com.wangjie.wheelview.sample
import android.R.attr
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.wangjie.wheelview.R
import com.wangjie.wheelview.WheelView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.R.attr.action
import android.widget.AdapterView

import android.preference.PreferenceManager

import android.content.SharedPreferences
import android.widget.AdapterView.OnItemSelectedListener


class MainActivity:AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val PLANETS = ArrayList<String>()
        lateinit var dateText: TextView
        lateinit var dialogText: TextView
        private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
    }

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PLANETS.clear()
        for (i in 0 until 200) {
            PLANETS.add(i, "item $i")
        }
        findViewById<Button>(R.id.main_show_dialog_btn).setOnClickListener(this)
        dateText = findViewById(R.id.dateText)
        dialogText = findViewById(R.id.dialogText)

        val builder : MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()

        val picker : MaterialDatePicker<*> =
            builder
                .setTheme(R.style.MaterialCalendarTheme)
                .build()
        findViewById<Button>(R.id.date_picker_btn).setOnClickListener{
            picker.show(supportFragmentManager, picker.toString())
        }


        picker.addOnPositiveButtonClickListener {
            val selectedDate = dateFormat.format(it)
            dateText.text = "$selectedDate"
        }
    }

    override fun onClick(v:View) {
        when (v.id) {
            R.id.main_show_dialog_btn -> {
                val outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null)
                val wv = outerView.findViewById(R.id.wheel_view_wv) as WheelView
                wv.offset = 3
                wv.setItems(PLANETS)
                wv.setSelection(0)

                //builder
                val builder = AlertDialog.Builder(this)
                builder.setView(outerView)

                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)


                wv.onWheelViewListener = object :WheelView.OnWheelViewListener() {
                     override fun onSelected(index:Int, item:String?) {

                    }


                    override fun onClickItem(index: Int, item: String?) {
                         Log.d(TAG, "index: ${index-3}, item: $item")
                         dialogText.text = "Index is ${index-3} " + "Text is $item"
                         dialog.dismiss()
                    }
                }

                dialog.show()

            }
        }
    }


}

