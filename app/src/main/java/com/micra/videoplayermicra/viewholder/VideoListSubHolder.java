package com.micra.videoplayermicra.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.micra.videoplayermicra.adapter.VideoListSubAdapater;
import com.micra.videoplayermicra.databinding.VideoListItem2Binding;
import com.micra.videoplayermicra.model.VideoItem;

import java.util.List;

public class VideoListSubHolder extends RecyclerView.ViewHolder {
    private VideoListItem2Binding binding;

    public VideoListItem2Binding getBinding() {
        return binding;
    }

    public void ChechChange(boolean b) {
        binding.deleteCheck.setChecked(b);
    }

    public interface OnVideoCellListner{
        void onExtraDotClick(VideoItem videoItem, ImageView imageView,int position);
        void onVideoItemClick(List<VideoItem> videoItemFilterList, int position);
    }

    public OnVideoCellListner getVideoCellListner() {
        return videoCellListner;
    }

    private OnVideoCellListner videoCellListner;
    public VideoListSubHolder(VideoListItem2Binding binding,OnVideoCellListner videoCellListner) {
        super(binding.getRoot());
        this.binding = binding;
        this.videoCellListner = videoCellListner;
    }
    public void setItem(VideoItem videoItem, boolean b){
        binding.setVideoItem(videoItem);

        binding.extraVideoList.setVisibility(VideoListSubAdapater.action?View.INVISIBLE:View.VISIBLE);
        binding.deleteCheck.setVisibility(VideoListSubAdapater.action?View.VISIBLE:View.INVISIBLE);
        binding.getRoot().setSelected(b);
        binding.deleteCheck.setChecked(b);
        binding.extraVideoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoCellListner.onExtraDotClick(videoItem,binding.extraVideoList,getAdapterPosition());
            }
        });
        binding.executePendingBindings();
    }
}
