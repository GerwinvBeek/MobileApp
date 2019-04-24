package org.wit.assignment.helpers

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.reminder_cards.view.*
import org.wit.assignment.R
import org.wit.assignment.models.ReminderModel

class ReminderAdapter constructor(private var reminders: List<ReminderModel>,
                                  private val listener: ReminderListener
) : RecyclerView.Adapter<ReminderAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent?.context).inflate(
                R.layout.reminder_cards,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val reminder = reminders[holder.adapterPosition]
        holder.bind(reminder, listener)
    }

    override fun getItemCount(): Int = reminders.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(reminder: ReminderModel, listener : ReminderListener) {
            itemView.reminderTitle.text = reminder.title
            itemView.description.text = reminder.description
            itemView.checkBox.isChecked = reminder.done
            itemView.setOnClickListener { listener.onReminderClick(reminder) }
            itemView.checkBox.setOnCheckedChangeListener { buttonView, isChecked -> listener.onCheckboxChange(reminder) }

            if(reminder.done){
                itemView.setBackgroundColor(Color.LTGRAY)
            }
        }
    }
}
interface ReminderListener {
    fun onReminderClick(reminder: ReminderModel)
    fun onCheckboxChange(reminder: ReminderModel)
}