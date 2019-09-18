package com.example.simonsguitarapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.util.TypedValue
import android.widget.SeekBar
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    enum class TimerState{
        Paused, Running
    }

    private lateinit var timerThread : Thread
    private var timeBetweenChordShuffleInMillis: Long = 5000
    private var timerState = TimerState.Paused

    private val chordStringToChordImage : HashMap<String, Int> = Utils.createChordStringToChordImageHashMap()

    //  "Bb", "C#",  "Eb", "F#", "G#"
    private var majorChords = setOf<String>("A", "B", "C", "D", "E", "F", "G")
    // "Bbm", "C#m", "Ebm", "F#m", "G#m"
    private var minorChords = setOf<String>("Am", "Bm", "Cm", "Dm", "Em", "Fm", "Gm")
    // "Bb7", "C#7", "Eb7", "F#7", "G#7"
    private var dom7MajorChords = setOf<String>("A7", "B7", "C7", "D7", "E7", "F7", "G7")
    // "Bbm7", "Bm7", "Cm7", "C#m7", "Dm7", "Ebm7", "Em7", "Fm7", "F#m7", "Gm7", "G#m7"
    private var dom7MinorChords = setOf<String>("Am7")
    private var allChords = mutableListOf<String>()
    private var currentDisplayedChord: String = ""
    private var nextChordToDisplay: String = ""

    private var oneCheckBoxIsChecked : Boolean = true

    private val minSeekBarProgress : Int = 1

    private fun initializeView() {
        majorChordsCheckBox.isChecked = true
        allChords.addAll(majorChords)
        chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.text_font_size))

        val initialSeekBarProgress = 5
        practiceTimeSeekBar.progress = initialSeekBarProgress
        practiceTimeSeekBar.setProgress(initialSeekBarProgress - minSeekBarProgress)

    }

    private fun displayNextChord() {
        if (allChords.size > 0) {
            while (currentDisplayedChord == nextChordToDisplay) {
                nextChordToDisplay = allChords.random()
            }

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

        minor7ChordsCheckBox.isClickable = false

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

        practiceTimeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            var progressChange = minSeekBarProgress;

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                return
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                return
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressChange = progress + minSeekBarProgress
                seekBarTextView.text = (progressChange).toString() + getString(R.string.seconds_short)
                timeBetweenChordShuffleInMillis = progressChange.toLong() * 1000
            }



        })

    }


}
