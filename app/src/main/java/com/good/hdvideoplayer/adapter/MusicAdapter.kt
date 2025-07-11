package com.good.hdvideoplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.good.hdvideoplayer.Fragment.SongsFragment
import com.good.hdvideoplayer.R
import com.good.hdvideoplayer.model.MusicModel


class MusicAdapter(
    private val context: Context,
    private var songList: List<MusicModel>,
    private val onItemClick: (MusicModel) -> Unit
) : RecyclerView.Adapter<MusicAdapter.SongViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song,parent,false)
        return SongViewHolder(view)
    }


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.tvTitle.text = song.title
        holder.tvArtist.text = song.artist

        holder.itemView.setOnClickListener {
            onItemClick(song) // ✅ Song play from here
        }

        holder.imgMore.setOnClickListener {
            showPopupMenu(it, song)
        }
    }


    private fun showPopupMenu(view: View,song: MusicModel){
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_song_options,popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId){
                R.id.menu_play_next -> {
                    popupMenu.dismiss() // ✅ Dismiss popup
                    onItemClick(song)    // ✅ Trigger song play
                    true
                }
                R.id.menu_add_to_queue ->{
                    Toast.makeText(context, "Added to queue", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_add_to_playlist ->{
                    true
                }
                R.id.menu_change_cover -> {
                    true
                }
                R.id.menu_edit -> {
                    true
                }
                R.id.menu_delete -> {
                    true
                }
                R.id.menu_share -> {
                    true
                }
                R.id.menu_rename -> {
                    true
                }
                R.id.menu_properties -> {
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return songList.size
    }
    fun updateList(newList: List<MusicModel>) {
        this.songList = newList
        notifyDataSetChanged()
    }


    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val imgMore: ImageView = itemView.findViewById(R.id.imgMore)

    }

}

//
//            0 -> SongsFragment()
////            1 -> FoldersFragment()
////            2 -> AlbumsFragment()
////            3 -> ArtistsFragment()
//            else -> SongsFragment()

