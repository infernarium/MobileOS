package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity



class menu : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var wins: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        wins = findViewById<EditText>(R.id.textView9)
        prefs = getSharedPreferences("opts", Context.MODE_PRIVATE)
        wins.setText(prefs.getInt("wins", 0).toString())
    }

    override fun onResume() {
        super.onResume()
        wins.setText(prefs.getInt("wins", 0).toString())
    }

    fun toGame(view: View) {
        val intent = Intent(this, game::class.java)
        startActivity(intent)
    }
    fun close(view: View) {
        finish()
    }
    fun goto_options(view: View) {
        val intent = Intent(this, options::class.java)
        startActivity(intent)
    }
}