package com.example.agapimenatragoudia.ui

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.agapimenatragoudia.R
import com.example.agapimenatragoudia.data.entity.SongEntity
import com.example.agapimenatragoudia.ui.SongViewModel.RepeatMode.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException

class SongPlaying : AppCompatActivity() {
    private lateinit var seekBar: SeekBar
    var mediaPlayer: MediaPlayer? = null
    private var runnable: Runnable? = null
    private lateinit var handler: Handler
    private lateinit var songViewModel: SongViewModel
    private var currentSongIndex: Int = -1
    private lateinit var songsList: ArrayList<SongEntity>
    companion object {
        var currentlyPlayingMediaPlayer: MediaPlayer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_music_playing)

        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        songViewModel = ViewModelProvider(this)[SongViewModel::class.java]
        songViewModel.allSongs.observe(this, Observer { songs ->
            Log.d("HomeFragment", "Songs loaded: ${songs.size}")
            songs.forEach {
                Log.d("HomeFragment", "Song: ${it.title}, ${it.artist}")
            }
        })

        seekBar = findViewById(R.id.SbClaping)
        handler = Handler(Looper.getMainLooper())

        var songTitle = intent.getStringExtra("SONG_TITLE")
        var songArtist = intent.getStringExtra("SONG_ARTIST")
        var songPath = intent.getStringExtra("SONG_PATH")
        Log.d("SongPlaying", "Song Title: $songTitle")
        Log.d("SongPlaying", "Song Artist: $songArtist")
        Log.d("SongPlaying", "Song Path: $songPath")

        val fbPlay = findViewById<FloatingActionButton>(R.id.fabPlay)
        val fbStop = findViewById<FloatingActionButton>(R.id.fabRepeat)
        val title = findViewById<TextView>(R.id.tvSongTitle)
        val artist = findViewById<TextView>(R.id.tvSongArtist)
        val nextSong = findViewById<FloatingActionButton>(R.id.nextTrack)
        val previousSong = findViewById<FloatingActionButton>(R.id.previousTrack)

        title.text = songTitle
        artist.text = songArtist
        fbPlay.setBackgroundColor(Color.TRANSPARENT)
        stopSong(fbPlay)
        playPauseSong(songPath, fbPlay)

        fbPlay.setOnClickListener {
            playPauseSong(songPath, fbPlay)
        }

        fbStop.setOnClickListener {
            stopSong(fbPlay)
        }

        currentSongIndex = intent.getIntExtra("SONG_INDEX", -1)
        songsList = intent.getSerializableExtra("SONG_LIST") as ArrayList<SongEntity>

        if (currentSongIndex != -1) {
            songViewModel.setCurrentSongIndex(currentSongIndex)
            songViewModel.playSong(currentSongIndex)
        }

        nextSong.setOnClickListener {
            val newSong = songViewModel.nextTrack()
            if (newSong != null) {
                songTitle = newSong.title
                songArtist = newSong.artist
                songPath = newSong.path
                Log.d("SongPlaying", "Song Title: $songTitle")
                Log.d("SongPlaying", "Song Artist: $songArtist")
                Log.d("SongPlaying", "Song Path: $songPath")
                stopSong(fbPlay)
                playPauseSong(newSong.path, fbPlay)
            }
        }

        previousSong.setOnClickListener {
            val previousSong = songViewModel.previousTrack()
            if (previousSong != null) {
                songTitle = previousSong.title
                songArtist = previousSong.artist
                songPath = previousSong.path
                stopSong(fbPlay)
                playPauseSong(previousSong.path, fbPlay)
            }
        }

        songViewModel.nextSong.observe(this) { song ->
            if (song != null) {
                stopSong(fbPlay)
                title.text = songViewModel.nextSong.value?.title
                artist.text = songViewModel.nextSong.value?.artist
                playPauseSong(songViewModel.nextSong.value?.path, fbPlay)
            }
        }

        songViewModel.previousSong.observe(this) { song ->
            if (song != null) {
                stopSong(fbPlay)
                title.text = songViewModel.previousSong.value?.title
                artist.text = songViewModel.previousSong.value?.artist
                playPauseSong(songViewModel.previousSong.value?.path, fbPlay)
            }
        }

        songViewModel.repeatMode.observe(this) { mode ->
            handleRepeatMode(mode)
        }

        findViewById<FloatingActionButton>(R.id.fabRepeat).setOnClickListener {
            toggleRepeatMode()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        runnable = null
    }

    private fun playPauseSong(songPath: String?, fbPlay: FloatingActionButton) {
        if (mediaPlayer == null) {
            if (songPath != null) {
                try {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(songPath)
                        prepare()
                        start()
                        currentlyPlayingMediaPlayer = this
                        initializeSeekBar()

                        setOnCompletionListener {
                            handleSongCompletion(fbPlay)
                        }
                    }
                    fbPlay.setImageResource(R.drawable.baseline_pause_24)
                } catch (e: IOException) {
                    Log.e("SongPlaying", "IOException: ${e.message}")
                    e.printStackTrace()
                }
            }
        } else {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                fbPlay.setImageResource(R.drawable.ic_play_arrow)
            } else {
                mediaPlayer!!.start()
                fbPlay.setImageResource(R.drawable.baseline_pause_24)
            }
        }
    }

    private fun stopSong(fbPlay: FloatingActionButton) {
        currentlyPlayingMediaPlayer?.let {
            it.stop()
            it.reset()
            it.release()
            currentlyPlayingMediaPlayer = null
            runnable?.let { runnable -> handler.removeCallbacks(runnable) }
            seekBar.progress = 0
            mediaPlayer = null
            fbPlay.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    private fun initializeSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) currentlyPlayingMediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val tvPlayed = findViewById<TextView>(R.id.tvPlayed)
        val tvDue = findViewById<TextView>(R.id.tvDue)

        currentlyPlayingMediaPlayer?.let {
            seekBar.max = it.duration

            runnable = Runnable {
                seekBar.progress = it.currentPosition

                val playedTime = it.currentPosition / 1000
                tvPlayed.text = "$playedTime sec"

                val duration = it.duration / 1000
                val dueTime = duration - playedTime
                tvDue.text = "$dueTime sec"

                runnable?.let { handler.postDelayed(it, 1000) }
            }
            handler.postDelayed(runnable!!, 1000)
        }
    }

    private fun toggleRepeatMode() {
        val newMode = when (songViewModel.repeatMode.value) {
            NO_REPEAT -> REPEAT_ONE
            REPEAT_ONE -> REPEAT_ALL
            REPEAT_ALL -> NO_REPEAT
            else -> NO_REPEAT
        }
        songViewModel.setRepeatMode(newMode)
        updateRepeatButtonUI(newMode)
    }

    private fun updateRepeatButtonUI(mode: SongViewModel.RepeatMode) {
        val repeatButton = findViewById<FloatingActionButton>(R.id.fabRepeat)
        when (mode) {
            NO_REPEAT -> repeatButton.setImageResource(R.drawable.baseline_repeat_24)
            REPEAT_ONE -> repeatButton.setImageResource(R.drawable.baseline_repeat_one_on_24)
            REPEAT_ALL -> repeatButton.setImageResource(R.drawable.baseline_repeat_on_24)
        }
    }

    private fun handleRepeatMode(mode: SongViewModel.RepeatMode) {
        updateRepeatButtonUI(mode)
    }

    private fun handleSongCompletion(fbPlay: FloatingActionButton) {
        when (songViewModel.repeatMode.value) {
            REPEAT_ONE -> {
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
            }
            REPEAT_ALL -> {
                currentSongIndex = (currentSongIndex + 1) % songsList.size
                val newSong = songsList[currentSongIndex]
                findViewById<TextView>(R.id.tvSongTitle).text = newSong.title
                findViewById<TextView>(R.id.tvSongArtist).text = newSong.artist
                stopSong(fbPlay)
                playPauseSong(newSong.path, fbPlay)
            }
            else -> {
                onSongFinish(fbPlay)
            }
        }
    }

    private fun onSongFinish(fbPlay: FloatingActionButton) {
        mediaPlayer?.seekTo(0)
        seekBar.progress = 0
        fbPlay.setImageResource(R.drawable.ic_play_arrow)
    }
}
