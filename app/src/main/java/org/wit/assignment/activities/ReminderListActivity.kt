package org.wit.assignment.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_reminder_list.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.wit.assignment.R
import org.wit.assignment.helpers.ReminderAdapter
import org.wit.assignment.helpers.ReminderListener
import org.wit.assignment.main.MainApp
import org.wit.assignment.models.ReminderModel


class ReminderListActivity : AppCompatActivity(), ReminderListener {

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)
        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        loadReminders()

        toolbarMain.title = title
        setSupportActionBar(toolbarMain)

        removeAllButton.setOnClickListener { onRemoveAllClick() }
        refreshButton.setOnClickListener { onRefreshClick() }

        sorting_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 > 0) {
                    val reminders = app.reminders.findAll()

                    if (p2 == 1) {
                        reminders.sortedBy { x -> x.deadline }.forEach {
                            app.reminders.delete(it)
                            app.reminders.create(it) }
                    }
                    if (p2 == 2) {
                        reminders.sortedBy { x -> x.done }.forEach {
                            app.reminders.delete(it)
                            app.reminders.create(it) }
                    }
                    if (p2 == 3) {
                        reminders.sortedBy { x -> x.id }.forEach {
                            app.reminders.delete(it)
                            app.reminders.create(it) }
                    }
                    if (p2 == 4) {
                        reminders.sortedBy { x -> x.priority }.reversed().forEach {
                            app.reminders.delete(it)
                            app.reminders.create(it) }
                    }
                    loadReminders()
                }
            }
        }
    }

    fun onRefreshClick(){
        val reminders = app.reminders.findAll()

        val sorting = sorting_spinner.selectedItem.toString()

        if (sorting == "Sort by deadline") {
            reminders.sortedBy { x -> x.deadline }.forEach {
                app.reminders.delete(it)
                app.reminders.create(it) }
        }
        if (sorting == "Sort by status") {
            reminders.sortedBy { x -> x.done }.forEach {
                app.reminders.delete(it)
                app.reminders.create(it) }
        }
        if (sorting == "Sort by newest") {
            reminders.sortedBy { x -> x.id }.forEach {
                app.reminders.delete(it)
                app.reminders.create(it) }
        }
        if (sorting == "Sort by priority") {
            reminders.sortedBy { x -> x.priority }.reversed().forEach {
                app.reminders.delete(it)
                app.reminders.create(it) }
        }
        loadReminders()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_add -> startActivityForResult<ReminderActivity>(0)
            R.id.item_map -> startActivity<ReminderMapsActivity>()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onReminderClick(reminder: ReminderModel) {
        startActivityForResult(intentFor<ReminderActivity>().putExtra("reminder_edit", reminder), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadReminders()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadReminders() {
        showReminders(app.reminders.findAll())
    }

    fun showReminders (reminders: List<ReminderModel>) {
        recyclerView.adapter = ReminderAdapter(reminders, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCheckboxChange(reminder: ReminderModel) {
        reminder.done = !reminder.done
        app.reminders.update(reminder.copy())
        loadReminders()
    }

    fun onRemoveAllClick() {
        var reminders = app.reminders.findAll().filter { x -> x.done == true }
        reminders.forEach{app.reminders.delete(it)}
        loadReminders()
    }
}