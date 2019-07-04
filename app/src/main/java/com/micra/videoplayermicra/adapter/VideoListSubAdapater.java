package com.micra.videoplayermicra.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.micra.videoplayermicra.R;
import com.micra.videoplayermicra.databinding.VideoListItem2Binding;
import com.micra.videoplayermicra.model.VideoItem;
import com.micra.videoplayermicra.viewholder.VideoListHolder;
import com.micra.videoplayermicra.viewholder.VideoListSubHolder;

import java.util.ArrayList;
import java.util.List;

public class VideoListSubAdapater extends RecyclerView.Adapter {

    private List<VideoItem> videoItemList = new ArrayList<>();
    private VideoListItem2Binding videoListItem2Binding;

    public VideoListSubAdapater(List<VideoItem> videoItemList) {
        this.videoItemList = videoItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        videoListItem2Binding = DataBindingUtil.inflate(inflater, R.layout.video_list_item2,parent,false);
        return new VideoListSubHolder(videoListItem2Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoListSubHolder vlsh = (VideoListSubHolder) holder;
        vlsh.setItem(videoItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoItemList != null?videoItemList.size():0;
    }
}
