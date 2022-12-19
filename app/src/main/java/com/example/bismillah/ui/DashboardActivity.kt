package com.example.bismillah.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import coil.api.clear
import com.example.bismillah.UserIn
import com.example.bismillah.UserOut
import com.example.bismillah.databinding.ActivityDashboardBinding
import com.example.bismillah.ui.utils.Helper
import com.example.bismillah.viewmodel.AbsentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.collections.ArrayList

class DashboardActivity : AppCompatActivity() {
    private lateinit var dashboardBinding: ActivityDashboardBinding
    private lateinit var absentViewModel: AbsentViewModel
    private lateinit var nameTxt : TextView
    private lateinit var positionTxt : TextView
    private lateinit var reportHeader : TextView
    private lateinit var reportAll : TextView
    private lateinit var reportOn : TextView
    private lateinit var reportLate : TextView
    private lateinit var inTime : TextView
    private lateinit var inLocation : TextView
    private lateinit var outTime : TextView
    private lateinit var outLocation : TextView
    private lateinit var greetingTXt : TextView
    private lateinit var historyBtn : TextView
    private lateinit var confirmBtn : Button

    private lateinit var absentHeader_txt : TextView
    private lateinit var absentTime_txt : TextView
    private lateinit var absentLoc_txt : TextView
    private lateinit var absentPhoto_btn : ImageButton
    private lateinit var absentPhoto_img : ImageView
    private lateinit var absentConfirm_btn : Button
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var listIn : ArrayList<UserIn> = arrayListOf()
    private var listOut : ArrayList<UserOut> = arrayListOf()
    private var action : String = ""
    private var current : String = ""
    private var lastIn : UserIn? = null
    private var lastOut : UserOut? = null
    private var distance = 0
    private var jam_masuk = ""

    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val MY_LOCATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)
        supportActionBar?.hide()

        init()
        getAbsent()
        refreshAbsent()

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        absentTime_txt.text = "-"
                        absentLoc_txt.text = "-"
                        jam_masuk = ""
                        distance = 0
                        absentPhoto_img.setImageBitmap(null)
                        dashboardBinding.absentInfo.visibility = View.VISIBLE
                        absentPhoto_btn.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })
        
        historyBtn.setOnClickListener {
            val intent = Intent(this,HistoryActivity::class.java)
            intent.putExtra("listIn", listIn)
            intent.putExtra("listOut", listOut)
            startActivity(intent)
        }
        confirmBtn.setOnClickListener {
            checkCameraPermisson(Manifest.permission.ACCESS_FINE_LOCATION, MY_LOCATION_PERMISSION_CODE)
        }

        dashboardBinding.absentCloseBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        absentPhoto_btn.setOnClickListener {
            checkCameraPermisson(Manifest.permission.CAMERA, MY_CAMERA_PERMISSION_CODE)
        }

        absentConfirm_btn.setOnClickListener {
            if (jam_masuk == ""){
                Toast.makeText(this, "Silahkan swafoto untuk absensi", Toast.LENGTH_SHORT).show()
            }else{
                sendAbsent()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun init(){
        //inisialisasi view dashboard activity
        absentViewModel = ViewModelProvider(this)[AbsentViewModel::class.java]
        bottomSheetBehavior = BottomSheetBehavior.from(dashboardBinding.absentFragment)
        nameTxt = dashboardBinding.dashboardUserName
        positionTxt = dashboardBinding.dashboardUserPosition
        reportHeader = dashboardBinding.dashboardReportHeader
        reportAll = dashboardBinding.dashboardReportAll
        reportOn = dashboardBinding.dashboardReportOn
        reportLate = dashboardBinding.dashboardReportLate
        inTime = dashboardBinding.dashboardInTime
        inLocation = dashboardBinding.dashboardInLocation
        outTime = dashboardBinding.dashboardOutTime
        outLocation = dashboardBinding.dashboardOutLocation
        greetingTXt = dashboardBinding.dashboardGreetingTxt

        historyBtn = dashboardBinding.dashboardHistoryBtn
        confirmBtn = dashboardBinding.dashboardConfirmBtn

        //inisilisasi view absent modal
        absentHeader_txt = dashboardBinding.absentHeaderLabel
        absentTime_txt = dashboardBinding.absentTimeValue
        absentLoc_txt = dashboardBinding.absentLocValue
        absentPhoto_btn = dashboardBinding.absentPhotoBtn
        absentPhoto_img = dashboardBinding.absentImage
        absentConfirm_btn = dashboardBinding.absentConfirmBtn

        current = Helper.dateString()
    }

    private fun getAbsent(){
        if (listIn.isEmpty()){
            absentViewModel.getAbsentsIn()
        }

        if (listOut.isEmpty()){
            absentViewModel.getAbsentsOut()
        }
    }

    private fun refreshAbsent(){
        absentViewModel.observerAbsentOutLiveData().observe(this, Observer {
            listOut = it
            dashboardUI()
            println("pap out dari view model$listOut")
        })

        absentViewModel.observerAbsentInLiveData().observe(this, Observer {
            listIn = it
            dashboardUI()
            println("pap in dari view model$listIn")
        })
    }

    @SuppressLint("SetTextI18n")
    private fun dashboardUI(){
        val username = intent.getStringExtra("name").toString()
        val position = intent.getStringExtra("position").toString()
        val absentAll = listIn.size
        val absentOntime = listIn.count() { it.statusIn == "OK" }
        val absentLate = listIn.count() { it.statusIn == "NOK" }

        println("pap report ${listIn.size}")

        nameTxt.text = username
        positionTxt.text = position
        val today = Helper.dateIndo()
        reportHeader.text = "Laporan Absensi $today"
        reportAll.text = absentAll.toString()
        reportOn.text = absentOntime.toString()
        reportLate.text = absentLate.toString()

        //cek absensi hari ini
        //current = "15-12-2022"
        if (listIn.none { it.date == current }){
            lastIn = null
            confirmBtn.text = "ABSEN MASUK"
            absentConfirm_btn.text = "ABSEN MASUK"
            absentHeader_txt.text = "FORM ABSENSI MASUK"
            action = "masuk"
        }else{
            lastIn = listIn.single { it.date == current }

            inTime.text = "${lastIn?.timeIn} WIB"
            inLocation.text = "${lastIn?.locIn} Km dari kantor"
            confirmBtn.text = "ABSEN PULANG"
            absentConfirm_btn.text = "ABSEN PULANG"
            absentHeader_txt.text = "FORM ABSENSI PULANG"
            action = "pulang"
        }

        //cek check out hari ini
        if (listOut.none { it.date == current }){
            lastOut = null
        }else{
            lastOut = listOut.single { it.date == current }

            outTime.text = "${lastOut?.timeOut} WIB"
            outLocation.text = "${lastOut?.locOut} Km dari kantor"
            confirmBtn.visibility = View.GONE
            greetingTXt.visibility = View.VISIBLE
            action = ""
        }
        println("pap $lastIn $lastOut")
    }
    
    private fun sendAbsent(){
        println("pap data absensi $action $current $jam_masuk $distance")

        if (action == "masuk"){
            absentViewModel.sendAbsentIn(current , jam_masuk, distance)
            refreshAbsent()
        }else if (action == "pulang"){
            absentViewModel.sendAbsentOut(current , jam_masuk, distance)
            refreshAbsent()
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        Handler().postDelayed({
            Toast.makeText(this, "Absensi Berhasil", Toast.LENGTH_SHORT).show()
        }, 1000)
    }

    fun absentModalUI(){

        //get jam sekarang
        jam_masuk= Helper.timeString()

        ////update UI absen modal
        absentViewModel.observerDistance().observe(this, Observer {
            distance = it
            dashboardBinding.absentInfo.visibility = View.GONE
            absentTime_txt.text = "$jam_masuk WIB"
            absentLoc_txt.text = "$distance Km dari kantor"
        })

    }

    private fun checkCameraPermisson(permission : String, requestCode: Int){
        if (ContextCompat.checkSelfPermission(this ,permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this  , arrayOf(permission), requestCode)
        }else {
            if (requestCode == MY_CAMERA_PERMISSION_CODE) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }else if (requestCode == MY_LOCATION_PERMISSION_CODE) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }else {
                Toast.makeText(
                    this,
                    "Izinkan aplikasi mengakses kamera",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else if (requestCode == MY_LOCATION_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }else {
                Toast.makeText(
                    this,
                    "Izinkan aplikasi mengakses lokasi",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CAMERA_REQUEST) {
                val photo = data!!.extras!!.get("data") as Bitmap?
                photo?.let {
                    absentPhoto_btn.visibility = View.GONE
                    absentPhoto_img.setImageBitmap(it)
                    //get location sekarang
                    absentViewModel.getUserLocation(this)
                    absentModalUI()
                }
            }
        }
    }
}