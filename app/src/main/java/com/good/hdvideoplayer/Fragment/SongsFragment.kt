package com.good.hdvideoplayer.Fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.good.hdvideoplayer.Activity.MusicPlayerActivity
import com.good.hdvideoplayer.R
import com.good.hdvideoplayer.adapter.MusicAdapter
import com.good.hdvideoplayer.model.MusicModel

class SongsFragment : Fragment() {

    private var originalList: List<MusicModel> = listOf()


    private lateinit var recyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private var musicList: List<MusicModel> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_songs,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerSongs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        checkPermissionAndLoadSongs()
    }

    private fun checkPermissionAndLoadSongs() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
        } else {
            loadSongs()
        }
    }

    fun filterSongs(query: String) {
        musicList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true)
            }
        }
        musicAdapter.updateList(musicList)
    }


    private fun loadSongs() {
        originalList = getAllAudioFromStorage(requireContext()) // ✅ Save full list
        musicList = originalList
        musicAdapter = MusicAdapter(requireContext(), musicList) { music ->
            val intent = Intent(requireContext(), MusicPlayerActivity::class.java)
            intent.putExtra("songId", music.id)
            intent.putExtra("songPath", music.path)
            intent.putExtra("songTitle", music.title)
            intent.putExtra("songArtist", music.artist)
            intent.putExtra("songDuration", music.duration)
            startActivity(intent)
        }
        recyclerView.adapter = musicAdapter
    }

    private fun getAllAudioFromStorage(context: Context): List<MusicModel> {
        val temList = mutableListOf<MusicModel>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = context.contentResolver.query(uri, projection, selection, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val pathCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            do {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol)
                val path = cursor.getString(pathCol)
                val artist = cursor.getString(artistCol)
                val album = cursor.getString(albumCol)
                val duration = cursor.getLong(durationCol)
                val albumId = cursor.getLong(albumIdCol)

                temList.add(MusicModel(id, title, artist, album, duration, path, albumId))
            } while (cursor.moveToNext())

            cursor.close()
        }
        return temList
    }



}