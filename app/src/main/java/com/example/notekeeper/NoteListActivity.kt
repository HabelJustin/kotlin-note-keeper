package com.example.notekeeper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_note_list.*
import me.pushy.sdk.Pushy
import android.Manifest
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.drawer_main.*
import java.util.*
import kotlin.concurrent.schedule

const val REQUEST_STORAGE = 0

class NoteListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    private val noteLayoutManager by lazy { LinearLayoutManager(this) }
    private val noteRecycleAdapter by lazy { NoteRecyclerAdapter(this, DataManger.notes) }

    private val courseLayoutManager by lazy { GridLayoutManager(this, 2) }
    private val courseRecycleAdapter by lazy {
        CourseRecyclerAdapter(
            this,
            DataManger.courses.values.toList()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.drawer_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        // pushy start listen
        Pushy.listen(this)

        Timer("SettingUp", false).schedule(1000) {
            checkPermissionPushy()
        }

        // create notification channel
        createNotificationChannel()

        addList.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Initialize Recycle View layout & adapater
        displayNotes()

        /*
        val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, DataManger.notes)

        listNotes.adapter = listAdapter

        listNotes.setOnItemClickListener { parent, view, position, id ->
            val activityIntent = Intent(this, MainActivity::class.java)
            activityIntent.putExtra(
                EXTRA_NOTE_POSITION, position
            )
            startActivity(activityIntent)
        }
        */
    }

    private fun displayNotes() {
        // Initialize Layout Manager (LinearLayout)
        noteListViews.layoutManager = noteLayoutManager
        // Implement Adapter for Recycle View
        noteListViews.adapter = noteRecycleAdapter

        nav_view.menu.findItem(R.id.listItems).isChecked = true
    }


    private fun displayCourses() {
        noteListViews.layoutManager = courseLayoutManager
        noteListViews.adapter = courseRecycleAdapter

        nav_view.menu.findItem(R.id.galleryItems).isChecked = true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // on nav view item selected
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.listItems -> {
                displayNotes()
            }
            R.id.galleryItems -> {
                displayCourses()
            }
            R.id.share -> {
                val sentObject: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "https://developer.android.com/training/sharing/")
                    putExtra(Intent.EXTRA_TITLE, "Introducing content previews")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sentObject, null)
                startActivity(shareIntent)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun checkPermissionPushy() {
        // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_STORAGE
            )
        } else {
            pushyRegisterDevice()
        }
    }


    // @Pushy - register device
    // check if device has beeen registered
    private fun pushyRegisterDevice() {
        if (!Pushy.isRegistered(this)) {
            RegisterPushAsync(this).execute()
        } else {
            runOnUiThread {
                Toast.makeText(this, "Pushy already been Registered!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    // handle request permission callback
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pushyRegisterDevice()
                } else {
                    Toast.makeText(
                        this,
                        "Permission wast not granted, Unable save data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    // populate list view
    private fun populateList() {
        // (listNotes.adapter as ArrayAdapter<NoteInfo>).notifyDataSetChanged()
        noteListViews.adapter?.notifyDataSetChanged()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("channel01", name, importance).apply {
                description = descriptionText
            }

            // Register the channel into the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onResume() {
        super.onResume()
        populateList()
    }
}