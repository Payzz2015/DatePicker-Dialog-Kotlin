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
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.CalendarConstraints


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

        var mSelectedDate: Long = MaterialDatePicker.thisMonthInUtcMilliseconds()
        fun Context.showDatePickerDialogWithMaximumDateThemeNew() {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            cal.timeInMillis = today
            cal.set(cal[Calendar.YEAR] - 80, cal[Calendar.MONTH], cal[Calendar.DATE])
            val yearsBack = cal.timeInMillis
            cal.timeInMillis = today
            cal.set(cal[Calendar.YEAR] - 0, cal[Calendar.MONTH], cal[Calendar.DATE])
            val yearsAhead = cal.timeInMillis


            var builder: MaterialDatePicker.Builder<Long>  = MaterialDatePicker.Builder.datePicker()
            builder
                .setTheme(R.style.MaterialCalendarTheme)
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(mSelectedDate)
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setStart(yearsBack)
                        .setEnd(yearsAhead)
                        .setOpenAt(mSelectedDate)
                        .build()
                )
            val materialDatePicker: MaterialDatePicker<Long> = builder.build()

            materialDatePicker.show((this as FragmentActivity).supportFragmentManager, materialDatePicker.toString())

            materialDatePicker.addOnPositiveButtonClickListener {
                mSelectedDate = it
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val selectedDate = sdf.format(it)




        Toast.makeText(
            this, selectedDate.toString(),
            Toast.LENGTH_SHORT
        ).show()
            }
        }
        findViewById<Button>(R.id.date_picker_btn).setOnClickListener{
            showDatePickerDialogWithMaximumDateThemeNew()
        }
    }

    override fun onClick(v:View) {
        when (v.id) {
            R.id.main_show_dialog_btn -> {
                val outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null)
                val wv = outerView.findViewById(R.id.wheel_view_wv) as WheelView
                wv.offset = 2
                wv.setItems(PLANETS)
                wv.setSelection(0)

                //builder
                val builder = AlertDialog.Builder(this, R.style.RoundedCornerDialog)
                builder.setView(outerView)

                val dialog = builder.create()


                wv.onWheelViewListener = object :WheelView.OnWheelViewListener() {
                     override fun onSelected(index:Int, item:String?) {

                    }


                    override fun onClickItem(index: Int, item: String?) {
                         Log.d(TAG, "index: ${index-wv.offset}, item: $item")
                         dialogText.text = "Index is ${index-wv.offset} " + "Text is $item"
                         dialog.dismiss()
                    }
                }

                dialog.show()

            }
        }
    }


}

