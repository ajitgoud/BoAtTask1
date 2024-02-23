package com.example.boattask1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.boattask1.databinding.ActivityVospBinding
import com.example.boattask1.utils.ACCEPT_CALL_COMMAND
import com.example.boattask1.utils.CONFUSING_COMMAND_ARRAY
import com.example.boattask1.utils.REJECT_CALL_COMMAND
import com.example.boattask1.utils.matchCommand
import org.json.JSONException
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.IOException


private const val TAG = "VospActivity"
private const val VOSK_SPEECH_SAMPLE_RATE = 20000.0f

private const val PERMISSIONS_REQUEST_RECORD_AUDIO_ANSWER_PHONE = 100

class VospActivity : AppCompatActivity(), RecognitionListener {


    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var speechStreamService: SpeechStreamService? = null
    private val binding by lazy {
        ActivityVospBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkForSpeechRecognition()
        initModel()
        checkPermissions()
        setListeners()

    }

    private fun checkForSpeechRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech Recognition not available", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun setListeners() {
        binding.toggleSpeechListenerBtn.setOnClickListener {
            if (isHavePermissions()) {
                val btn = it as Button
                if (getString(R.string.start_listening_btn).equals(btn.text.toString(), true)) {
                    startListening()
                    binding.toggleSpeechListenerBtn.text = getString(R.string.stop_listening_btn)
                } else {
                    binding.toggleSpeechListenerBtn.text = getString(R.string.start_listening_btn)
                    stopListening()
                }
            } else {
                checkPermissions()
            }
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


    private fun isHavePermissions(): Boolean {
        val permissionCheck =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.RECORD_AUDIO
            )
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }

    private fun stopListening() {
        speechService?.stop()
        speechService?.shutdown()
    }

    private fun startListening() {
        val keywords = arrayOf(
            CONFUSING_COMMAND_ARRAY.joinToString(","),
            ACCEPT_CALL_COMMAND,
            REJECT_CALL_COMMAND
        )
        val wordList = "[" + keywords.joinToString(",") { "\"$it\"" } + "]"
        val rec = Recognizer(model, VOSK_SPEECH_SAMPLE_RATE, wordList)

        speechService = SpeechService(rec, VOSK_SPEECH_SAMPLE_RATE)
        speechService!!.startListening(this)
    }

    private fun initModel() {
        StorageService.unpack(this, "model-en-us", "model",
            {
                model = it
                binding.toggleSpeechListenerBtn.isEnabled = true
            }
        ) { exception: IOException ->
            Toast.makeText(this, "Failed to unpack model", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (speechService != null) {
            speechService?.stop()
            speechService?.shutdown()
        }
        speechStreamService?.stop()
    }


    override fun onPartialResult(hypothesis: String?) {
//        Log.d(TAG, "onPartialResult: $hypothesis")
    }

    override fun onResult(hypothesis: String?) {
        val rawCommand = hypothesis ?: ""

        Log.d(TAG, "onResult: $rawCommand")
        try {

            if (rawCommand.isNotEmpty()) {
                val jsonObject = JSONObject(rawCommand);
                if (jsonObject.has("text")) {
                    val command = jsonObject.getString("text")
                    binding.speechTextView.text = command
                    matchCommand(command)
                }

            }
        } catch (_: JSONException) {
        }

    }

    override fun onFinalResult(hypothesis: String?) {
//        Log.d(TAG, "onFinalResult: $hypothesis")
    }

    override fun onError(exception: Exception?) {
        Log.d(TAG, "onError: ${exception?.localizedMessage ?: "Got an error"}")
    }

    override fun onTimeout() {
        Log.d(TAG, "onTimeout")
    }


}