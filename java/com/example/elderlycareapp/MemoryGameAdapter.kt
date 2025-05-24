package com.example.elderlycareapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MemoryGameAdapter(
    private val cards: List<MemoryCard>,
    private val level: Int,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<MemoryGameAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(card: MemoryCard) {
            val front = itemView.findViewById<ImageView>(R.id.cardFront)
            val back = itemView.findViewById<ImageView>(R.id.cardBack)

            front.setImageResource(card.imageResId)

            if (card.isFlipped || card.isMatched) {
                flipToFront(front, back)
            } else {
                flipToBack(front, back)
            }

            itemView.setOnClickListener {
                onCardClick(adapterPosition)
            }
        }

        fun shake() {
            val shakeAnim = AnimationUtils.loadAnimation(itemView.context, R.anim.shake)
            itemView.startAnimation(shakeAnim)
        }

        private fun flipToFront(front: View, back: View) {
            front.visibility = View.VISIBLE
            back.animate()
                .rotationY(90f)
                .setDuration(150)
                .withEndAction {
                    back.visibility = View.GONE
                    front.rotationY = -90f
                    front.animate()
                        .rotationY(0f)
                        .setDuration(350)
                        .start()
                }.start()
        }

        private fun flipToBack(front: View, back: View) {
            back.visibility = View.VISIBLE
            front.animate()
                .rotationY(90f)
                .setDuration(150)
                .withEndAction {
                    front.visibility = View.GONE
                    back.rotationY = -90f
                    back.animate()
                        .rotationY(0f)
                        .setDuration(350)
                        .start()
                }.start()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)

        val context = parent.context
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val cardWidth: Int
        val cardHeight: Int
        val spacing = 32

        when (level) {
            1 -> {
                // 2 columns, rectangular: narrow width, taller height
                val numColumns = 2
                val totalPairs = 3 * 2
                val numRows = totalPairs / numColumns

                cardWidth = (screenWidth - (spacing * (numColumns + 5))) / numColumns
                cardHeight = (screenHeight - (spacing * (numRows + 1)) - 600) / numRows
            }

            2 -> {
                // 3 columns
                val numColumns = 3
                val totalPairs = 6 * 2
                val numRows = totalPairs / numColumns

                cardWidth = (screenWidth - (spacing * (numColumns + 4))) / numColumns
                cardHeight = (screenHeight - (spacing * (numRows + 1)) - 600) / numRows
            }

            3 -> {
                // 4 columns
                val numColumns = 4
                val totalPairs = 8 * 2
                val numRows = totalPairs / numColumns

                cardWidth = (screenWidth - (spacing * (numColumns + 4))) / numColumns
                cardHeight = (screenHeight - (spacing * (numRows + 3)) - 600) / numRows
            }

            else -> {
                cardWidth = (screenWidth * 0.3).toInt()
                cardHeight = (screenHeight * 0.18).toInt()
            }
        }

        val layoutParams = RecyclerView.LayoutParams(cardWidth, cardHeight)
        layoutParams.setMargins(16, 16, 16, 16)
        view.layoutParams = layoutParams

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains("SHAKE")) {
            holder.shake()
        } else {
            holder.bind(cards[position])
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    fun triggerShakeAnimation(pos1: Int, pos2: Int) {
        notifyItemChanged(pos1, "SHAKE")
        notifyItemChanged(pos2, "SHAKE")
    }

    override fun getItemCount() = cards.size
}