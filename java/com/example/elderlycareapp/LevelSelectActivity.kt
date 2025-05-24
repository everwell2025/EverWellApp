package com.example.elderlycareapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

class LevelSelectActivity : BaseActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_select)

        setupBottomNavigation(null)

        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val unlockedLevel = prefs.getInt("unlocked_level", 1)

        val level1 = findViewById<ImageView>(R.id.level1)
        val level2 = findViewById<ImageView>(R.id.level2)
        val level3 = findViewById<ImageView>(R.id.level3)

        level1.setImageResource(R.drawable.level1_unlocked)
        level2.setImageResource(if (unlockedLevel >= 2) R.drawable.level2_unlocked else R.drawable.level2_locked)
        level3.setImageResource(if (unlockedLevel >= 3) R.drawable.level3_unlocked else R.drawable.level3_locked)

        level1.setOnClickListener { startLevel(1) }
        level2.setOnClickListener { if (unlockedLevel >= 2) startLevel(2) }
        level3.setOnClickListener { if (unlockedLevel >= 3) startLevel(3) }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }

    private fun startLevel(level: Int) {
        val intent = Intent(this, MemoryGameActivity::class.java)
        intent.putExtra("LEVEL", level)
        startActivity(intent)
    }
}
