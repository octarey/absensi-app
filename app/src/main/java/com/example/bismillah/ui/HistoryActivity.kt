package com.example.bismillah.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bismillah.R
import com.example.bismillah.UserIn
import com.example.bismillah.UserOut
import com.example.bismillah.databinding.ActivityHistoryBinding
import com.example.bismillah.ui.adapters.AbsentAdapter
import com.example.bismillah.ui.utils.Helper
import com.example.bismillah.viewmodel.AbsentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyBinding: ActivityHistoryBinding
    private lateinit var absentViewModel: AbsentViewModel
    private lateinit var absentAdapter: AbsentAdapter

    private lateinit var tvDate: TextView
    private lateinit var rvAbsent: RecyclerView
    private lateinit var onTimeLabel : TextView
    private lateinit var lateLabel : TextView
    private lateinit var btnBack : ImageButton

    private var listIn : ArrayList<UserIn> = arrayListOf()
    private var listOut :ArrayList<UserOut> = arrayListOf()

    private val MY_STORAGE_PERMISSION_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyBinding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(historyBinding.root)
        supportActionBar?.hide()

        init()

        listIn = intent.getSerializableExtra("listIn") as ArrayList<UserIn>
        listOut = intent.getSerializableExtra("listOut") as ArrayList<UserOut>

        absentAdapter.setAbsentList(listIn,listOut)
        println("pap history in $listIn ${listIn.size}")
        println("pap history out $listOut")

        historyUI()
        btnBack.setOnClickListener { onBackPressed() }
        tvDate.setOnClickListener {
            Toast.makeText(this, "Fitur filer riwayat absensi", Toast.LENGTH_SHORT).show()
        }

        historyBinding.historyPrintBtn.setOnClickListener {
            checkStoragePermisson(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_STORAGE_PERMISSION_CODE)
        }

    }

    private fun init(){
        absentViewModel = ViewModelProvider(this)[AbsentViewModel::class.java]
        tvDate = historyBinding.historyDateValue
        onTimeLabel = historyBinding.historyFooterOn
        lateLabel = historyBinding.historyFooterLate
        btnBack = historyBinding.historyBackBtn

        rvAbsent = historyBinding.historyRvAbsent
        absentAdapter = AbsentAdapter()
    }

    private fun historyUI(){
        tvDate.text = "Riwayat Absensi " + Helper.monthSting()
        onTimeLabel.text = "On Time " + listIn.count() {it.statusIn == "OK"}.toString() + " hari"
        lateLabel.text = "Terlambat " + listIn.count() {it.statusIn == "NOK"}.toString() + " hari"

        rvAbsent.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity ,LinearLayoutManager.VERTICAL,false)
            adapter = absentAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun printAbsent(){
        val date = Helper.monthSting()
        Toast.makeText(this, "Export data absensi, harap tunggu...", Toast.LENGTH_SHORT).show()
        val hssfWorkbook = HSSFWorkbook()
        val hssfSheet = hssfWorkbook.createSheet("Custom Sheet")
        //---------------
        //---------------
        val hssfRowMainTitle = hssfSheet.createRow(0)
        hssfRowMainTitle.createCell(0).setCellValue("Absensi $date")
        //-------------
        //-------------
        val hssfRowTitle = hssfSheet.createRow(1)
        hssfRowTitle.createCell(0).setCellValue("tanggal")
        hssfRowTitle.createCell(1).setCellValue("jam masuk")
        hssfRowTitle.createCell(2).setCellValue("lokasi masuk")
        hssfRowTitle.createCell(3).setCellValue("jam pulang")
        hssfRowTitle.createCell(4).setCellValue("lokasi pulang")
        hssfRowTitle.createCell(5).setCellValue("status")
        //--------------
        //--------------
        var row = 2
        var index = 0
        for (a in listOut) {
                val hssfRow = hssfSheet.createRow(row)
                val listIn = listIn as ArrayList<UserIn>
                val status = if (listIn[index].statusIn.equals("OK")) "On Time" else "Terlambat"
                hssfRow.createCell(0).setCellValue(listIn[index].date)
                hssfRow.createCell(1).setCellValue(listIn[index].timeIn)
                hssfRow.createCell(2).setCellValue(listIn[index].locIn.toString())
                hssfRow.createCell(3).setCellValue(a.timeOut)
                hssfRow.createCell(4).setCellValue(a.locOut.toString())
                hssfRow.createCell(5).setCellValue(status)
                row++
                index++
        }
        //---------
        //---------
        val path: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + File.separator)
        val file = File(path.toString())
        file.mkdirs()
        val fileName: String =
            path.toString() + "/" + "absensi" + System.currentTimeMillis() + ".xls"
        try {
            val fileOutputStream = FileOutputStream(fileName)
            hssfWorkbook.write(fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            Toast.makeText(this, "Export berhasil, cek file di storage anda", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.d("pap gagal print", e.toString())
            e.printStackTrace()
        }
    }

    private fun checkStoragePermisson(permission : String, requestCode: Int){
        if (ContextCompat.checkSelfPermission(this ,permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this  , arrayOf(permission), requestCode)
        }else {
            printAbsent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                printAbsent()
            }else {
                Toast.makeText(
                    this,
                    "Izinkan aplikasi mengakses storage",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}