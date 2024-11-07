package com.example.agapimenatragoudia.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agapimenatragoudia.R
import com.example.agapimenatragoudia.data.domain.SongAdapter
import com.example.agapimenatragoudia.data.entity.SongEntity

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val REQUEST_CODE_PERMISSION = 1
    private val songViewModel: SongViewModel by viewModels()
    private val PICK_AUDIO_REQUEST = 1
    private lateinit var recyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private lateinit var songPlaying: SongPlaying

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        songPlaying = SongPlaying()
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = mutableListOf<String>()

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            }

            if (permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(requireActivity(), permissions.toTypedArray(), REQUEST_CODE_PERMISSION)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
           val permissions = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

           if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

           if (permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(requireActivity(), permissions.toTypedArray(), REQUEST_CODE_PERMISSION)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        songAdapter = SongAdapter(emptyList()) { song ->
            songViewModel.deleteSong(song)
           songPlaying.mediaPlayer?.stop()
        }

        recyclerView.adapter = songAdapter

        songViewModel.allSongs.observe(viewLifecycleOwner, Observer { songs ->
            songs?.let { songAdapter.updateSongs(it) }
        })

        val btnAddSong = view.findViewById<Button>(R.id.btnAddSong)
        btnAddSong.setOnClickListener {
            requestPermissions()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, PICK_AUDIO_REQUEST)
                } else {

                    requestPermissions()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val song = getSongDetails(uri)
                song?.let {
                    songViewModel.insert(it)
                }
            }
        }
    }

    private fun getSongDetails(uri: Uri): SongEntity? {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            projection.plus(MediaStore.Audio.Media.DATA)
        }

        val cursor: Cursor? = requireContext().contentResolver.query(
            uri, projection, null, null, null
        )


        cursor?.use {
            if (it.moveToFirst()) {
                val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                return SongEntity(title = title, artist = artist, path = path , position = 1)
            }
        }
        return null
    }

}
