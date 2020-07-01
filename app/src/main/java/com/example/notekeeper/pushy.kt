package com.example.notekeeper

import android.app.Activity
import android.app.AlertDialog
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import me.pushy.sdk.Pushy
import java.net.URL

//private class RegisterForPushNotificationsAsync(var mActivity: Activity) :
//    AsyncTask<Void?, Void?, Any>() {
//    protected override fun doInBackground(vararg params: Void): Any {
//        return try {
//            // Register the device for notifications
////            val deviceToken = Pushy.register(getA())
//
//            // Registration succeeded, log token to logcat
////            Log.d("Pushy", "Pushy device token: $deviceToken")
//
//            // Send the token to your backend server via an HTTP GET request
////            URL("https://{YOUR_API_HOSTNAME}/register/device?token=$deviceToken").openConnection()
//
//            // Provide token to onPostExecute()
////            deviceToken
//        } catch (exc: Exception) {
//            // Registration failed, provide exception to onPostExecute()
//            exc
//        }
//    }
//
//    override fun onPostExecute(result: Any) {
//        // Registration failed?
//        if (result is Exception) {
//            // Display error as toast message
//            Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show()
//            return
//        }
//
//        // Registration succeeded, display an alert with the device token
//        AlertDialog.Builder(mActivity)
//            .setTitle("Registration success")
//            .setMessage("Pushy device token: $result\n\n(copy from logcat)")
//            .setPositiveButton(android.R.string.ok, null)
//            .show()
//    }
//
//}