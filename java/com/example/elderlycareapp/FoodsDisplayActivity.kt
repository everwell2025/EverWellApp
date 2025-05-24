package com.example.elderlycareapp

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FoodsDisplayActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.foods_display_activity)

        setupBottomNavigation(null) // Page where nothing should be selected

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val category = intent.getStringExtra("CATEGORY") ?: ""

        val foods = getFoodsByCategory(category)
        foodAdapter = FoodAdapter(foods)
        recyclerView.adapter = foodAdapter

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }

    private fun getFoodsByCategory(category: String): List<Food> {
        return when (category) {
            "Heart" -> listOf(
                Food("Salmon", "🐟 Rich in omega-3 fatty acids that reduce inflammation, lower blood pressure, and prevent blood clots.", "Calories: 200 per 100g\nBenefits: ❤️ Supports heart health, 🧠 boosts brain function", R.drawable.salmon, "Heart"),
                Food("Oats", "🥣 Contains beta-glucan fiber that lowers bad cholesterol (LDL) and maintains blood sugar.", "Calories: 68 per 100g\nBenefits: 💓 Lowers cholesterol, ⚡ Sustained energy", R.drawable.oats, "Heart"),
                Food("Berries", "🍓 Packed with antioxidants and fiber that reduce oxidative stress and support blood vessel health.", "Calories: 57 per 100g\nBenefits: 🧬 Fights inflammation, 💗 Improves circulation", R.drawable.berries, "Heart")
            )
            "Diabetes" -> listOf(
                Food("Broccoli", "🥦 Low in carbs, high in fiber, supports insulin function and helps regulate blood sugar.", "Calories: 55 per 100g\nBenefits: 🩸 Stabilizes blood sugar, 💪 Strengthens immunity", R.drawable.broccoli, "Diabetes"),
                Food("Lentils", "🍲 High in fiber and plant-based protein with a low glycemic index.", "Calories: 116 per 100g\nBenefits: 📉 Lowers blood sugar spikes, 💖 Heart-friendly", R.drawable.lentils, "Diabetes"),
                Food("Cinnamon", "🌿 May help lower blood sugar by increasing insulin sensitivity. Also rich in antioxidants.", "Calories: 247 per 100g\nBenefits: 🧪 Balances glucose, 🛡️ Antioxidant boost", R.drawable.cinnamon, "Diabetes")
            )
            "Bones" -> listOf(
                Food("Spinach", "🌱 Rich in calcium, magnesium, and Vitamin K, essential for strong bones and joints.", "Calories: 23 per 100g\nBenefits: 🦴 Strengthens bones, 🧠 Improves brain function", R.drawable.spinach, "Bones"),
                Food("Tofu", "🍛 Plant-based protein with added calcium and iron, ideal for bone health.", "Calories: 144 per 100g\nBenefits: 🦴 Builds bone mass, 💪 Muscle repair", R.drawable.tofu, "Bones"),
                Food("Almonds", "🥜 Packed with calcium, magnesium, and phosphorus. Great for bone density.", "Calories: 579 per 100g\nBenefits: 🦷 Bone support, 💓 Healthy fats", R.drawable.almonds, "Bones")
            )
            "Energy" -> listOf(
                Food("Banana", "🍌 Natural sugars, potassium, and vitamin B6 boost quick energy and muscle function.", "Calories: 89 per 100g\nBenefits: ⚡ Instant energy, 🦵 Prevents cramps", R.drawable.banana, "Energy"),
                Food("Sweet Potato", "🍠 Complex carbs and fiber provide steady, long-lasting energy.", "Calories: 86 per 100g\nBenefits: 🔋 Sustained energy, 🧠 Brain support", R.drawable.sweet_potato, "Energy"),
                Food("Eggs", "🥚 High-quality protein and healthy fats that keep you full and energized.", "Calories: 143 per 100g\nBenefits: 💪 Muscle fuel, 🧠 Brain nourishment", R.drawable.eggs, "Energy")
            )
            else -> emptyList()
        }
    }
}
