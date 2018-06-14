package com.example.musick
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.R.raw
import android.os.Environment
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import java.io.BufferedInputStream
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)

        val ctr_button = findViewById(R.id.ctr) as Button
        val listView=findViewById<ListView>(R.id.Music)

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        val StringArray = path.list().toMutableList()
        val StringArrayIterator = StringArray.iterator()
        for (Song in StringArray){
            if (Song.takeLast(4) != ".mp3"){
                StringArrayIterator.remove()
            }
        }
        val adapter = ArrayAdapter<String>(this, R.layout.activity_listview, StringArray)
        listView.adapter = adapter;
        val pos= StringArray[0]
        val filePos = Uri.parse(path.absolutePath+'/'+pos)
        var mediaPlayer = MediaPlayer.create(this, filePos)

        listView.onItemClickListener=AdapterView.OnItemClickListener{adapterView, view, position, id ->
            val pos= StringArray[position]
            val filePos = Uri.parse(path.absolutePath+'/'+pos)
            mediaPlayer = MediaPlayer.create(this, filePos)

            val duration = Toast.LENGTH_LONG
            val toast = Toast.makeText(this,"Playing "+pos,duration)
            toast.show()
        }
        ctr_button.setOnClickListener{
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause()
            }
            else{
                mediaPlayer.start()
            }
        }
    }
}
