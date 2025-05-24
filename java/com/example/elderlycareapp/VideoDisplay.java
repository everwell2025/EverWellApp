package com.example.elderlycareapp;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class VideoDisplay extends AppCompatActivity {

    private boolean useStopwatch = true;
    private boolean running = false;
    private long countdownTimeMillis = 0;
    private long stopwatchElapsed = 0;
    private long stopwatchStartTime = 0;
    private CountDownTimer countDownTimer;
    private Handler stopwatchHandler = new Handler();
    private Runnable stopwatchRunnable;


    private Button timerButton;
    private TextView timeDisplay;
    private Switch modeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_display_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        WebView webView = findViewById(R.id.webView);
        String videoId = getIntent().getStringExtra("VIDEO_ID");
        if (videoId == null) videoId = "8NemLjfqy24";
        String videoHtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\" allowfullscreen></iframe>";


        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadData(videoHtml, "text/html", "utf-8");


        timerButton = findViewById(R.id.timerButton);
        timeDisplay = findViewById(R.id.timeDisplay);
        modeSwitch = findViewById(R.id.modeSwitch);


        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            stopTimer();
            stopwatchElapsed = 0;
            useStopwatch = !isChecked;
            resetTimer();
        });




        timerButton.setOnClickListener(v -> {
            if (running) {
                showFinishDialog();
            } else {
                if (useStopwatch) {
                    startStopwatch();
                } else {
                    showTimePicker();
                }
            }
        });
    }


    private void showTimePicker() {
        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            countdownTimeMillis = (hourOfDay * 3600 + minute * 60) * 1000;
            if (countdownTimeMillis > 0) {
                startCountdown();
            }
        }, 0, 0, true);
        timePicker.setTitle("Set Timer (HH:MM)");
        timePicker.show();
    }


    private void startCountdown() {
        countDownTimer = new CountDownTimer(countdownTimeMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeDisplay.setText(formatTime(millisUntilFinished));
            }


            public void onFinish() {
                timeDisplay.setText("00:00:00");
                running = false;
                timerButton.setText("Start");
            }
        }.start();
        timerButton.setText("Finish");
        running = true;
    }


    private void startStopwatch() {
        stopwatchStartTime = System.currentTimeMillis();
        stopwatchRunnable = new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                long totalElapsed = stopwatchElapsed + (now - stopwatchStartTime);
                timeDisplay.setText(formatTime(totalElapsed));
                stopwatchHandler.postDelayed(this, 1000);
            }
        };
        stopwatchHandler.post(stopwatchRunnable);
        timerButton.setText("Finish");
        running = true;
    }


    private void stopTimer() {
        if (useStopwatch) {
            if (stopwatchRunnable != null) {
                stopwatchHandler.removeCallbacks(stopwatchRunnable);
            }
            stopwatchElapsed += System.currentTimeMillis() - stopwatchStartTime;
        } else if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        running = false;
    }


    private void resetTimer() {
        stopTimer();
        stopwatchElapsed = 0;
        timeDisplay.setText("00:00:00");
        timerButton.setText("Start");
    }


    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000);
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
    }


    private void showFinishDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Finish Workout")
                .setMessage("Are you sure you want to finish the workout?")
                .setPositiveButton("OK", (dialog, which) -> {
                    stopTimer();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}