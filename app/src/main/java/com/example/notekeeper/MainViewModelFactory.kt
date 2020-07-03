package com.example.notekeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class MainViewModelFactory(private val name: String) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NoteListActivityViewModel::class.java)){
            return NoteListActivityViewModel(name) as T
        }else{
            throw IllegalArgumentException("ViewModel class not found.")
        }
    }


}