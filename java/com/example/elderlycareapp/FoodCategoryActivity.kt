package com.example.elderlycareapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class FoodCategoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.food_category_activity)

        setupBottomNavigation(null)

        findViewById<ImageButton>(R.id.btnHeart).setOnClickListener {
            openFoodsActivity("Heart")
        }
        findViewById<ImageButton>(R.id.btnDiabetes).setOnClickListener {
            openFoodsActivity("Diabetes")
        }
        findViewById<ImageButton>(R.id.btnBones).setOnClickListener {
            openFoodsActivity("Bones")
        }
        findViewById<ImageButton>(R.id.btnEnergy).setOnClickListener {
            openFoodsActivity("Energy")
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }

    private fun openFoodsActivity(category: String) {
        val intent = Intent(this, FoodsDisplayActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }
}
