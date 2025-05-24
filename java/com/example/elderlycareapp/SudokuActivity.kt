package com.example.elderlycareapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SudokuActivity : BaseActivity() {

    private val puzzles = listOf(
        arrayOf(
            arrayOf("1", "", "", "4"),
            arrayOf("", "3", "", ""),
            arrayOf("", "", "2", ""),
            arrayOf("3", "", "", "1")
        ),
        arrayOf(
            arrayOf("", "2", "", ""),
            arrayOf("3", "", "", "4"),
            arrayOf("4", "", "", "2"),
            arrayOf("", "", "3", "")
        ),
        arrayOf(
            arrayOf("", "", "", "2"),
            arrayOf("2", "1", "", ""),
            arrayOf("", "", "4", "3"),
            arrayOf("3", "", "", "")
        )
    )

    private val solutions = listOf(
        arrayOf(
            arrayOf("1", "2", "3", "4"),
            arrayOf("4", "3", "1", "2"),
            arrayOf("2", "1", "4", "3"),
            arrayOf("3", "4", "2", "1")
        ),
        arrayOf(
            arrayOf("1", "2", "4", "3"),
            arrayOf("3", "1", "2", "4"),
            arrayOf("4", "3", "1", "2"),
            arrayOf("2", "4", "3", "1")
        ),
        arrayOf(
            arrayOf("4", "3", "1", "2"),
            arrayOf("2", "1", "3", "4"),
            arrayOf("1", "2", "4", "3"),
            arrayOf("3", "4", "2", "1")
        )
    )

    private lateinit var puzzle: Array<Array<String>>
    private lateinit var solution: Array<Array<String>>

    private var currentPuzzleIndex = 0
    private var hintsLeft = 3
    private var selectedCell: TextView? = null  // To keep track of the selected cell

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sudoku)
        setupBottomNavigation(null)

        val gridSize = intent.getIntExtra("GRID_SIZE", 4) // default to 4x4

        val sudokuGrid = findViewById<GridLayout>(R.id.sudokuGrid)
        sudokuGrid.removeAllViews()

        currentPuzzleIndex = (puzzles.indices).random()
        puzzle = puzzles[currentPuzzleIndex].map { it.copyOf() }.toTypedArray()
        solution = solutions[currentPuzzleIndex]

        val isDark = getSharedPreferences("sudoku", MODE_PRIVATE)
            .getBoolean("darkTheme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        val hintBtn = findViewById<Button>(R.id.hintButton)
        hintBtn.setOnClickListener {
            giveHint()
        }

        val restartBtn = findViewById<Button>(R.id.restartButton)
        restartBtn.setOnClickListener {
            restartPuzzle()
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }

        // Number buttons setup
        val number1Button = findViewById<Button>(R.id.number1)
        val number2Button = findViewById<Button>(R.id.number2)
        val number3Button = findViewById<Button>(R.id.number3)
        val number4Button = findViewById<Button>(R.id.number4)

        val numberButtons = listOf(number1Button, number2Button, number3Button, number4Button)
        numberButtons.forEach { button ->
            button.setOnClickListener {
                selectedCell?.let { cell ->
                    val number = button.text.toString()
                    if (number == solution[getCellRow(cell)][getCellCol(cell)]) {
                        cell.text = number
                        cell.setTextColor(Color.BLACK)
                        cell.setBackgroundResource(R.drawable.cell_correct)
                        highlightMatchingNumbers(number)
                        checkWinCondition()
                    } else {
                        cell.text = number
                        cell.setTextColor(Color.RED)
                        cell.setBackgroundResource(R.drawable.cell_wrong)
                    }
                }
                selectedCell = null
            }
        }

        // Creating Sudoku grid
        for (row in 0..3) {
            for (col in 0..3) {
                val cell = TextView(this)
                cell.text = puzzle[row][col]
                cell.textSize = 24f
                cell.gravity = Gravity.CENTER
                cell.setPadding(24, 24, 24, 24)
                cell.setBackgroundResource(R.drawable.cell_background)

                if (puzzle[row][col].isEmpty()) {
                    cell.setOnClickListener {
                        selectedCell?.setBackgroundResource(R.drawable.cell_background) // Remove highlight from previous cell
                        selectedCell = cell
                        selectedCell?.setBackgroundResource(R.drawable.cell_selected)   // Use the fancy new selected background
                    }
                } else {
                    cell.setTypeface(null, Typeface.BOLD)
                }

                val params = GridLayout.LayoutParams(
                    GridLayout.spec(row),
                    GridLayout.spec(col)
                ).apply {
                    width = 160
                    height = 160
                    setMargins(4, 4, 4, 4)
                }
                cell.layoutParams = params
                sudokuGrid.addView(cell)
            }
        }

        updateHintButton()
    }

    private fun giveHint() {
        if (hintsLeft <= 0) return

        val grid = findViewById<GridLayout>(R.id.sudokuGrid)
        for (i in 0 until 16) {
            val row = i / 4
            val col = i % 4
            val cell = grid.getChildAt(i) as TextView
            if (cell.text.isEmpty()) {
                val correct = solution[row][col]
                cell.text = correct
                cell.setTextColor(Color.BLUE)
                cell.setBackgroundResource(R.drawable.cell_correct)
                hintsLeft--
                updateHintButton()
                break
            }
        }
    }

    private fun updateHintButton() {
        val hintBtn = findViewById<Button>(R.id.hintButton)
        hintBtn.text = "Hint ($hintsLeft left)"
        if (hintsLeft <= 0) {
            hintBtn.isEnabled = false
            hintBtn.alpha = 0.5f
        } else {
            hintBtn.isEnabled = true
            hintBtn.alpha = 1f
        }
    }

    private fun highlightMatchingNumbers(value: String) {
        val grid = findViewById<GridLayout>(R.id.sudokuGrid)
        for (i in 0 until grid.childCount) {
            val cell = grid.getChildAt(i) as TextView
            if (cell.text == value) {
                cell.setBackgroundResource(R.drawable.cell_highlight)
            } else {
                if (cell.currentTextColor != Color.RED) {
                    cell.setBackgroundResource(R.drawable.cell_background)
                }
            }
        }
    }

    private fun checkWinCondition() {
        val grid = findViewById<GridLayout>(R.id.sudokuGrid)

        for (i in 0 until 16) {
            val row = i / 4
            val col = i % 4
            val cell = grid.getChildAt(i) as TextView
            val userInput = cell.text.toString().trim()

            // If a cell is still empty, don't check yet
            if (userInput.isEmpty()) return

            val correctAnswer = solution[row][col]
            if (userInput != correctAnswer) return
        }

        // All cells filled and correct
        AlertDialog.Builder(this)
            .setTitle("🎉 Congratulations!")
            .setMessage("You solved the puzzle!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun restartPuzzle() {
        puzzle = puzzles[currentPuzzleIndex].map { it.copyOf() }.toTypedArray()

        val sudokuGrid = findViewById<GridLayout>(R.id.sudokuGrid)
        for (i in 0 until sudokuGrid.childCount) {
            val cell = sudokuGrid.getChildAt(i) as TextView
            val row = i / 4
            val col = i % 4
            cell.text = puzzle[row][col]
            cell.setBackgroundResource(R.drawable.cell_background)
            cell.setTextColor(Color.BLACK)
        }

        hintsLeft = 3
        updateHintButton()
        showCustomToast("Puzzle Restarted!")
    }

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast_custom, null)

        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun getCellRow(cell: TextView): Int {
        val grid = findViewById<GridLayout>(R.id.sudokuGrid)
        val index = grid.indexOfChild(cell)
        return index / 4
    }

    private fun getCellCol(cell: TextView): Int {
        val grid = findViewById<GridLayout>(R.id.sudokuGrid)
        val index = grid.indexOfChild(cell)
        return index % 4
    }
}