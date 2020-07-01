package com.example.notekeeper

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*

private val CURRENT_POSITION = "CURRENT_POSITION"

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{

    private var notePosition: Int = POSITION_NOT_SET
    private var isNext: Boolean = false
    private var isNewNote: Boolean = false
    private var receiver: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapterCourses = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            DataManger.courses.values.toList()
        )

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        mySpinner.adapter = adapterCourses

        notePosition = intent.getIntExtra(EXTRA_NOTE_POSITION, POSITION_NOT_SET)

        // implement onClick item listener interface
        mySpinner.onItemSelectedListener = this

        if (notePosition != POSITION_NOT_SET) {
            displayNote()
            addNote.visibility = View.GONE
        }

        // Build Notification builder
        /*
        var notifyBuilder = NotificationCompat.Builder(this, "channel01")
            .setSmallIcon(R.drawable.ic_ringbell_white_24dp)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle("New Note Created!")
            .setContentText("Thank you!")
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        */

        // on add note
        addNote.setOnClickListener {
            isNewNote = true
            println("isNewNote")

            val note = NoteInfo(
                mySpinner.selectedItem as CourseInfo,
                textNoteTitle.text.toString(),
                textNoteText.text.toString()
            )
            DataManger.notes.add(note)

            // initiate notification
            /* with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                Timer("SettingUp", false).schedule(1000) {
                    notify(0, notifyBuilder.build())
                }
            } */

            // Broadcast to MyReceiver
            // @MY 1st way
             sendBroadcast(Intent(this, MyReceiver::class.java).apply {
                 action = "com.example.notekeeper.SOME_ACTION"
                 putExtra("data", "Notice me senpai!")
             })

            // @MY 2st way
            /* sendBroadcast(Intent("com.example.notekeeper.MY_ACTION").apply {
                putExtra("data", "Broadcast Message!!")
            }) */
            // end of Broadcast to MyReceiver


            // back to parent activity
            finish()
        }


        // register context receiver ()
        registerAirPlaneModeReceiver()


        // Schedule a Job
        scheduleJob()
    }

    private fun displayNote() {
        val note = DataManger.notes[notePosition]
        textNoteTitle.setText(note.title)
        textNoteText.setText(note.text)

        val coursePosition = DataManger.courses.values.indexOf(note.course)
        mySpinner.setSelection(coursePosition)
    }

    // JobSchedule
    private fun scheduleJob(){
        val PERIODIC_TIME: Long = 15 * 60 * 1000

        // Identify Component
        val componentName = ComponentName(this, MyJobService::class.java)

        // Build Job Info
        val jobInfo = JobInfo.Builder(101, componentName).apply {
            setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            setRequiresDeviceIdle(false)
            setRequiresCharging(true)
            setPersisted(true)
            setPeriodic(PERIODIC_TIME)
        }

        // Start Schedule
        val jobScheduler: JobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = jobScheduler.schedule(jobInfo.build())

        val isJobScheduledSuccess = resultCode == JobScheduler.RESULT_SUCCESS
        if(isJobScheduledSuccess){
            runOnUiThread{
                Toast.makeText(this, "Job Scheduled ${if (isJobScheduledSuccess) "SUCCESS" else "FAILED"}", Toast.LENGTH_LONG).show()
            }
        }
    }
    // end of JobSchedule

    private fun cancelJob(){
        val jobScheduler: JobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(101)
        Log.d("MainActivity", "Job CANCELED")
        runOnUiThread{
            Toast.makeText(this, "Job Schedule Canceled!", Toast.LENGTH_LONG).show()
        }
    }


    // Implement Context Receiver
    // @desc - listen for AirPlane Mode
    private fun registerAirPlaneModeReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(context, intent?.action, Toast.LENGTH_LONG).show()
            }
        }
        registerReceiver(receiver, filter)
    }
/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.nextNote -> {
                moveNext()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    private fun moveNext() {
        isNext = true
        val notesSize = DataManger.notes.size - 1
        if (notePosition < notesSize) {
            ++notePosition
        } else {
            // notePosition = 0
        }
        invalidateOptionsMenu()
        displayNote()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (notePosition >= DataManger.notes.lastIndex) {
            val menuItem = menu?.findItem(R.id.nextNote)
            if (menuItem != null) {
                menuItem.icon = getDrawable((R.drawable.ic_block_white_24dp))
                menuItem.isEnabled = false
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    // spinner item selected implementation
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        invalidateOptionsMenu()
        println("POSITION $position")
    }
    // end of spinner item selected implementation

    // lifecycle activity
    override fun onPause() {
        super.onPause()
        if (!isNewNote && notePosition != POSITION_NOT_SET) {
            saveNote()
        }

        // unregister receiver
        unregisterReceiver(receiver)

        // cancel job scheduler
        cancelJob()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun saveNote() {
        val note = DataManger.notes[notePosition]
        note.title = textNoteTitle.text.toString()
        note.text = textNoteText.text.toString()
        note.course = mySpinner.selectedItem as CourseInfo
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_POSITION, notePosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val stateCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION, POSITION_NOT_SET)
        notePosition = stateCurrentPosition
    }
    // end of lifecycle activity

    /*
    override fun onBackPressed() {
        super.onBackPressed()
    }
     */
}