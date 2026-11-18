package com.example.sleepwell

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var number = 1
    private var selectedDate = ""
    private val entriesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutHome = findViewById<LinearLayout>(R.id.layoutHome)
        val layoutCalendar = findViewById<LinearLayout>(R.id.layoutCalendar)
        val tvNumber = findViewById<TextView>(R.id.tvNumber)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val btnDate = findViewById<Button>(R.id.btnDate)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        val listViewCalendar = findViewById<ListView>(R.id.listViewCalendar)
        val navHome = findViewById<Button>(R.id.navHome)
        val navCalendar = findViewById<Button>(R.id.navCalendar)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entriesList)
        listViewCalendar.adapter = adapter

        val cal = Calendar.getInstance()
        var year = cal[Calendar.YEAR]
        var month = cal[Calendar.MONTH]
        var day = cal[Calendar.DAY_OF_MONTH]

        selectedDate = "$day/${month + 1}/$year"
        btnDate.text = getString(R.string.date_format, selectedDate)

        btnMinus.setOnClickListener {
            if (number > 1) {
                number--
                tvNumber.text = number.toString()
            }
        }

        btnPlus.setOnClickListener {
            if (number < 10) {
                number++
                tvNumber.text = number.toString()
            }
        }

        btnDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, sYear, sMonth, sDay ->
                    year = sYear
                    month = sMonth
                    day = sDay
                    selectedDate = "$day/${month + 1}/$year"
                    btnDate.text = getString(R.string.date_format, selectedDate)
                },
                year,
                month,
                day,
            ).show()
        }

        btnSave.setOnClickListener {
            val entryText = getString(R.string.entry_format, selectedDate, number)
            entriesList.add(entryText)
            adapter.notifyDataSetChanged()
            tvEmpty.visibility = View.GONE
            Toast.makeText(this, getString(R.string.added_to_calendar), Toast.LENGTH_SHORT).show()
        }

        navHome.setOnClickListener {
            layoutHome.visibility = View.VISIBLE
            layoutCalendar.visibility = View.GONE
        }

        navCalendar.setOnClickListener {
            layoutHome.visibility = View.GONE
            layoutCalendar.visibility = View.VISIBLE
            tvEmpty.visibility = if (entriesList.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
