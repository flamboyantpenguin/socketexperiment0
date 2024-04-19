package com.example.socketexperiment0.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socketexperiment0.MainActivity
import com.example.socketexperiment0.R

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        supportActionBar?.hide()
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener{exitAbout()}
    }

    private fun exitAbout() {
        val i = Intent(this@About, MainActivity::class.java)
        startActivity(i)
    }
}