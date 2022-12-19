package com.example.bismillah.viewmodel

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bismillah.UserData
import com.example.bismillah.UserIn
import com.example.bismillah.UserOut
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AbsentViewModel: ViewModel() {
    private var absentIntLiveData = MutableLiveData<ArrayList<UserIn>>()
    private var absentOutLiveData = MutableLiveData<ArrayList<UserOut>>()
    private var distanceLiveData = MutableLiveData<Int>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val simpleDateFormat = SimpleDateFormat("HH:mm")
    private val jam_masuk = simpleDateFormat.parse("08:00")
    private val jam_pulang = simpleDateFormat.parse("17:00")

    fun getAbsentsIn() {
        absentIntLiveData.value = UserData.absensiData.listIn
    }

    fun getAbsentsOut() {
        absentOutLiveData.value = UserData.absensiData.listOut
    }

    fun sendAbsentIn(date:String, time: String, loc : Int){
        val user_masuk = simpleDateFormat.parse(time)
        val new = UserIn()
        new.date = date
        new.timeIn = time
        new.locIn = loc
        new.statusIn = if (user_masuk.before(jam_masuk)) "OK" else "NOK"
        absentIntLiveData.value?.add(new)
    }

    fun sendAbsentOut(date:String, time: String, loc : Int){
        val user_masuk = simpleDateFormat.parse(time)
        val new = UserOut()
        new.date = date
        new.timeOut = time
        new.locOut = loc
        new.statusOut = if (user_masuk.after(jam_pulang)) "OK" else "NOK"
        absentOutLiveData.value?.add(new)
    }

    fun getUserLocation(mContext: Context){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        var office = Location("office")
        office.latitude = -7.9723572
        office.longitude = 112.6292202
//        office.latitude = -7.8665146
//        office.longitude = 112.6802038
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(mContext, "Gagal mendapatkan lokasi" , Toast.LENGTH_SHORT).show()
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    // use your location object
                    // get latitude , longitude and other info from this
                    distanceLiveData.value = location.distanceTo(office).toInt()/1000
                    Log.d("pap", "${location.latitude} ${location.longitude} ${distanceLiveData.value}")
                }

            }
    }

    fun observerAbsentInLiveData(): LiveData<ArrayList<UserIn>> {
        return absentIntLiveData
    }

    fun observerAbsentOutLiveData(): LiveData<ArrayList<UserOut>> {
        return absentOutLiveData
    }

    fun observerDistance(): LiveData<Int> {
        return distanceLiveData
    }
}