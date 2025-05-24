package com.example.elderlycareapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.cardview.widget.CardView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PuzzleSelectionActivity : AppCompatActivity() {

    private val easyPuzzles = listOf(
        R.drawable.puzzle_easy_one,
        R.drawable.puzzle_easy_two,
        R.drawable.puzzle_easy_three
    )

    private val mediumPuzzles = listOf(
        R.drawable.puzzle_medium_one,
        R.drawable.puzzle_medium_two,
        R.drawable.puzzle_medium_three
    )

    private val hardPuzzles = listOf(
        R.drawable.puzzle_hard_one,
        R.drawable.puzzle_hard_two,
        R.drawable.puzzle_hard_three
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_selection)

        val difficulty = intent.getStringExtra("difficulty") ?: "easy"
        val puzzleContainer = findViewById<LinearLayout>(R.id.puzzleList)

        val puzzles = when (difficulty) {
            "medium" -> mediumPuzzles
            "hard" -> hardPuzzles
            else -> easyPuzzles
        }

        for ((index, resId) in puzzles.withIndex()) {
            val cardView = CardView(this).apply {
                radius = 24f
                cardElevation = 12f
                useCompatPadding = true
                layoutParams = LinearLayout.LayoutParams(
                    600,  // Less wide
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(24, 24, 24, 24)
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }

            val cardContent = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 26, 0, 0) // ⬅️ adds space around content inside the card
            }

            val button = ImageButton(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    500
                )
                setImageResource(resId)
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = "Puzzle Image"
                setBackgroundResource(0)
                setOnClickListener {
                    val intent = Intent(this@PuzzleSelectionActivity, PuzzleActivity::class.java)
                    intent.putExtra("imageResId", resId)
                    intent.putExtra("difficulty", difficulty)
                    intent.putExtra("level", index + 1)
                    startActivity(intent)
                }
            }

            val levelText = TextView(this).apply {
                text = "Level ${index + 1}"
                textSize = 16f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 16)
            }

            cardContent.addView(button)
            cardContent.addView(levelText)
            cardView.addView(cardContent)
            puzzleContainer.addView(cardView)
        }
    }
}
