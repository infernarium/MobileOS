package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.log

class options : AppCompatActivity() {

    private lateinit var maxScoreTextView: EditText
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        maxScoreTextView = findViewById<EditText>(R.id.MaxScore)
        prefs = getSharedPreferences("opts", Context.MODE_PRIVATE)
        maxScoreTextView.setText(prefs.getInt("maxscore", 10).toString())
    }

    fun save(view: View) {
        val points = maxScoreTextView.text?.toString()?.toIntOrNull() ?: 0
        Log.d("testlogs", points.toString())
        if (points > 0) {
            prefs.edit()
                .putInt("maxscore", maxScoreTextView.text.toString().toInt())
                .apply()
            Toast.makeText(this, "Сохранено.", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Поле заполнено некорректно.", Toast.LENGTH_SHORT).show()
        }
    }
    fun close(view: View) {
            finish()
    }
}