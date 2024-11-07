package com.example.agapimenatragoudia.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.agapimenatragoudia.data.domain.SongRepository
import com.example.agapimenatragoudia.data.entity.AppDatabase
import com.example.agapimenatragoudia.data.entity.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class SongViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SongRepository
    val allSongs: LiveData<List<SongEntity>>
    private val _currentSong = MutableLiveData<SongEntity?>()
    val currentSong: LiveData<SongEntity?> get() = _currentSong
    private val _nextSong = MutableLiveData<SongEntity?>()
    val nextSong : LiveData<SongEntity?> get() = _nextSong
    private val _previousSong = MutableLiveData<SongEntity?>()
    val previousSong : LiveData<SongEntity?> get() = _previousSong
    private var currentIndex: Int = -1

    init {
        val songDao = AppDatabase.getDatabase(application).songDao()
        repository = SongRepository(songDao)
        allSongs = repository.allSongs
    }

    fun insert(song: SongEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertSong(song)
    }

    fun deleteSong(song: SongEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteSong(song)
        if (_currentSong.value == song ) {
            _currentSong.value = null
             currentIndex = -1
        } else if (_nextSong.value == song) {
            _nextSong.value = null
            currentIndex = -1
        }else if (previousSong.value == song){
            _previousSong.value = null
            currentIndex = -1
        }
    }
    private val _currentSongIndex = MutableLiveData<Int>()
    val currentSongIndex: LiveData<Int> = _currentSongIndex
    enum class RepeatMode {
        NO_REPEAT,
        REPEAT_ONE,
        REPEAT_ALL
    }

    private val _repeatMode = MutableLiveData<RepeatMode>(RepeatMode.NO_REPEAT)
    val repeatMode: LiveData<RepeatMode> get() = _repeatMode

    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
    }
    fun setCurrentSongIndex(index: Int) {
        currentIndex = index
        _currentSongIndex.value = currentIndex
        _currentSong.value = allSongs.value?.get(currentIndex)
    }
    fun getCurrentSong(): SongEntity? {
        return allSongs.value?.get(currentIndex)
    }
    fun playSong(index: Int) {
        val song = allSongs.value
        Log.d("SongPlaying", "Playing song: ${allSongs.value}")
    }
    fun nextTrack(): SongEntity? {
        val songs = allSongs.value
        if (!songs.isNullOrEmpty()) {
            currentIndex = (currentIndex + 1) % songs.size
            _nextSong.value = songs[currentIndex]
            return _nextSong.value
        }
        return null
    }

    fun previousTrack(): SongEntity? {
        val songs = allSongs.value
        if (!songs.isNullOrEmpty()) {
           currentIndex = if (currentIndex - 1 < 0) songs.size - 1 else currentIndex - 1
            _previousSong.value = songs[currentIndex]
            return _previousSong.value
        }
        return null
    }
}