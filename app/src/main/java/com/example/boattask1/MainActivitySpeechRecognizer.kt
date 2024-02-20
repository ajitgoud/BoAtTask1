package com.example.boattask1

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.boattask1.databinding.ActivityMainBinding
import com.example.boattask1.databinding.SpeechActivityMainBinding
import com.example.boattask1.dks.speech.Dks
import com.example.boattask1.dks.speech.DksListener
import com.example.boattask1.utils.IS_CONTINUES_LISTEN
import com.example.boattask1.utils.PERMISSIONS_REQUEST_RECORD_AUDIO
import com.example.boattask1.utils.RESULTS_LIMIT
import com.example.boattask1.utils.getErrorText
import com.google.android.material.button.MaterialButton
import java.util.Locale

private const val TAG = "MainActivitySpeechRecognizer"

class MainActivitySpeechRecognizer : AppCompatActivity() {


    private var  isPressedStart=false

    private val binding by lazy {
        SpeechActivityMainBinding.inflate(layoutInflater)
    }


    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null

    private var selectedLanguage = "en" // Default "en selected"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        setListeners()
        checkPermissions()
        resetSpeechRecognizer()
        setRecogniserIntent()
        prepareLocales()


    }

    private fun setListeners() {
        binding.toggleSpeechListenerBtn.setOnClickListener {

            val btn = it as Button
            if (getString(R.string.start_listening_btn).equals(btn.text.toString(), true)) {
                startListening()
                isPressedStart = true
                binding.toggleSpeechListenerBtn.text = getString(R.string.stop_listening_btn)
            } else {
                binding.toggleSpeechListenerBtn.text = getString(R.string.start_listening_btn)
                isPressedStart=false
                stopListening()
            }
        }
    }

    private fun stopListening() {
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
        speechRecognizer!!.stopListening()
    }

    private fun checkPermissions() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.RECORD_AUDIO
            )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                PERMISSIONS_REQUEST_RECORD_AUDIO
            )
            return
        }
    }

    private fun resetSpeechRecognizer() {
        if (speechRecognizer != null) speechRecognizer!!.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        Log.d(
            TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this)
        )
        if (SpeechRecognizer.isRecognitionAvailable(this))
            speechRecognizer!!.setRecognitionListener(mRecognitionListener)
        else finish()
    }

    private fun setRecogniserIntent() {
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            selectedLanguage
        )
        recognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            selectedLanguage
        )
        recognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, RESULTS_LIMIT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startListening() {
        speechRecognizer!!.startListening(recognizerIntent)
        binding.progressBar.visibility = View.VISIBLE
        binding.errorTextView.text = ""
        binding.errorTextView.visibility = View.VISIBLE
    }


    public override fun onResume() {
        super.onResume()
        resetSpeechRecognizer()
        if (IS_CONTINUES_LISTEN && isPressedStart) {
            startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        speechRecognizer!!.stopListening()
    }

    override fun onStop() {
        super.onStop()
        if (speechRecognizer != null) {
            speechRecognizer!!.destroy()
        }
    }


    private fun prepareLocales() {
        val availableLocales =
            Locale.getAvailableLocales() //Alternatively you can check https://cloud.google.com/speech-to-text/docs/speech-to-text-supported-languages

        val adapterLocalization: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            availableLocales
        )
        adapterLocalization.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


    }

    private val mRecognitionListener = object : RecognitionListener {
        override fun onBeginningOfSpeech() {
            binding.progressBar.isIndeterminate = false
            binding.progressBar.max = 10
        }

        override fun onBufferReceived(buffer: ByteArray) {
            Log.d(TAG, "onBufferReceived: $buffer")
        }

        override fun onEndOfSpeech() {
            binding.progressBar.isIndeterminate = true
            speechRecognizer!!.stopListening()
        }

        override fun onResults(results: Bundle) {
            Log.d(TAG, "onResults")
            val matches: ArrayList<String>? = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            var text = ""
            for (result in matches!!) text += """
     $result
     
     """.trimIndent()
            binding.speechTextView.text = text
            if (IS_CONTINUES_LISTEN && isPressedStart) {
                startListening()
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        override fun onError(errorCode: Int) {
            val errorMessage = getErrorText(errorCode)
            Log.d(TAG, "FAILED $errorMessage")
            binding.errorTextView.text = errorMessage

            // rest voice recogniser
            resetSpeechRecognizer()
            if(isPressedStart){
                startListening()
            }
        }

        override fun onEvent(arg0: Int, arg1: Bundle) {
            Log.d(TAG, "onEvent")
        }

        override fun onPartialResults(arg0: Bundle) {
            Log.d(TAG, "onPartialResults")
        }

        override fun onReadyForSpeech(arg0: Bundle) {
            Log.d(TAG, "onReadyForSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            binding.progressBar.progress = rmsdB.toInt()
        }
    }


}