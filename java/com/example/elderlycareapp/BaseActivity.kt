package com.example.elderlycareapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Magnifier
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

open class BaseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    protected lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fabMic: FloatingActionButton
    private lateinit var tts: TextToSpeech
    private val SPEECH_REQUEST_CODE = 100

    private var magnifier: Magnifier? = null
    private var isMagnifierEnabled = false
    private lateinit var rootView: View
    private lateinit var overlay: FrameLayout
    private val magnifierSize = 500
    private val radius = magnifierSize / 1.5f

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent direct usage
        if (this.javaClass == BaseActivity::class.java) {
            Toast.makeText(this, "BaseActivity should not be launched directly", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        tts = TextToSpeech(this, this)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                isMagnifierEnabled = !isMagnifierEnabled
                if (!isMagnifierEnabled) hideMagnifier()
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()

        rootView = findViewById(android.R.id.content)
        overlay = findViewById(R.id.magnifierOverlay)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && magnifier == null) {
            magnifier = Magnifier.Builder(rootView)
                .setSize(magnifierSize, magnifierSize)
                .setCornerRadius(radius)
                .setElevation(10f)
                .setInitialZoom(2.5f)
                .build()
        }

        rootView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isMagnifierEnabled &&
                (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN)) {
                magnifier?.show(event.x, event.y)
                overlay.x = event.rawX - radius
                overlay.y = event.rawY - radius
                overlay.visibility = View.VISIBLE
            }

            if (!isMagnifierEnabled && event.action == MotionEvent.ACTION_UP) {
                hideMagnifier()
            }

            true
        }
    }

    private fun hideMagnifier() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            magnifier?.dismiss()
        }
        overlay.visibility = View.GONE
    }

    fun setupBottomNavigation(currentMenuItemId: Int? = null) {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        fabMic = findViewById(R.id.fab_mic)

        if (currentMenuItemId != null && currentMenuItemId != 0) {
            bottomNavigationView.menu.setGroupCheckable(0, true, true)
            bottomNavigationView.selectedItemId = currentMenuItemId
        } else {
            bottomNavigationView.menu.setGroupCheckable(0, false, true) // disable checking
            bottomNavigationView.menu.forEach { it.isChecked = false } // uncheck all
            // do NOT set selectedItemId to invalid id 0
            // just leave it like this to have no highlight
        }

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    if (this !is MainActivity) startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_map -> {
                    if (this !is StreetViewActivity) startActivity(Intent(this, StreetViewActivity::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    if (this !is NotificationsActivity) startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    if (this !is SettingsActivity) startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        fabMic.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command...")
            try {
                startActivityForResult(intent, SPEECH_REQUEST_CODE)
            } catch (e: Exception) {
                Toast.makeText(this, "Speech recognizer failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        } else {
            Toast.makeText(this, "TTS not supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakAndNavigate(message: String, targetActivity: Class<*>) {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
        android.os.Handler(mainLooper).postDelayed({
            startActivity(Intent(this, targetActivity))
        }, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0)?.lowercase(Locale.ROOT)

            when {
                listOf("settings", "open settings").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Opening settings", SettingsActivity::class.java)

                listOf("home", "main page").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Going to home", MainActivity::class.java)

                listOf("games", "play a game", "game", "I want to play a game", "Lead me to games").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Launching games", GamesOptionsActivity::class.java)

                listOf("puzzle games", "puzzle", "I want to make a puzzle").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Launching puzzle games", PuzzleMenuActivity::class.java)

                listOf("sudoku games", "sudoku", "solve a sudoku").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Launching sudoku", SudokuActivity::class.java)

                listOf("memory games", "card games", "card", "flip cards").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Launching memory game", LevelSelectActivity::class.java)

                listOf("exercise", "start my workout").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Opening exercises", ExerciseCategoryActivity::class.java)

                listOf("map", "street view", "I want to see my hometown", "Google maps").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Opening map view", StreetViewActivity::class.java)

                listOf("diet", "food", "diet plan", "healthy diet", "lead me to diet section").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Showing diet plans", FoodCategoryActivity::class.java)

                listOf("bills", "bill reminder").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Opening bill reminders", BillReminderListActivity::class.java)

                listOf("set bill", "set bill reminder").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Leading to set bill reminder page", BillReminderActivity::class.java)

                listOf("medicine", "medicine reminder").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Showing medicine reminders", ReminderListActivity::class.java)

                listOf("set medicine", "set medicine reminder").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Leading to set medicine reminder page", ReminderActivity::class.java)

                listOf("notifications", "upcoming notification").any { spokenText?.contains(it) == true } ->
                    speakAndNavigate("Showing upcoming notifications", NotificationsActivity::class.java)

                else -> {
                    Toast.makeText(this, "Command not recognized: $spokenText", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            magnifier?.dismiss()
        }

        super.onDestroy()  // Only call this ONCE, and at the end
    }
}
