package org.wit.assignment.models

interface ReminderStore {
    fun findAll(): List<ReminderModel>
    fun create(reminder: ReminderModel)
    fun update(reminder: ReminderModel)
    fun delete(reminder: ReminderModel)
}