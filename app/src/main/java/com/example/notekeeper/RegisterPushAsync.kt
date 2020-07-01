package com.example.notekeeper

import android.app.Activity
import android.os.AsyncTask
import android.widget.Toast
import me.pushy.sdk.Pushy
import java.lang.Exception

class RegisterPushAsync(private val mActivity: Activity?) : AsyncTask<Void, Void, Any>() {

    override fun doInBackground(vararg params: Void?): Any {
        try {
            // Register the device for notifications
            val deviceToken = Pushy.register(mActivity);

            println("DEVICETOKEN $deviceToken")

            // Registration succeeded, log token to logcat
//            Log.d("Pushy", "Pushy device token: " + deviceToken);

            // Send the token to your backend server via an HTTP GET request
//            new URL("https://{YOUR_API_HOSTNAME}/register/device?token=" + deviceToken).openConnection();

            // Provide token to onPostExecute()
            return deviceToken;
        } catch (e: Exception) {
            // Registration failed, provide exception to onPostExecute()
            return e
        }

    }


    override fun onPostExecute(result: Any?) {
        if(result is Exception){
            // Display error as toast message
            Toast.makeText(mActivity, result.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(mActivity, "Pushy Registered Successfully!", Toast.LENGTH_LONG).show();
    }
}