package com.example.simonsguitarapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.util.DisplayMetrics
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.TypedValue


class MainActivity : AppCompatActivity() {

    enum class TimerState{
        Paused, Running
    }

    private lateinit var timer: Timer
    private val timeBetweenChordShuffleInMillis: Long = 3000
    private var timerState = TimerState.Paused

    private var majorChords = setOf<String>("A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#")
    private var minorChords = setOf<String>("Am", "Bbm", "Bm", "Cm", "C#m", "Dm", "Ebm", "Em", "Fm", "F#m", "Gm", "G#m")
    private var dom7MajorChords = setOf<String>("A7", "Bb7", "B7", "C7", "C#7", "D7", "Eb7", "E7", "F7", "F#7", "G7", "G#7")
    private var dom7MinorChords = setOf<String>("Am7", "Bbm7", "Bm7", "Cm7", "C#m7", "Dm7", "Ebm7", "Em7", "Fm7", "F#m7", "Gm7", "G#m7")
    private var oneCheckBoxIsChecked : Boolean = true

    private var currentDisplayedChord: String = ""
    private var nextChordToDisplay: String = ""

    private var allChords = mutableListOf<String>()
    private lateinit var timerThread : Thread

    fun initializeView() {
        majorChordsCheckBox.isChecked = true
        allChords.addAll(majorChords)
        chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.text_font_size))
    }

    fun displayNextChord() {
        if (allChords.size > 0) {
            while (currentDisplayedChord == nextChordToDisplay) {
                nextChordToDisplay = allChords.random()
            }

            System.out.println("Setting text size to " + getResources().getDimension(R.dimen.chord_font_size))
            chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.chord_font_size))
            chordTextView.text = nextChordToDisplay
            currentDisplayedChord = nextChordToDisplay
        }
        else {
            chordTextView.text = getString(R.string.no_chords)
        }
    }

    fun noChordCheckBoxChecked() {
        oneCheckBoxIsChecked = false
        System.out.println("Setting text size to " + getResources().getDimension(R.dimen.text_font_size))
        chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.text_font_size))
        chordTextView.text = getString(R.string.no_chords)

        if (::timerThread.isInitialized) {
            timerThread.interrupt()
        }

        startPauseButton.text = getString(R.string.start)
        startPauseButton.isClickable = false
        timerState = TimerState.Paused
    }

    fun chordCheckBoxChecked(chords: Set<String>) {
        allChords.addAll(chords)
        startPauseButton.isClickable = true

        if (!oneCheckBoxIsChecked) {
            chordTextView.text = getString(R.string.start_practicing)
        }

        oneCheckBoxIsChecked = true
    }

    fun initializeOnCheckBoxListeners() {

        minorChordsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chordCheckBoxChecked(minorChords)
            }
            else {
                allChords.removeAll(minorChords)

                if (!majorChordsCheckBox.isChecked && !major7ChordsCheckBox.isChecked && !minor7ChordsCheckBox.isChecked) {
                    noChordCheckBoxChecked()
                }
            }
        }

        majorChordsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chordCheckBoxChecked(majorChords)
            }
            else {
                allChords.removeAll(majorChords)

                if (!minorChordsCheckBox.isChecked && !major7ChordsCheckBox.isChecked && !minor7ChordsCheckBox.isChecked) {
                    noChordCheckBoxChecked()
                }
            }
        }

        major7ChordsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chordCheckBoxChecked(dom7MajorChords)
            }
            else {
                allChords.removeAll(dom7MajorChords)

                if (!minorChordsCheckBox.isChecked && !majorChordsCheckBox.isChecked && !minor7ChordsCheckBox.isChecked) {
                    noChordCheckBoxChecked()
                }
            }
        }

        minor7ChordsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chordCheckBoxChecked(dom7MinorChords)
            }
            else {
                allChords.removeAll(dom7MinorChords)

                if (!minorChordsCheckBox.isChecked && !majorChordsCheckBox.isChecked && !major7ChordsCheckBox.isChecked) {
                    noChordCheckBoxChecked()
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeView()
        initializeOnCheckBoxListeners()

        System.out.println("Initial text size " + chordTextView.textSize)

        startPauseButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                // chordTextView.text = timerState.toString()

                if (timerState == TimerState.Paused) {
                    startPauseButton.text = getString(R.string.pause)

                    displayNextChord()

                    timerState = TimerState.Running
                    timerThread = object : Thread() {

                        override fun run() {
                            try {
                                while (true) {
                                    sleep(timeBetweenChordShuffleInMillis)
                                    runOnUiThread {
                                        displayNextChord()
                                    }
                                }
                            } catch (e: InterruptedException) {
                            }
                        }
                    }
                    timerThread.start()

                }
                else {
                    startPauseButton.text = getString(R.string.start)
                    timerState = TimerState.Paused
                    timerThread.interrupt()
                    //System.out.println("Setting text size to " + getResources().getDimension(R.dimen.text_font_size))
                    chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.text_font_size))
                    chordTextView.text = getString(R.string.start_practicing)
                }

            }
        })

    }


}
