package com.example.elderlycareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {


    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginbutton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)


        // Skip login if already logged in
        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        setContentView(R.layout.activity_login)


        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginbutton = findViewById(R.id.loginbutton)


        loginbutton.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()


            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Normally you'd verify credentials from database/server
                val editor = prefs.edit()
                editor.putBoolean("is_logged_in", true)
                editor.putString("username", username)
                editor.apply()


                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
