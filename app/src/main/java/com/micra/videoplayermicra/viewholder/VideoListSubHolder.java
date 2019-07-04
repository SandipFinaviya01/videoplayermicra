package com.micra.videoplayermicra.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.micra.videoplayermicra.databinding.VideoListItem2Binding;
import com.micra.videoplayermicra.model.VideoItem;

public class VideoListSubHolder extends RecyclerView.ViewHolder {
    private VideoListItem2Binding binding;
    public VideoListSubHolder(VideoListItem2Binding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
    public void setItem(VideoItem videoItem){
        binding.setVideoItem(videoItem);
        binding.executePendingBindings();
    }
}
