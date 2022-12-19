package com.example.bismillah.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.bismillah.R
import com.example.bismillah.UserData
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var usernameField : EditText
    private lateinit var passwordField : EditText
    private lateinit var loginBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        init()

        loginBtn.setOnClickListener { login() }
    }

    private fun init(){
        usernameField = findViewById(R.id.main_usernameField)
        passwordField = findViewById(R.id.main_passwordField)
        loginBtn = findViewById(R.id.main_loginBtn)
    }

    private fun login(){
        val username = usernameField.text.toString()
        val password = passwordField.text.toString()
        if (username == UserData.username && password == UserData.password){
            //erroret("Login Successfully", R.color.success)
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("name", UserData.name)
            intent.putExtra("position", UserData.position)
            startActivity(intent)
            finish()
        }else {
            erroret("Login Failed, Check your input", R.color.error)
        }
    }

    private fun erroret(msg:String, color:Int){
        val snackbar = Snackbar.make(findViewById(R.id.main_container), msg, Snackbar.LENGTH_LONG)
        snackbar.view.minimumHeight = 20
        snackbar.view.setBackgroundResource(color)
        snackbar.setTextColor(Color.BLACK)
        snackbar.show()
    }
}