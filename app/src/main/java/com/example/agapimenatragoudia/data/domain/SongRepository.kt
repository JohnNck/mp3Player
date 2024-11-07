package com.example.agapimenatragoudia.data.domain

import androidx.lifecycle.LiveData
import com.example.agapimenatragoudia.data.entity.SongDao
import com.example.agapimenatragoudia.data.entity.SongEntity

class SongRepository(private val songDao: SongDao) {

    val allSongs: LiveData<List<SongEntity>> = songDao.getAllSongs()

    suspend fun insertSong(song: SongEntity) {
        songDao.insertSong(song)
    }

    suspend fun deleteSong(song: SongEntity){
        songDao.deleteSong(song)
    }
}
