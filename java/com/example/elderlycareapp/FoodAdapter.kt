package com.example.elderlycareapp

import android.graphics.Color
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(private val foodList: List<Food>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.foodTitle)
        val description: TextView = itemView.findViewById(R.id.foodDescription)
        val nutrition: TextView = itemView.findViewById(R.id.foodNutrition)
        val detailsLayout: LinearLayout = itemView.findViewById(R.id.detailsLayout)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage) // <-- New
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]

        holder.title.text = food.title
        holder.description.text = food.description
        holder.nutrition.text = food.nutrition
        holder.foodImage.setImageResource(food.imageResId)

        // Change card color based on category or food name
        val backgroundColor = when (food.category) {
            "Heart" -> "#FFF5E2"     // Light YELLOW
            "Diabetes" -> "#FFF5E2" // Light RED
            "Bones" -> "#FFF5E2"    // Light BLUE
            "Energy" -> "#FFF5E2"   // Light GREEN
            else -> "#FFFFFF"         // Default white
        }

        holder.cardView.setCardBackgroundColor(Color.parseColor(backgroundColor))

// Restore expanded/collapsed state properly
        if (food.isExpanded) {
            holder.detailsLayout.visibility = View.VISIBLE
        } else {
            holder.detailsLayout.visibility = View.GONE
        }

        holder.cardView.setOnClickListener {
            food.isExpanded = !food.isExpanded
            if (food.isExpanded) {
                ViewAnimationUtils.expand(holder.detailsLayout)
            } else {
                ViewAnimationUtils.collapse(holder.detailsLayout)
            }
        }
    }

    override fun getItemCount(): Int = foodList.size
}
