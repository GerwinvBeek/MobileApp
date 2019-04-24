package org.wit.assignment.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.wit.assignment.helpers.exists
import org.wit.assignment.helpers.read
import org.wit.assignment.helpers.write
import java.util.*

val JSON_FILE = "placemarks.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<java.util.ArrayList<ReminderModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class ReminderJSONStore : ReminderStore, AnkoLogger {

    val context: Context
    var reminders = mutableListOf<ReminderModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<ReminderModel> {
        return reminders
    }

    override fun create(reminder: ReminderModel) {
        reminder.id = generateRandomId()
        reminders.add(reminder)
        serialize()
    }


    override fun update(reminder: ReminderModel) {
        var foundReminder: ReminderModel? = reminders.find { p -> p.id == reminder.id }
            if (foundReminder != null) {
                foundReminder.title = reminder.title
                foundReminder.description = reminder.description
                foundReminder.lat = reminder.lat
                foundReminder.lng = reminder.lng
                foundReminder.zoom = reminder.zoom
                foundReminder.done = reminder.done
                foundReminder.deadline = reminder.deadline
                foundReminder.priority = reminder.priority
            }
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(reminders, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        reminders = Gson().fromJson(jsonString, listType)
    }

    override fun delete(reminder: ReminderModel) {
        reminders.remove(reminder)
        serialize()
    }
}