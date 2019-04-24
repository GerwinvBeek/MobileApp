package org.wit.assignment.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.assignment.R
import org.wit.assignment.main.MainApp
import org.wit.assignment.models.Location
import org.wit.assignment.models.ReminderModel

import java.text.SimpleDateFormat


class ReminderActivity : AppCompatActivity(){

    var edit = false;
    var reminder = ReminderModel()
    lateinit var app: MainApp

    val LOCATION_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = application as MainApp

        toolbarAdd.title = title
        calendarView.minDate = System.currentTimeMillis()
        setSupportActionBar(toolbarAdd)

        if (intent.hasExtra("reminder_edit")) {
            edit = true
            btnAdd.setText(R.string.save_reminder)
            reminder = intent.extras.getParcelable<ReminderModel>("reminder_edit")
            reminderTitle.setText(reminder.title)
            description.setText(reminder.description)
            calendarView.date = reminder.deadline
            priority.setText(reminder.priority)
        }

        btnAdd.setOnClickListener() {
            reminder.title = reminderTitle.text.toString()
            reminder.description = description.text.toString()
            reminder.priority = priority.text.toString()

            if (reminder.zoom == 0f) {
               var location = Location(52.245696, -7.139102, 15f)
                reminder.lat = location.lat
                reminder.lng = location.lng
                reminder.zoom = location.zoom
            }

            if (reminder.title.isEmpty()) {
                toast(R.string.enter_reminder_title)
            } else {
                if (edit) {
                    app.reminders.update(reminder.copy())
                } else {
                    app.reminders.create(reminder.copy())
                }
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }
        }

        reminderLocation.setOnClickListener {
            reminderLocation.setOnClickListener {
                val location = Location(52.245696, -7.139102, 15f)
                if (reminder.zoom != 0f) {
                    location.lat =  reminder.lat
                    location.lng = reminder.lng
                    location.zoom = reminder.zoom
                }
                startActivityForResult(intentFor<MapsActivity>().putExtra("location", location), LOCATION_REQUEST)
            }
        }

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
            var newMonth = month
            newMonth++
            val newDate = "" + dayOfMonth + "-" + newMonth + "-" + year + " 00:00:00"
            val date = sdf.parse(newDate)

            reminder.deadline = date.time
        }
    }
        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(org.wit.assignment.R.menu.menu_reminder, menu)
            if (edit && menu != null) menu.getItem(0).setVisible(true)
            return super.onCreateOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.item_delete -> {
                    app.reminders.delete(reminder)
                    finish()
                }
                R.id.item_cancel -> {
                    finish()
                }
            }
            return super.onOptionsItemSelected(item)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOCATION_REQUEST -> {
                if (data != null) {
                    var location = data.extras.getParcelable<Location>("location")
                    reminder.lat = location.lat
                    reminder.lng = location.lng
                    reminder.zoom = location.zoom
                    }
                }
            }
        }


    }


