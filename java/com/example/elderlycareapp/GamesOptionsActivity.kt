package com.example.elderlycareapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

class GamesOptionsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_options)
        setupBottomNavigation(null)

        val memoryGameButton = findViewById<ImageButton>(R.id.btnMemoryGame)
        memoryGameButton.setOnClickListener {
            val intent = Intent(this, LevelSelectActivity::class.java)
            startActivity(intent)
        }

        val btnPuzzleGame = findViewById<ImageButton>(R.id.btnPuzzleGame)
        btnPuzzleGame.setOnClickListener {
            val intent = Intent(this, PuzzleMenuActivity::class.java)
            startActivity(intent)
        }

        val sudokuBtn = findViewById<ImageButton>(R.id.sudokuButton)
        sudokuBtn.setOnClickListener {
            val intent = Intent(this, SudokuActivity::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }
}