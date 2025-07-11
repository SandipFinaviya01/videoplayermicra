package com.good.hdvideoplayer.Activity

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.good.hdvideoplayer.AudioVisualizerView
import com.good.hdvideoplayer.R
import com.good.hdvideoplayer.model.MusicModel
import java.util.Random
import java.util.concurrent.TimeUnit

class MusicPlayerActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var visualizer: Visualizer? = null
    private lateinit var visualizerView: AudioVisualizerView

    private lateinit var musicList: List<MusicModel>
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var tvSongTitle: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalDuration: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnRepeat: ImageButton
    private lateinit var btnShuffle: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrevious: ImageButton

    private var isPlaying = false
    private var isRepeat = false
    private var isShuffle = false
    private var currentSongIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_player)

        // Initialize views
        tvSongTitle = findViewById(R.id.tvSongTitle)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalDuration = findViewById(R.id.tvTotalDuration)
        seekBar = findViewById(R.id.seekBar)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        visualizerView = findViewById(R.id.visualizerView)


        btnRepeat = findViewById(R.id.btnRepeat)
        btnShuffle = findViewById(R.id.btnShuffle)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)

        musicList = getAllSongsFromStorage(this)

        val path = intent.getStringExtra("songPath")
        val title = intent.getStringExtra("songTitle") ?: "Unknown Song"
        tvSongTitle.text = title

        if (path != null) {
            val uri = Uri.parse(path)
            currentSongIndex = musicList.indexOfFirst { it.path == path }
            setupMediaPlayer(uri)
        } else {
            Toast.makeText(this, "No file path found", Toast.LENGTH_SHORT).show()
        }

        // Control buttons
        btnPlayPause.setOnClickListener {
            if (::mediaPlayer.isInitialized) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    isPlaying = false
                    btnPlayPause.setImageResource(R.drawable.ic_play)
                } else {
                    mediaPlayer.start()
                    isPlaying = true
                    btnPlayPause.setImageResource(R.drawable.ic_pause)
                    handler.post(updateSeekBar)
                }
            }
        }

        btnRepeat.setOnClickListener {
            isRepeat = !isRepeat
            Toast.makeText(this, if (isRepeat) "Repeat On" else "Repeat Off", Toast.LENGTH_SHORT).show()
            btnRepeat.setColorFilter(if (isRepeat) Color.YELLOW else Color.WHITE)
        }

        btnShuffle.setOnClickListener {
            isShuffle = !isShuffle
            Toast.makeText(this, if (isShuffle) "Shuffle On" else "Shuffle Off", Toast.LENGTH_SHORT).show()
            btnShuffle.setColorFilter(if (isShuffle) Color.YELLOW else Color.WHITE)
        }

        btnNext.setOnClickListener { playNext() }
        btnPrevious.setOnClickListener { playPrevious() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && ::mediaPlayer.isInitialized) {
                    mediaPlayer.seekTo(progress)
                    tvCurrentTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    private fun setupMediaPlayer(uri: Uri) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@MusicPlayerActivity, uri)
            setOnPreparedListener {
                seekBar.max = duration
                tvTotalDuration.text = formatTime(duration)
                start()
                this@MusicPlayerActivity.isPlaying = true
                btnPlayPause.setImageResource(R.drawable.ic_pause)
                setUpVisualizer()
                handler.post(updateSeekBar)
            }
            setOnCompletionListener {
                if (isRepeat) {
                    playSong(currentSongIndex)
                } else if (isShuffle) {
                    val random = (musicList.indices).random()
                    playSong(random)
                } else {
                    playNext()
                }
            }
            prepareAsync()
        }
    }
    private fun setUpVisualizer() {
        try {
            val sessionId = mediaPlayer.audioSessionId
            if (sessionId == AudioManager.ERROR || sessionId == 0) {
                throw RuntimeException("Invalid audio session ID")
            }

            visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]

                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer?,
                        waveform: ByteArray?,
                        samplingRate: Int
                    ) {
                        waveform?.let { visualizerView.updateVisualizer(it) }
                    }

                    override fun onFftDataCapture(
                        visualizer: Visualizer?,
                        fft: ByteArray?,
                        samplingRate: Int
                    ) {
                        // Not used here
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false)

                enabled = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
            runFakeVisualizer()
        }
    }


    private fun runFakeVisualizer() {
        handler = Handler(Looper.getMainLooper())
        val random = Random()

        runnable = object : Runnable {
            override fun run() {
                val fakeData = ByteArray(128) { (random.nextInt(255) - 128).toByte() }
                visualizerView.updateVisualizer(fakeData)
                handler.postDelayed(this, 50)
            }
        }

        handler.post(runnable)
    }





    private fun playSong(index: Int) {
        if (index < 0 || index >= musicList.size) return
        currentSongIndex = index
        val song = musicList[index]
        tvSongTitle.text = song.title
        mediaPlayer.reset()
        mediaPlayer.setDataSource(this, Uri.parse(song.path))
        mediaPlayer.setOnPreparedListener {
            seekBar.max = it.duration
            tvTotalDuration.text = formatTime(it.duration)
            it.start()
            btnPlayPause.setImageResource(R.drawable.ic_pause)
            handler.post(updateSeekBar)
        }
        mediaPlayer.prepareAsync()
    }

    private fun playNext() {
        currentSongIndex++
        if (currentSongIndex >= musicList.size) currentSongIndex = 0
        playSong(currentSongIndex)
    }

    private fun playPrevious() {
        currentSongIndex--
        if (currentSongIndex < 0) currentSongIndex = musicList.size - 1
        playSong(currentSongIndex)
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                val currentPosition = mediaPlayer.currentPosition
                seekBar.progress = currentPosition
                tvCurrentTime.text = formatTime(currentPosition)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getAllSongsFromStorage(context: Context): List<MusicModel> {
        val musicList = mutableListOf<MusicModel>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)
        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val title = it.getString(titleCol)
                val artist = it.getString(artistCol)
                val album = it.getString(albumCol)
                val duration = it.getLong(durationCol)
                val path = it.getString(pathCol)
                val albumId = it.getLong(albumIdCol)

                musicList.add(MusicModel(id, title, artist, album, duration, path, albumId))
            }
        }
        return musicList
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            visualizer?.release()
        } catch (_: Exception) {
        }
        if (::handler.isInitialized) {
            handler.removeCallbacks(runnable)
        }
    }


}