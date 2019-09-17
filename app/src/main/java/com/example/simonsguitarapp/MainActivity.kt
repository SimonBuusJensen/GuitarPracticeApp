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
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    enum class TimerState{
        Paused, Running
    }

    private lateinit var timer: Timer
    private lateinit var timerThread : Thread

    private val timeBetweenChordShuffleInMillis: Long = 3000
    private var timerState = TimerState.Paused

    private val chordStringToChordImage : HashMap<String, Int> = hashMapOf(
        "A" to R.drawable.a,
        "B" to R.drawable.b,
        "C" to R.drawable.c,
        "D" to R.drawable.d,
        "E" to R.drawable.e,
        "F" to R.drawable.f,
        "G" to R.drawable.g,
        "Am" to R.drawable.am,
        "Bm" to R.drawable.bm,
        "Cm" to R.drawable.cm,
        "Dm" to R.drawable.dm,
        "Em" to R.drawable.em,
        "Fm" to R.drawable.fm,
        "Gm" to R.drawable.gm
    )

    //  "Bb", "C#",  "Eb", "F#", "G#"
    private var majorChords = setOf<String>("A", "B", "C", "D", "E", "F", "G")

    // "Bbm", "C#m", "Ebm", "F#m", "G#m"
    private var minorChords = setOf<String>("Am", "Bm", "Cm", "Dm", "Em", "Fm", "Gm")
    private var dom7MajorChords = setOf<String>("A7", "Bb7", "B7", "C7", "C#7", "D7", "Eb7", "E7", "F7", "F#7", "G7", "G#7")
    private var dom7MinorChords = setOf<String>("Am7", "Bbm7", "Bm7", "Cm7", "C#m7", "Dm7", "Ebm7", "Em7", "Fm7", "F#m7", "Gm7", "G#m7")
    private var allChords = mutableListOf<String>()

    private var oneCheckBoxIsChecked : Boolean = true

    private var currentDisplayedChord: String = ""
    private var nextChordToDisplay: String = ""

    private fun initializeView() {
        majorChordsCheckBox.isChecked = true
        allChords.addAll(majorChords)
        chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.text_font_size))
    }

    private fun displayNextChord() {
        if (allChords.size > 0) {
            while (currentDisplayedChord == nextChordToDisplay) {
                nextChordToDisplay = allChords.random()
            }

            System.out.println("Setting text size to " + getResources().getDimension(R.dimen.chord_font_size))
            chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.chord_font_size))
            chordTextView.text = nextChordToDisplay
            currentDisplayedChord = nextChordToDisplay

            chordImageView.setImageResource(chordStringToChordImage.get(currentDisplayedChord)!!)
        }
        else {
            chordTextView.text = getString(R.string.no_chords)
        }
    }

    private fun noChordCheckBoxChecked() {
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

        chordImageView.setImageResource(R.drawable.am)
    }

    private fun chordCheckBoxChecked(chords: Set<String>) {
        allChords.addAll(chords)
        startPauseButton.isClickable = true

        if (!oneCheckBoxIsChecked) {
            chordTextView.text = getString(R.string.start_practicing)
        }

        oneCheckBoxIsChecked = true
    }

    private fun initializeOnCheckBoxListeners() {

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

                    chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.text_font_size))
                    chordTextView.text = getString(R.string.start_practicing)
                }

            }
        })

    }


}
