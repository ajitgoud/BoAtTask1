package com.example.boattask1.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


 const val ACCEPT_CALL_COMMAND = "accept"
 const val REJECT_CALL_COMMAND = "reject"
private const val ACCEPT_RINGING_CALL_METHOD_NAME = "acceptRingingCall"
private const val END_CALL_METHOD_NAME = "endCall"
private const val TAG = "CallUtils"

fun Activity.matchCommand(
    command: String
) {

    val telecomManager = getSystemService(AppCompatActivity.TELECOM_SERVICE) as TelecomManager
    val telephonyManager = getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
    if (command.equals(ACCEPT_CALL_COMMAND, ignoreCase = true)) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ANSWER_PHONE_CALLS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telecomManager.acceptRingingCall()
        } else {
            acceptTheCall(telephonyManager)
        }
    }
    if (command.equals(REJECT_CALL_COMMAND, ignoreCase = true)) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ANSWER_PHONE_CALLS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            telecomManager.endCall()
        } else {
            rejectTheCall(telephonyManager)
        }
    }
}


private fun invokeCallActionMethods(telephonyManager: TelephonyManager, methodName: String) {
    try {
        val c = Class.forName(telephonyManager.javaClass.name)
        val m = c.getDeclaredMethod("getITelephony")
        m.isAccessible = true
        val telephonyService = m.invoke(telephonyManager)

        val cls = Class.forName(telephonyService.javaClass.name)
        val method = cls.getDeclaredMethod(methodName)

        method.isAccessible = true
        method.invoke(telephonyService)
    } catch (e: Exception) {
        Log.e(TAG, "invokeCallActionMethods: ${e.localizedMessage}")
    }
}

private fun acceptTheCall(telephonyManager: TelephonyManager) {
    invokeCallActionMethods(telephonyManager, ACCEPT_RINGING_CALL_METHOD_NAME)
}

private fun rejectTheCall(telephonyManager: TelephonyManager) {
    invokeCallActionMethods(telephonyManager, END_CALL_METHOD_NAME)
}
