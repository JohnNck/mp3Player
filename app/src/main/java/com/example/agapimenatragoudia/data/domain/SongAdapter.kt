package com.example.agapimenatragoudia.data.domain

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agapimenatragoudia.R
import com.example.agapimenatragoudia.ui.SongPlaying
import com.example.agapimenatragoudia.data.entity.SongEntity

class SongAdapter(
    private var songs: List<SongEntity>,
    private val onDeleteClick: (SongEntity)-> Unit,
    ) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return SongViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = songs[position]
        holder.textViewTitle.text = currentSong.title
        holder.textViewArtist.text = currentSong.artist
        holder.deleteButton.setOnClickListener {
            onDeleteClick(currentSong)
        }
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, SongPlaying::class.java).apply {
                putExtra("SONG_TITLE", currentSong.title)
                putExtra("SONG_ARTIST", currentSong.artist)
                putExtra("SONG_PATH", currentSong.path)
                putExtra("SONG_LIST", ArrayList(songs))
                putExtra("SONG_INDEX", position)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = songs.size

    fun updateSongs(newSongs: List<SongEntity>) {
        songs = newSongs
        notifyDataSetChanged()
    }
    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewArtist: TextView = itemView.findViewById(R.id.textViewArtist)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteIcon)
    }
}
