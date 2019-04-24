package org.wit.assignment.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReminderModel(var id: Long = 0,
                         var title: String = "",
                         var description: String = "",
                         var image: String = "",
                         var lat : Double = 0.0,
                         var lng: Double = 0.0,
                         var zoom: Float = 0f,
                         var deadline: Long = System.currentTimeMillis(),
                         var done: Boolean = false,
                        var priority: String = "1") : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable