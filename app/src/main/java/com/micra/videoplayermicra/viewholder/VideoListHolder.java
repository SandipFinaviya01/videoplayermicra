package com.micra.videoplayermicra.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.micra.videoplayermicra.Model.Folder;
import com.micra.videoplayermicra.databinding.VideoListItemBinding;

public class VideoListHolder extends RecyclerView.ViewHolder {
    private VideoListItemBinding videoListItemBinding;
    public VideoListHolder(VideoListItemBinding videoListItemBinding) {
        super(videoListItemBinding.getRoot());
        this.videoListItemBinding = videoListItemBinding;
    }

    public void setItem(Folder folder){
        videoListItemBinding.setFolder(folder);
        videoListItemBinding.executePendingBindings();
    }
}
