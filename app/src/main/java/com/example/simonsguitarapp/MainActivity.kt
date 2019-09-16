package com.example.simonsguitarapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    enum class TimerState{
        Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Paused

    private var majorChords = setOf<String>("A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#")
    private var minorChords = setOf<String>("Am", "Bbm", "Bm", "Cm", "C#m", "Dm", "Ebm", "Em", "Fm", "F#m", "Gm", "G#m")
    private var dom7MajorChords = setOf<String>("A7", "Bb7", "B7", "C7", "C#7", "D7", "Eb7", "E7", "F7", "F#7", "G7", "G#7")
    private var dom7MinorChords = setOf<String>("Am7", "Bbm7", "Bm7", "Cm7", "C#m7", "Dm7", "Ebm7", "Em7", "Fm7", "F#m7", "Gm7", "G#m7")

    private var allChords = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        majorChordsCheckBox.isChecked = true
        allChords.addAll(majorChords)

        startPauseButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                // chordTextView.text = timerState.toString()

                if (timerState == TimerState.Paused) {
                    startPauseButton.text = getString(R.string.pause)
                    timerState = TimerState.Running
                }
                else {
                    startPauseButton.text = getString(R.string.start)
                    timerState = TimerState.Paused
                }

            }
        })

    }


}
