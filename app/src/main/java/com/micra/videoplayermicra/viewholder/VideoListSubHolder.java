package com.micra.videoplayermicra.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.micra.videoplayermicra.databinding.VideoListItem2Binding;
import com.micra.videoplayermicra.model.VideoItem;

public class VideoListSubHolder extends RecyclerView.ViewHolder {
    private VideoListItem2Binding binding;
    public interface OnVideoCellListner{
        void onExtraDotClick(VideoItem videoItem, ImageView imageView);
    }
    private OnVideoCellListner videoCellListner;
    public VideoListSubHolder(VideoListItem2Binding binding,OnVideoCellListner videoCellListner) {
        super(binding.getRoot());
        this.binding = binding;
        this.videoCellListner = videoCellListner;
    }
    public void setItem(VideoItem videoItem){
        binding.setVideoItem(videoItem);
        binding.executePendingBindings();
        binding.extraVideoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoCellListner.onExtraDotClick(videoItem,binding.extraVideoList);
            }
        });
    }
}
