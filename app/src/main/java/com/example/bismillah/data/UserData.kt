package com.example.bismillah

import java.io.Serializable

class UserData {
    companion object userInfo {
        val name = "Octa Reyzaldy"
        val position = "Android Developer"
        val username = "user1"
        val password = "123456"
        //status berisi WFH, WFO, JALDIS, CUTI
        val status = "WFO"
    }

    object absensiData{

        private val absentDate = arrayOf(
            "01-12-2022",
            "02-12-2022",
            "05-12-2022",
            "06-12-2022",
            "07-12-2022",
            "08-12-2022",
            "09-12-2022",
            "12-12-2022",
            "13-12-2022",
            "14-12-2022",
        )

        private val checkinTime = arrayOf(
            "07:30",
            "07:32",
            "07:30",
            "07:45",
            "07:58",
            "07:50",
            "08:05",
            "08:00",
            "07:59",
            "07:30",
        )

        private val checkinLoc = arrayOf(
            0,0,0,0,0,12,0,0,0,0
        )

        private val checkinStatus = arrayOf(
            "OK","OK","OK","OK","OK","OK","NOK","OK","OK","OK"
        )

        private val checkoutLoc = arrayOf(
            0,0,0,0,0,12,0,0,0,0
        )

        private val checkoutTime = arrayOf(
            "17:02",
            "17:01",
            "17:01",
            "17:05",
            "17:00",
            "17:13",
            "17:05",
            "17:00",
            "17:19",
            "17:05"
        )

        private val checkoutStatus = arrayOf(
            "OK","OK","OK","OK","OK","OK","OK","OK","OK","OK"
        )

        val listIn : ArrayList<UserIn>
            get() {
                val list = arrayListOf<UserIn>()
                for (position in checkinTime.indices){
                    val absensi = UserIn()
                    absensi.date = absentDate[position]
                    absensi.timeIn = checkinTime[position]
                    absensi.locIn = checkinLoc[position]
                    absensi.statusIn = checkinStatus[position]
                    list.add(absensi)
                }
                return list
            }

        val listOut : ArrayList<UserOut>
            get() {
                val list = arrayListOf<UserOut>()
                for (position in checkoutTime.indices){
                    val absensi = UserOut()
                    absensi.date = absentDate[position]
                    absensi.timeOut = checkoutTime[position]
                    absensi.locOut = checkoutLoc[position]
                    absensi.statusOut = checkoutStatus[position]
                    list.add(absensi)
                }
                return list
            }
    }

}

data class UserIn(
    var date : String = "",
    var timeIn : String = "",
    var locIn : Int = 0,
    var statusIn : String = "",
):Serializable

data class UserOut(
    var date : String = "",
    var timeOut : String? = "",
    var locOut : Int? = 0,
    var statusOut : String? = ""
):Serializable