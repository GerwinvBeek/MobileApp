package org.wit.assignment.models

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class ReminderMemStore : ReminderStore {

    val reminders = ArrayList<ReminderModel>()

    override fun findAll(): List<ReminderModel> {
        return reminders
    }

    /**
     * Add reminder to memory
     */
    override fun create(placemark: ReminderModel) {
        placemark.id = getId()
        reminders.add(placemark)
    }

    /**
     * Update changed reminder in memory
     */
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
    }

    /**
     * Delete reminder from memory
     */
    override fun delete(reminder: ReminderModel) {
        reminders.remove(reminder)
    }
}