package com.example.boattask1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.boattask1.databinding.SpeechActivityMainBinding
import com.example.boattask1.utils.IS_CONTINUES_LISTEN
import com.example.boattask1.utils.RESULTS_LIMIT
import com.example.boattask1.utils.getErrorText
import com.example.boattask1.utils.matchCommand

private const val TAG = "MainActivitySpeechRecognizer"

private const val PERMISSIONS_REQUEST_RECORD_AUDIO_ANSWER_PHONE = 100

class MainActivitySpeechRecognizer : AppCompatActivity() {


    private var isPressedStart = false

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
                isPressedStart = false
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
        val audioPermissionCheck =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.RECORD_AUDIO
            )

        val permissionList = mutableListOf<String>()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val phoneStatePermissionCheck = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ANSWER_PHONE_CALLS
            )

            if (phoneStatePermissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ANSWER_PHONE_CALLS)
            }
        }
        if (audioPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissionList.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionList.toTypedArray(),
                PERMISSIONS_REQUEST_RECORD_AUDIO_ANSWER_PHONE
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
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                selectedLanguage
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                selectedLanguage
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, RESULTS_LIMIT)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO_ANSWER_PHONE) {
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
            matchCommand(text)
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
            if (isPressedStart) {
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