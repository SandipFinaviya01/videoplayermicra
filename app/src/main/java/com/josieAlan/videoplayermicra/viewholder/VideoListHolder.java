package com.josieAlan.videoplayermicra.viewholder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.josieAlan.videoplayermicra.model.Folder;
import com.josieAlan.videoplayermicra.databinding.VideoListItemBinding;

public class VideoListHolder extends RecyclerView.ViewHolder {
    public interface OnVideoListListner{
        void onVideoListItemClick(Folder folder);
    }
    private OnVideoListListner videoListListner;
    private VideoListItemBinding videoListItemBinding;
    public VideoListHolder(VideoListItemBinding videoListItemBinding,OnVideoListListner videoListListner) {
        super(videoListItemBinding.getRoot());
        this.videoListItemBinding = videoListItemBinding;
        this.videoListListner = videoListListner;
    }

    public void setItem(Folder folder){
        videoListItemBinding.setFolder(folder);
        videoListItemBinding.executePendingBindings();
        videoListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoListListner.onVideoListItemClick(folder);
            }
        });
    }
}
