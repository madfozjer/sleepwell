package com.example.sleepwell

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var number = 1
    private var selectedDate = ""
    private val entriesList = ArrayList<String>()
    private val ratingsList = ArrayList<Int>()
    private val savedDatesSet = HashSet<String>()
    private var activeTab = TAB_HOME

    companion object {
        private const val TAB_HOME = 0
        private const val TAB_CALENDAR = 1
        private const val TAB_AVERAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Restore instance state if saved (e.g. after theme toggle)
        if (savedInstanceState != null) {
            number = savedInstanceState.getInt("KEY_NUMBER", 1)
            selectedDate = savedInstanceState.getString("KEY_DATE", "") ?: ""
            savedInstanceState.getStringArrayList("KEY_ENTRIES")?.let {
                entriesList.addAll(it)
            }
            savedInstanceState.getIntegerArrayList("KEY_RATINGS")?.let {
                ratingsList.addAll(it)
            } ?: run {
                // Parse rating from entries if ratingsList wasn't in Bundle
                entriesList.forEach { entry ->
                    val ratingStr = entry.substringAfter("Rating: ", "").trim()
                    ratingStr.toIntOrNull()?.let { ratingsList.add(it) }
                }
            }
            activeTab = savedInstanceState.getInt("KEY_ACTIVE_TAB", TAB_HOME)
        }

        // Populate set of dates that already have entries
        savedDatesSet.clear()
        entriesList.forEach { entry ->
            val dateStr = entry.substringAfter("Date: ", "").substringBefore(" |").trim()
            if (dateStr.isNotEmpty()) {
                savedDatesSet.add(dateStr)
            }
        }

        // 1. Find UI Views
        val btnThemeToggle = findViewById<ImageButton>(R.id.btnThemeToggle)
        val layoutHome = findViewById<LinearLayout>(R.id.layoutHome)
        val layoutAverage = findViewById<LinearLayout>(R.id.layoutAverage)
        val layoutCalendar = findViewById<LinearLayout>(R.id.layoutCalendar)
        val tvNumber = findViewById<TextView>(R.id.tvNumber)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val btnDate = findViewById<Button>(R.id.btnDate)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        val listViewCalendar = findViewById<ListView>(R.id.listViewCalendar)
        val tvAverage = findViewById<TextView>(R.id.averageSleep)
        val navHome = findViewById<Button>(R.id.navHome)
        val navCalendar = findViewById<Button>(R.id.navCalendar)
        val navAverage = findViewById<Button>(R.id.navAverage)

        // 2. Setup Dark / Light Theme Toggle Logic with Sun / Moon Icon
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            btnThemeToggle.setImageResource(R.drawable.ic_sun)
            btnThemeToggle.contentDescription = getString(R.string.light_mode)
        } else {
            btnThemeToggle.setImageResource(R.drawable.ic_moon)
            btnThemeToggle.contentDescription = getString(R.string.dark_mode)
        }

        btnThemeToggle.setOnClickListener {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        // 3. Setup ListView Adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entriesList)
        listViewCalendar.adapter = adapter

        // 4. Set Date (Initial or Restored)
        val cal = Calendar.getInstance()
        var year = cal[Calendar.YEAR]
        var month = cal[Calendar.MONTH]
        var day = cal[Calendar.DAY_OF_MONTH]

        if (selectedDate.isEmpty()) {
            selectedDate = "$day/${month + 1}/$year"
        }
        btnDate.text = getString(R.string.date_format, selectedDate)
        tvNumber.text = number.toString()

        // Helper to update average rating text
        fun updateAverage() {
            if (ratingsList.isEmpty()) {
                tvAverage.text = getString(R.string.default_average)
            } else {
                val avg = ratingsList.average()
                tvAverage.text = String.format(Locale.US, "%.1f", avg)
            }
        }

        updateAverage()

        // 5. Rating Buttons (- and +)
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

        // 6. Date Picker Dialog
        btnDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, sYear, sMonth, sDay ->
                    year = sYear
                    month = sMonth
                    day = sDay
                    selectedDate = "$day/${month + 1}/$year"
                    btnDate.text = getString(R.string.date_format, selectedDate)
                    if (savedDatesSet.contains(selectedDate)) {
                        Toast.makeText(
                            this,
                            getString(R.string.date_already_exists, selectedDate),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
                year,
                month,
                day,
            ).show()
        }

        // 7. Save Entry
        btnSave.setOnClickListener {
            if (savedDatesSet.contains(selectedDate)) {
                Toast.makeText(
                    this,
                    getString(R.string.date_already_exists, selectedDate),
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }

            val entryText = getString(R.string.entry_format, selectedDate, number)
            entriesList.add(entryText)
            ratingsList.add(number)
            savedDatesSet.add(selectedDate)
            adapter.notifyDataSetChanged()
            updateAverage()
            tvEmpty.visibility = View.GONE
            Toast.makeText(this, getString(R.string.added_to_calendar), Toast.LENGTH_SHORT).show()
        }

        // 8. Tab Navigation Function
        fun showTab(tab: Int) {
            activeTab = tab
            layoutHome.visibility = if (tab == TAB_HOME) View.VISIBLE else View.GONE
            layoutCalendar.visibility = if (tab == TAB_CALENDAR) View.VISIBLE else View.GONE
            layoutAverage.visibility = if (tab == TAB_AVERAGE) View.VISIBLE else View.GONE

            if (tab == TAB_CALENDAR) {
                tvEmpty.visibility = if (entriesList.isEmpty()) View.VISIBLE else View.GONE
            } else if (tab == TAB_AVERAGE) {
                updateAverage()
            }
        }

        // Restore visible tab layout
        showTab(activeTab)

        navHome.setOnClickListener { showTab(TAB_HOME) }
        navCalendar.setOnClickListener { showTab(TAB_CALENDAR) }
        navAverage.setOnClickListener { showTab(TAB_AVERAGE) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("KEY_NUMBER", number)
        outState.putString("KEY_DATE", selectedDate)
        outState.putStringArrayList("KEY_ENTRIES", entriesList)
        outState.putIntegerArrayList("KEY_RATINGS", ratingsList)
        outState.putInt("KEY_ACTIVE_TAB", activeTab)
    }
}
