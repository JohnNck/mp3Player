package com.example.agapimenatragoudia.data.entity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): LiveData<List<SongEntity>>

    @Insert
    suspend fun insertSong(song: SongEntity)

    @Delete
    suspend fun deleteSong(song: SongEntity)

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Int): SongEntity

    @Query("SELECT id FROM songs WHERE position = :position")
    suspend fun getSongIdByPosition(position: Int): Int

  }
