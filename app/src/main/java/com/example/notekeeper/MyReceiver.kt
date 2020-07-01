package com.example.notekeeper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log

private const val TAG = "MyBroadcastReceiver"

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        val pendingResult: PendingResult = goAsync()
        val asyncTask = Task(pendingResult, intent)
        asyncTask.execute()
    }

    private class Task(private val pendingTask: PendingResult, private val intent: Intent): AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String {
            val action = "Action: ${intent.action}\n"
            val msg = "Message: ${intent.extras?.getString("data") ?: "Default Message"}"
            val uri = "URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n"
            return toString().apply { ->
                Log.d(TAG, action)
                Log.d(TAG, msg)
                Log.d(TAG, uri)
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingTask.finish()
        }
    }
}
