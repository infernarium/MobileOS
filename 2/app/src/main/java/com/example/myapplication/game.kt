package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class game : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private lateinit var score: TextView
    private lateinit var res: TextView

    private lateinit var botChoice: ImageView
    private lateinit var gamerChoice: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        prefs = getSharedPreferences("opts", Context.MODE_PRIVATE)

        score = findViewById(R.id.textView2)
        score.setText("0:0")
        res = findViewById(R.id.textView5)
        botChoice = findViewById(R.id.imageView2)
        gamerChoice = findViewById(R.id.imageView)
    }

    fun change_menu(view: View) {
        finish()
    }

    fun change_settings(view: View) {
        val intent = Intent(this, options::class.java)
        startActivity(intent)
    }

    fun restart(view: View) {
        finish();
        startActivity(getIntent());
    }

    private fun drawChoice(value: Int, view: ImageView) {
        when (value) {
            1 -> view.setImageResource(R.drawable.rock)
            2 -> view.setImageResource(R.drawable.scissors)
            3 -> view.setImageResource(R.drawable.papper)
        }
    }

    fun iswin(user: Int, bot: Int): String{
        when (user) {
            1 -> {
                return when (bot) {
                    1 -> "Ничья"
                    2 -> "Победа"
                    else -> "Поражение"
                }
            }
            2 -> {
                return when (bot) {
                    1 -> "Поражение"
                    2 -> "Ничья"
                    else -> "Победа"
                }
            }
            else -> {
                return when (bot) {
                    1 -> "Победа"
                    2 -> "Поражение"
                    else -> "Ничья"
                }
            }
        }
    }

    fun calculating(view: View) {
        var userSelection = view.tag.toString().toInt()
        var botselection = Random.nextInt(1, 4)

        drawChoice(userSelection, gamerChoice)
        drawChoice(botselection, botChoice)

        var situation = iswin(userSelection, botselection)
        res.text = situation

        var parts = score.text.toString().split(":")

        Log.d("mydebug", parts[0].toString())
        var userSS = parts[0].toInt()
        var botSS = parts[1].toInt()
        if (situation == "Победа") {
            userSS += 1
        } else if (situation == "Поражение") {
            botSS += 1
        }

        score.setText("${userSS.toString()}:${botSS.toString()}")

        var maxscore = prefs.getInt("maxscore", 10)
        if (userSS == maxscore) {
            var userrecord = prefs.getInt("wins", 0)
            prefs.edit().putInt("wins", userrecord + 1).apply()
            finish()
            startActivity(getIntent())
        }
        else if (botSS == maxscore) {
            finish()
            startActivity(getIntent())
        }
    }
}