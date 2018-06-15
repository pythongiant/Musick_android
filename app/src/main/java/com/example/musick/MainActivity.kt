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
    fun CheckAction(ctr_button:ImageButton,mediaPlayer:MediaPlayer){
        ctr_button.setOnClickListener{
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause()
                ctr_button.setImageResource(R.drawable.play_struc)
            }
            else{
                ctr_button.setImageResource(R.drawable.pause_struc)
                mediaPlayer.start()
            }
        }
    }


    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val bluePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)

    val StringArray = mutableListOf<String>()

    fun TreatFiles() {
        for (Song in path.list().iterator()) {
            StringArray.add(Song)

        }
        for (Song in bluePath.list().iterator()) {
            StringArray.add(Song)
        }
        val StringArrayIterator = StringArray.iterator()
        for (i in StringArrayIterator) {
            if (i.takeLast(4) != ".mp3") {
                StringArrayIterator.remove()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ctr_button = findViewById(R.id.ctr) as ImageButton
        ctr_button.setImageResource(R.drawable.play_struc)
        val listView=findViewById<ListView>(R.id.Music)
        TreatFiles()

        val values= arrayListOf<HashMap<String,String>>()
        for (song in StringArray){
            val value=HashMap<String,String>()
            value.put("song",song.removeSuffix(".mp3"))
            value.put("artist",song)
            values.add(value)
        }

        val from = arrayOf("song", "artist")//string array
        val to = intArrayOf(R.id.song, R.id.artist)
        val adapter = SimpleAdapter(this,values, R.layout.activity_listview, from,to)
        listView.adapter = adapter;
        val pos= StringArray[0]
        var filePos = Uri.parse(path.absolutePath+'/'+pos)
        var mediaPlayer = MediaPlayer.create(this, filePos)

        listView.onItemClickListener=AdapterView.OnItemClickListener{adapterView, view, position, id ->
            val pos= StringArray[position]
            filePos = Uri.parse(path.absolutePath+'/'+pos)
            if (mediaPlayer.isPlaying){
                mediaPlayer.release()
            }

            if(filePos == null){
                filePos = Uri.parse(bluePath.absolutePath+'/'+pos)
                mediaPlayer = MediaPlayer.create(this, filePos)
            }
            CheckAction(ctr_button,mediaPlayer)

            val duration = Toast.LENGTH_LONG
            val toast = Toast.makeText(this,"Playing "+pos,duration)
            toast.show()
        }
    }
    }

