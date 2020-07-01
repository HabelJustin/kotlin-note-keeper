package com.example.notekeeper

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteRecyclerAdapter(private val context: Context, private val notes: List<NoteInfo>) : RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>(){

    // to inflate layout
    private val layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val textCourse = itemView?.findViewById<TextView>(R.id.courseTitle)
        val textTitle = itemView?.findViewById<TextView>(R.id.textTitle)
        var notePosition = 0

        init {
            itemView?.setOnClickListener{
               Intent(context, MainActivity::class.java).also {
                    it.putExtra(EXTRA_NOTE_POSITION, notePosition)
                    context.startActivity(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.note_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.textCourse?.text = note.course.title
        holder.textTitle?.text = note.title
        holder.notePosition = position
    }
}