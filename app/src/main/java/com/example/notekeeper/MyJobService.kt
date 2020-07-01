package com.example.notekeeper

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class MyJobService : JobService() {

    companion object {
        private const val TAG = "ExampleJobService"
        private const val TIME_SLEEP_MILLISECONDS: Long = 1000
    }

    private var jobCanceled: Boolean = false

    private fun doBackgroundWork(params: JobParameters?) {
        Thread(Runnable {
            kotlin.run {
                for (i: Int in 0 until 9) {
                    Log.d(TAG, "run: $i")

                    if (jobCanceled) {
                        return@run
                    }

                    Thread.sleep(TIME_SLEEP_MILLISECONDS)
                }
                Log.d(TAG, "Job finish")
                jobFinished(params, false)
            }
        }).start()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        doBackgroundWork(params)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Job canceled before completion")
        jobCanceled = true
        return true
    }
}