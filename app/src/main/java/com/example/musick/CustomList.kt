package com.example.musick


import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CustomList(private val context: Activity,
                 private val songs: MutableList<String>, private val SongId: Array<String>) : ArrayAdapter<String>(context, R.layout.activity_listview, songs) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.activity_listview, null, true)
        val txtTitle = rowView.findViewById(R.id.song) as TextView

        val imageView = rowView.findViewById(R.id.artist) as TextView
        return rowView
    }
}