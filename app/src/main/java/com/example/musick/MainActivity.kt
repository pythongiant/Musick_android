package com.example.musick
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.R.raw
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import org.w3c.dom.Text
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import android.media.MediaMetadataRetriever
import android.os.*
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.os.HandlerCompat.postDelayed
import android.view.View
import android.widget.Toast
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener




class MainActivity : AppCompatActivity() {
    var metaRetriver = MediaMetadataRetriever()

    fun CheckAction(ctr_button:ImageButton,mediaPlayer:MediaPlayer){
        ctr_button.setOnClickListener{
            if (mediaPlayer.isPlaying){

                mediaPlayer.pause()
                ctr_button.setImageResource(R.drawable.ic_play_circle_filled_black_24dp)
            }
            else{
                ctr_button.setImageResource(R.drawable.ic_pause_black_24dp)
                mediaPlayer.start()
            }
        }
    }
    var pos_2=0
    var mediaPlayer = MediaPlayer()

    private fun buttonClickCustomAction(state_button:TextView,ctr_button: ImageButton,sub: Int){

        when (sub){
            0 -> pos_2-=1
            1 -> pos_2 +=1
            2 -> pos_2+=0

        }

        //get the items at the clicked position
        val pos= StringArray[pos_2]
        var filePos = Uri.parse(path.absolutePath+'/'+pos)

        if (File(path.absolutePath+'/'+pos).exists()) {
            metaRetriver.setDataSource(path.absolutePath + '/' + pos)
        }
        else {
            metaRetriver.setDataSource(bluePath.absolutePath + '/' + pos)
        }

        if(mediaPlayer.isPlaying){
            mediaPlayer.release()
        }

        if(MediaPlayer.create(this, filePos) == null){
            filePos = Uri.parse(bluePath.absolutePath+'/'+pos)

            if(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)!=null){
                state_button.text= metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).take(30)
            }
            else {
                state_button.text=pos.removeSuffix(".mp3").take(30)
            }
            mediaPlayer = MediaPlayer.create(this, filePos)
            ctr_button.setImageResource(R.drawable.ic_pause_black_24dp)
            mediaPlayer.start()
        }
        else{

            filePos = Uri.parse(path.absolutePath+'/'+pos)

            if(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)!=null){
                state_button.text= metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            }
            else {
                state_button.text=pos.removeSuffix(".mp3")
            }
            mediaPlayer = MediaPlayer.create(this, filePos)
            ctr_button.setImageResource(R.drawable.ic_pause_black_24dp)


            mediaPlayer.start()
        }

        CheckAction(ctr_button,mediaPlayer)

        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(this,"Playing "+pos,duration)
        toast.show()

    }
    //gather files ,remove .mp3
    val path=File("/storage/self/primary/Download/")

    val bluePath =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    val sdPath = Environment.getExternalStorageDirectory()

    val StringArray = mutableListOf<String>() //String of Songs
    val values = arrayListOf<HashMap<String, String>>()

    private fun TreatFiles() {
        if(path.list()!=null){
        for (Song in path.list().iterator()) {
            StringArray.add(Song)

        }
            }
        if(bluePath.list()!=null){
        for (Song in bluePath.list().iterator()) {
            StringArray.add(Song)
        }
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

        //Initialise everything
        val ctr_button = findViewById<ImageButton>(R.id.ctr)
        val ctr_prev_button = findViewById<ImageButton>(R.id.ctr_prev)
        val ctr_next_button = findViewById<ImageButton>(R.id.ctr_next)
        val state_button = findViewById<TextView>(R.id.State)
        val albumArt = findViewById<ImageView>(R.id.art)
        val seekBar=findViewById<SeekBar>(R.id.seek)

        //TreatFiles (remove .mp3 , add them to array
        TreatFiles()
        for (song in StringArray) {
            val value = HashMap<String, String>()

            if (File(path.absolutePath+'/'+song).exists()) {
                metaRetriver.setDataSource(path.absolutePath + '/' + song)
            }
            else {
                metaRetriver.setDataSource(bluePath.absolutePath + '/' + song)
            }
            if(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)!=null){
                value.put("artist", metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST))
            }
            else{
                value.put("artist","unknown")
            }
            if(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)!=null){
                value.put("song", metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))
            }
            else {
                value.put("song",song.removeSuffix(".mp3"))
            }
            values.add(value)
        }

        ctr_button.setImageResource(R.drawable.ic_play_circle_filled_black_24dp)
        val listView = findViewById<ListView>(R.id.Music)
        //add songs, artists to adapter
        val from = arrayOf("song", "artist")//string array
        val to = intArrayOf(R.id.song, R.id.artist)
        val adapter = SimpleAdapter(this, values, R.layout.activity_listview, from, to)
        listView.adapter = adapter

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                CheckAction(ctr_button,mediaPlayer)

                mediaPlayer.setOnCompletionListener {
                    seekBar.progress=0
                    mediaPlayer.seekTo(0)
                    buttonClickCustomAction(state_button,ctr_button,1)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                if (fromUser) {
                    mediaPlayer.pause()

                    mediaPlayer.seekTo(progress)

                    mediaPlayer.start()
                }

            }
        })// Init
        var handle:Handler = Handler()
        var runnable:Runnable =    object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying){
                    seekBar.progress = mediaPlayer.currentPosition

                }
                handle.postDelayed(this, 1000)
            }
        }
    handle.postDelayed(runnable,1000)

        mediaPlayer.setOnCompletionListener {
            seekBar.progress=0
            mediaPlayer.seekTo(0)
            buttonClickCustomAction(state_button,ctr_button,1)
        }
        Log.i("Path",path.toString())
        Log.i("BluePath",bluePath.toString())

        listView.onItemClickListener=AdapterView.OnItemClickListener{adapterView, view, position, id ->
            //get the items at the clicked position

            try{
                var song = StringArray[position]
                var metarRetr = MediaMetadataRetriever()
                metarRetr.setDataSource(bluePath.absolutePath + '/' + song)

                var art = BitmapFactory.decodeByteArray(metarRetr.embeddedPicture,0,metarRetr.embeddedPicture.count())
                albumArt.setImageBitmap(art)

            }
            catch (e:Exception){
                Log.i("metaRetriver called","fail")
                albumArt.setImageResource(R.drawable.ic_library_music_black_24dp)
            }
            pos_2=position
            buttonClickCustomAction(state_button,ctr_button,2)

            seekBar.max=mediaPlayer.duration


            seekBar.progress=0
            mediaPlayer.seekTo(0)
            mediaPlayer.setOnCompletionListener {
                seekBar.progress=0
                mediaPlayer.seekTo(0)
                buttonClickCustomAction(state_button,ctr_button,1)


            }

        }

        ctr_prev_button.setOnClickListener{


            seekBar.progress=0
            mediaPlayer.seekTo(0)
            buttonClickCustomAction(state_button,ctr_button,0)
            mediaPlayer.setOnCompletionListener {
                seekBar.progress=0
                mediaPlayer.seekTo(0)
                buttonClickCustomAction(state_button,ctr_button,1)


            }

        }
        ctr_next_button.setOnClickListener{

            seekBar.progress=0
            mediaPlayer.seekTo(0)
            buttonClickCustomAction(state_button,ctr_button,1)

            mediaPlayer.setOnCompletionListener {
                seekBar.progress=0
                mediaPlayer.seekTo(0)
                buttonClickCustomAction(state_button,ctr_button,1)


            }
        }

        }
    }

/*
*
* try{
                var song = StringArray[position]
                var metarRetr = MediaMetadataRetriever()
                metarRetr.setDataSource(bluePath.absolutePath + '/' + song)

                var art = BitmapFactory.decodeByteArray(metarRetr.embeddedPicture,0,metarRetr.embeddedPicture.count())
                albumArt.setImageBitmap(art)

            }
            catch (e:Exception){
                Log.i("metaRetriver called","fail")
                albumArt.setImageResource(R.drawable.ic_library_music_black_24dp)
            }
* */





