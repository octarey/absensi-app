package com.example.bismillah.ui.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

object Helper {

    fun dateIndo(): String {
        return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id")).format(Date()).toString()
    }

    fun monthSting():String {
        return SimpleDateFormat("MMMM yyyy", Locale("id")).format(Date()).toString()
    }

    fun dateString():String {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        return formatter.format(Date()).toString()
    }

    fun timeString():String {
        val time_string = SimpleDateFormat("HH:mm")
        return time_string.format(Date()).toString()
    }


}