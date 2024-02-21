package com.example.boattask1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.boattask1.databinding.ActivityVospBinding
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.IOException


private const val TAG = "VospActivity"
private const val ACCEPT_CALL_COMMAND = "accept the call"
private const val REJECT_CALL_COMMAND = "reject the call"

private const val PERMISSIONS_REQUEST_RECORD_AUDIO_ANSWER_PHONE = 100

class VospActivity : AppCompatActivity(), RecognitionListener {


    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var speechStreamService: SpeechStreamService? = null
    private lateinit var telecomManager: TelecomManager

    private val binding by lazy {
        ActivityVospBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        initModel()
        checkPermissions()
        setListeners()

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
                android.Manifest.permission.RECORD_AUDIO
            )

        val permissionList = mutableListOf<String>()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val phoneStatePermissionCheck = ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ANSWER_PHONE_CALLS
            )

            if (phoneStatePermissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(android.Manifest.permission.ANSWER_PHONE_CALLS)
            }
        }
        if (audioPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.RECORD_AUDIO)
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
                android.Manifest.permission.RECORD_AUDIO
            )
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }

    private fun stopListening() {
        speechService?.stop()
        speechService?.shutdown()
    }

    private fun startListening() {
        val rec = Recognizer(model, 16000.0f)
        speechService = SpeechService(rec, 16000.0f)
        speechService!!.startListening(this)
    }

    private fun initModel() {
        StorageService.unpack(this, "model-en-us", "model",
            {
                model = it
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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun matchCommand(command: String) {
        if(command.equals(ACCEPT_CALL_COMMAND, ignoreCase = true)){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telecomManager.acceptRingingCall()
            }
        }
        if(command.equals(REJECT_CALL_COMMAND, ignoreCase = true)){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                telecomManager.endCall()
            }
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        Log.d(TAG, "onPartialResult: $hypothesis")
    }

    override fun onResult(hypothesis: String?) {
        val rawCommand = hypothesis ?: ""

        Log.d(TAG, "onResult: $rawCommand")
        if (rawCommand.isNotEmpty()) {
            val index = rawCommand.indexOf(":")
            var command = rawCommand.substring(index + 1).trim()
            command = command.trim('"', '{', '}')
           if(command.length>1){
               command = command.substring(0, command.length-2)
               binding.speechTextView.text = command
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                   matchCommand(command)
               }
           }
        }
    }

    override fun onFinalResult(hypothesis: String?) {
//        binding.speechTextView.text = hypothesis ?: ""

        Log.d(TAG, "onFinalResult: $hypothesis")
    }

    override fun onError(exception: Exception?) {
        Log.d(TAG, "onError: ${exception?.localizedMessage ?: "Got an error"}")
    }

    override fun onTimeout() {
        Log.d(TAG, "onTimeout")
    }


}