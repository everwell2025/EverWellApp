package com.example.elderlycareapp

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MemoryGameActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scoreText: TextView
    private lateinit var adapter: MemoryGameAdapter
    private lateinit var cards: List<MemoryCard>
    private lateinit var flipSound: MediaPlayer
    private lateinit var matchSound: MediaPlayer

    private var indexOfSingleSelectedCard: Int? = null
    private var score = 0
    private var matchedPairs = 0
    private var numUniqueCards = 0

    private val cardImages = listOf(
        R.drawable.img_1, R.drawable.img_2, R.drawable.img_3,
        R.drawable.img_4, R.drawable.img_5, R.drawable.img_6,
        R.drawable.img_7, R.drawable.img_8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_game)

        flipSound = MediaPlayer.create(this, R.raw.flip)
        matchSound = MediaPlayer.create(this, R.raw.match)

        recyclerView = findViewById(R.id.recyclerView)
        scoreText = findViewById(R.id.scoreText)

        recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        val level = intent.getIntExtra("LEVEL", 1)
        numUniqueCards = when (level) {
            1 -> 3
            2 -> 6
            3 -> 8
            else -> 4
        }

        val shuffledImages = (cardImages.take(numUniqueCards) + cardImages.take(numUniqueCards)).shuffled()
        cards = shuffledImages.map { MemoryCard(it) }

        adapter = MemoryGameAdapter(cards, level) { position -> handleCardClick(position) }

        recyclerView.adapter = adapter
        val numColumns = when (level) {
            1 -> 2
            2 -> 3
            3 -> 4
            else -> 2
        }
        recyclerView.layoutManager = GridLayoutManager(this, numColumns)


        updateScore(0)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }

    private fun handleCardClick(position: Int) {
        val card = cards[position]
        if (card.isMatched || card.isFlipped) return

        card.isFlipped = true
        adapter.notifyItemChanged(position)

        flipSound.seekTo(0)
        flipSound.start()

        if (indexOfSingleSelectedCard == null) {
            indexOfSingleSelectedCard = position
        } else {
            val matchedIndex = indexOfSingleSelectedCard!!
            indexOfSingleSelectedCard = null

            val currentScore = score

            Handler(Looper.getMainLooper()).postDelayed({
                if (cards[matchedIndex].imageResId == card.imageResId) {
                    cards[matchedIndex].isMatched = true
                    card.isMatched = true
                    matchedPairs++
                    updateScore(score + 10)
                    matchSound.start()

                    if (matchedPairs == numUniqueCards) {
                        showWinDialog()
                    }
                } else {
                    adapter.triggerShakeAnimation(matchedIndex, position)
                    Handler(Looper.getMainLooper()).postDelayed({
                        cards[matchedIndex].isFlipped = false
                        card.isFlipped = false
                        adapter.notifyItemChanged(matchedIndex)
                        adapter.notifyItemChanged(position)
                    }, 500)
                    updateScore(score - 2)
                }
            }, 500)
        }
    }

    private fun updateScore(newScore: Int) {
        score = newScore.coerceAtLeast(0)
        scoreText.text = "Score: $score"
    }

    private fun showWinDialog() {
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val currentLevel = intent.getIntExtra("LEVEL", 1)
        val unlockedLevel = prefs.getInt("unlocked_level", 1)
        val highScoreKey = "high_score_level_$currentLevel"
        val previousHigh = prefs.getInt(highScoreKey, 0)

        // Save high score if this run is better
        if (score > previousHigh) {
            prefs.edit().putInt(highScoreKey, score).apply()
        }

        if (currentLevel == unlockedLevel && currentLevel < 3) {
            prefs.edit().putInt("unlocked_level", currentLevel + 1).apply()
        }

        AlertDialog.Builder(this)
            .setTitle("🎉 You Win!")
            .setMessage("Level $currentLevel Completed!\nScore: $score")
            .setPositiveButton("Next") { _, _ ->
                val intent = Intent(this, LevelSelectActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Exit") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        flipSound.release()
        matchSound.release()
        super.onDestroy()
    }
}
