package com.example.elderlycareapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class PuzzleActivity : BaseActivity() {

    private var currentImageResId: Int = 0
    private lateinit var grid: GridLayout
    private lateinit var originalBitmap: Bitmap
    private lateinit var shuffledTiles: MutableList<Bitmap>
    private lateinit var correctTiles: List<Bitmap>
    private lateinit var tileViews: MutableList<ImageView>
    private lateinit var swapSound: MediaPlayer

    private var numRows = 3
    private var numCols = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)

        setupBottomNavigation(null)

        val levelText = findViewById<TextView>(R.id.levelText)
        val difficulty = intent.getStringExtra("difficulty")?.capitalize() ?: "Easy"
        val level = intent.getIntExtra("level", 1)

        levelText.text = "Level $level - $difficulty"

        grid = findViewById(R.id.puzzleGrid)
        swapSound = MediaPlayer.create(this, R.raw.flip)

        currentImageResId = intent.getIntExtra("imageResId", R.drawable.puzzle_easy_one) // fallback image
        val previewImage = findViewById<ImageView>(R.id.previewImage)
        previewImage.setImageResource(currentImageResId)

        // Adjust grid size based on difficulty
        when (difficulty) {
            "Medium" -> {
                numRows = 4
                numCols = 4
            }
            "Hard" -> {
                numRows = 5
                numCols = 5
            }
            else -> {
                numRows = 3
                numCols = 3
            }
        }

        grid.post {
            loadPuzzle(currentImageResId)
        }

        // Set grid layout dimensions dynamically based on numRows and numCols
        grid.rowCount = numRows
        grid.columnCount = numCols

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }

    private fun loadPuzzle(imageResId: Int) {
        grid.removeAllViews()

        val bitmap = BitmapFactory.decodeResource(resources, imageResId)
        originalBitmap = bitmap
        correctTiles = splitImage(originalBitmap)
        shuffledTiles = correctTiles.shuffled().toMutableList()
        tileViews = mutableListOf()

        // Update tile size based on grid dimensions
        val tileSize = minOf(grid.width, grid.height) / numCols

        for (i in 0 until numRows * numCols) {
            val tileView = LayoutInflater.from(this).inflate(R.layout.puzzle_tile, grid, false)
            val image = tileView.findViewById<ImageView>(R.id.tileImage)

            image.setImageBitmap(shuffledTiles[i])
            image.tag = i
            image.setOnTouchListener(TileTouchListener())
            image.setOnDragListener(TileDragListener())

            val params = GridLayout.LayoutParams().apply {
                width = tileSize
                height = tileSize
                rowSpec = GridLayout.spec(i / numCols)
                columnSpec = GridLayout.spec(i % numCols)
            }

            tileView.layoutParams = params
            tileViews.add(image)
            grid.addView(tileView)
        }
    }

    private fun splitImage(bitmap: Bitmap): List<Bitmap> {
        val tileWidth = bitmap.width / numCols
        val tileHeight = bitmap.height / numRows
        val tiles = mutableListOf<Bitmap>()

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val x = col * tileWidth
                val y = row * tileHeight
                tiles.add(Bitmap.createBitmap(bitmap, x, y, tileWidth, tileHeight))
            }
        }

        return tiles
    }

    inner class TileTouchListener : View.OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val shadowBuilder = View.DragShadowBuilder(view)
                view.startDragAndDrop(null, shadowBuilder, view, 0)
                return true
            }
            return false
        }
    }

    inner class TileDragListener : View.OnDragListener {
        override fun onDrag(targetView: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as View
                    if (draggedView != targetView) {
                        swapTiles(draggedView as ImageView, targetView as ImageView)
                        checkIfCompleted()
                    }
                }
            }
            return true
        }
    }

    private fun swapTiles(tile1: ImageView, tile2: ImageView) {
        val tempBitmap = (tile1.drawable as BitmapDrawable).bitmap
        tile1.setImageDrawable(tile2.drawable)
        tile2.setImageBitmap(tempBitmap)

        swapSound.start()
    }

    private fun showCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("🎉 Puzzle Completed!")
            .setMessage("Great job! Want to try another one?")
            .setPositiveButton("Try Another") { _, _ ->
                startActivity(Intent(this, PuzzleMenuActivity::class.java))
                finish()
            }
            .setNegativeButton("Main Menu") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun checkIfCompleted() {
        val currentBitmaps = tileViews.map {
            (it.drawable as BitmapDrawable).bitmap
        }

        var allCorrect = true
        for (i in currentBitmaps.indices) {
            if (!bitmapsEqual(currentBitmaps[i], correctTiles[i])) {
                allCorrect = false
                break
            }
        }

        if (allCorrect) {
            showCompletionDialog()
        }
    }

    private fun bitmapsEqual(b1: Bitmap, b2: Bitmap): Boolean {
        return b1.sameAs(b2)
    }

    override fun onDestroy() {
        super.onDestroy()
        swapSound.release()
    }
}
