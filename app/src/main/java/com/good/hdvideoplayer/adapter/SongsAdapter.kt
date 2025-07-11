package com.good.hdvideoplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.good.hdvideoplayer.R

class SongsAdapter(private val list: List<String>) :
    RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SongViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        return SongViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int,
    ) {
        holder.tv.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tvMusicTitle)
    }
}