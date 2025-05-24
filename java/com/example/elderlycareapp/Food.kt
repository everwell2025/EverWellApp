package com.example.elderlycareapp

data class Food(
    val title: String,
    val description: String,
    val nutrition: String,
    val imageResId: Int,
    val category: String,
    val extraImageResId: Int? = null,
    var isExpanded: Boolean = false
)
