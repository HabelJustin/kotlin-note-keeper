package com.example.notekeeper

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoteListActivityViewModel(myName: String) : ViewModel() {

    var navDrawerItemSelected = R.id.listItems

    private val maxRecentlyViewedNotes = 5
    val recentlyViewedNotes = ArrayList<NoteInfo>(maxRecentlyViewedNotes)

    private var name: String = myName

    private lateinit var timer: CountDownTimer
    private val _seconds = MutableLiveData<Int>()
    private val _finished = MutableLiveData<Boolean>()

    val seconds: LiveData<Int>
            get() = _seconds
    val finished: LiveData<Boolean>
            get() = _finished

    init {
        Log.d("ViewModel", "$name")
    }

   fun startTimer(){
       timer = object : CountDownTimer(10000, 1000){
           override fun onFinish() {
               _finished.value = true
           }

           override fun onTick(millisUntilFinished: Long) {
              val timeLeft = millisUntilFinished/1000
               _seconds.value = timeLeft.toInt()
           }
       }.start()
   }

    fun stopTimer(){
        timer.cancel()
    }

    fun addToRecentlyViewedNotes(note: NoteInfo) {
        // Check if selection is already in the list
        val existingIndex = recentlyViewedNotes.indexOf(note)
        if (existingIndex == -1) {
            // it isn't in the list...
            // Add new one to beginning of list and remove any beyond max we want to keep
            recentlyViewedNotes.add(0, note)
            for (index in recentlyViewedNotes.lastIndex downTo maxRecentlyViewedNotes)
                recentlyViewedNotes.removeAt(index)
        } else {
            // it is in the list...
            // Shift the ones above down the list and make it first member of the list
            for (index in (existingIndex - 1) downTo 0)
                recentlyViewedNotes[index + 1] = recentlyViewedNotes[index]
            recentlyViewedNotes[0] = note
        }
    }
}