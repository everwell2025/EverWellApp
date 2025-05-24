package com.example.elderlycareapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class PuzzleMenuActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_menu)
        setupBottomNavigation(null)

        findViewById<ImageButton>(R.id.easyButton).setOnClickListener {
            startPuzzleSelection("easy")
        }

        findViewById<ImageButton>(R.id.mediumButton).setOnClickListener {
            startPuzzleSelection("medium")
        }

        findViewById<ImageButton>(R.id.hardButton).setOnClickListener {
            startPuzzleSelection("hard")
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }

    private fun startPuzzleSelection(difficulty: String) {
        val intent = Intent(this, PuzzleSelectionActivity::class.java)
        intent.putExtra("difficulty", difficulty)
        startActivity(intent)
    }
}
