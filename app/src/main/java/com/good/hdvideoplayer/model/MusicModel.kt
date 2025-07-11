package com.good.hdvideoplayer.model


data class MusicModel(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val albumId: Long
)
