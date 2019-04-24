package org.wit.assignment.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.assignment.models.ReminderJSONStore
import org.wit.assignment.models.ReminderStore

class MainApp: Application(), AnkoLogger {

    lateinit var reminders: ReminderStore

    override fun onCreate() {
        super.onCreate()
//        reminders = ReminderMemStore()
        reminders = ReminderJSONStore(applicationContext)
        info("Application Started")
    }

}