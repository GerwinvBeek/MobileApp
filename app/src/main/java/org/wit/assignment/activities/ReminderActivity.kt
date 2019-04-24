package org.wit.assignment.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.assignment.R
import org.wit.assignment.main.MainApp
import org.wit.assignment.models.Location
import org.wit.assignment.models.ReminderModel

import java.text.SimpleDateFormat


class ReminderActivity : AppCompatActivity() {

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

        /**
         * if user is editing an item, apply values of selected reminder to corresponding fields
         */
        if (intent.hasExtra("reminder_edit")) {
            edit = true
            btnAdd.setText(R.string.save_reminder)
            reminder = intent.extras.getParcelable<ReminderModel>("reminder_edit")
            reminderTitle.setText(reminder.title)
            description.setText(reminder.description)
            calendarView.date = reminder.deadline
            priorityBar.rating = reminder.priority.toFloat()
        }

        /**
         * Add Click Listener to Add button
         * Applies chosen values to reminder object
         */
        btnAdd.setOnClickListener() {
            reminder.title = reminderTitle.text.toString()
            reminder.description = description.text.toString()
            reminder.priority = priorityBar.rating.toLong()

            /**
             * If user didn't choose location, use default location.
             */
            if (reminder.zoom == 0f) {
                var location = Location(52.245696, -7.139102, 15f)
                reminder.lat = location.lat
                reminder.lng = location.lng
                reminder.zoom = location.zoom
            }

            /**
             * Small validation to check if user filled in a title with at least 4 characters.
             * If title is correct, bigger validation on non-breaking values
             */
            if (reminder.title.isEmpty()) {
                toast(R.string.enter_reminder_title)
            } else if(reminder.title.length < 4){
                toast(R.string.title_too_short)
            } else {
                validateDescription()
            }
            }

        /**
         * Open the mapactivity and set default location
         */
        reminderLocation.setOnClickListener {
            reminderLocation.setOnClickListener {
                val location = Location(52.245696, -7.139102, 15f)
                if (reminder.zoom != 0f) {
                    location.lat = reminder.lat
                    location.lng = reminder.lng
                    location.zoom = reminder.zoom
                }
                startActivityForResult(intentFor<MapsActivity>().putExtra("location", location), LOCATION_REQUEST)
            }
        }

        /**
         * Set new date from datepicker to correct format
         */
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
            var newMonth = month
            newMonth++
            val newDate = "" + dayOfMonth + "-" + newMonth + "-" + year + " 00:00:00"
            val date = sdf.parse(newDate)

            reminder.deadline = date.time
        }
    }

    /**
     * Validation if description is filled in
     */
    fun validateDescription(){
        if (reminder.description.isEmpty()) {
            val builder = AlertDialog.Builder(this@ReminderActivity)
            builder.setTitle("Empty/Default Value")
            builder.setMessage("Description is empty, are you sure you want to continue?")
            builder.setPositiveButton("Yes") { dialog, which ->
                validateLocation()
            }
            builder.setNeutralButton("No") { dialog, which ->
                toast("Canceled")
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
            validateLocation()
        }
    }

    /**
     * Validation to check if the location is changed or that the user is using the default location
     */
    fun validateLocation(){
        if (reminder.lat == 52.245696 && reminder.lng == -7.139102 && reminder.zoom == 15f) {
            val builder = AlertDialog.Builder(this@ReminderActivity)
            builder.setTitle("Empty/Default Value")
            builder.setMessage("Location is default locaiton, are you sure you want to continue?")
            builder.setPositiveButton("Yes") { dialog, which ->
                /**
                 * Save reminder
                 */
                if (edit) {
                    app.reminders.update(reminder.copy())
                } else {
                    app.reminders.create(reminder.copy())
                }
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }
            builder.setNeutralButton("No") { dialog, which ->
                toast("Canceled")
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
            /**
             * Save reminder
             */
            if (edit) {
                app.reminders.update(reminder.copy())
            } else {
                app.reminders.create(reminder.copy())
            }
            setResult(AppCompatActivity.RESULT_OK)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(org.wit.assignment.R.menu.menu_reminder, menu)
        if (edit && menu != null) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Add listeners to items in actionbar (delete, cancel and share)
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_delete -> {
                app.reminders.delete(reminder)
                finish()
            }
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_share -> {
                shareReminder()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Add chosen location to reminder
     */
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

    /**
     * Create sharingintent and add body to the created intent
     */
    fun shareReminder() {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.setType("text/plain");
        val sdf = SimpleDateFormat("dd-M-yyyy")
        val date = sdf.format(reminder.deadline)
        val shareBody =
                "Title: " + reminder.title + "\n" +
                "Description: " + reminder.description + "\n" +
                "Finished: " + reminder.done + "\n" +
                "Deadline: " + date + "\n" +
                "Priority: " + reminder.priority

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Reminder");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}



