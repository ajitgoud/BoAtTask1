package com.example.boattask1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.boattask1.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.example.boattask1.dks.speech.Dks
import com.example.boattask1.dks.speech.DksListener

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), DksListener {

    private lateinit var dks: Dks

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dks = Dks(application, supportFragmentManager, this)

        dks.injectProgressView(R.layout.listening_progress_layout)
        dks.oneStepResultVerify = true
        dks.continuousSpeechRecognition = true


        binding.toggleSpeechListenerBtn.setOnClickListener {
            val btn = it as MaterialButton
            Log.d(TAG, "onCreate: buttonclicks")
            if (getString(R.string.start_listening_btn).equals(btn.text.toString(), true)) {
                dks.startSpeechRecognition()
                binding.toggleSpeechListenerBtn.text = getString(R.string.stop_listening_btn)
            } else {
                binding.toggleSpeechListenerBtn.text = getString(R.string.start_listening_btn)
                dks.closeSpeechOperations()
            }
        }
    }


    override fun onDksFinalSpeechResult(speechResult: String) {
        Log.d(TAG, "Final speech result - $speechResult")
    }

    override fun onDksLanguagesAvailable(
        defaultLanguage: String?,
        supportedLanguages: ArrayList<String>?
    ) {
        Log.d(TAG, "defaultLanguage - $defaultLanguage")
        Log.d(TAG, "supportedLanguages - $supportedLanguages")

        if (supportedLanguages != null && supportedLanguages.contains("en-IN")) {
            // Setting the speech recognition language to english india if found
            dks.currentSpeechLanguage = "en-IN"
        }
    }

    override fun onDksLiveSpeechFrequency(frequency: Float) {

        Log.d(TAG, "Speech Live result - $frequency")
    }

    override fun onDksLiveSpeechResult(liveSpeechResult: String) {
        Log.d(TAG, "Speech result - $liveSpeechResult")
    }

    override fun onDksSpeechError(errMsg: String) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }
}