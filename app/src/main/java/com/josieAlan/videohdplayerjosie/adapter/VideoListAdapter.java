package com.josieAlan.videohdplayerjosie.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.josieAlan.videohdplayerjosie.model.Folder;
import com.josieAlan.videohdplayerjosie.R;
import com.josieAlan.videohdplayerjosie.databinding.VideoListItemBinding;
import com.josieAlan.videohdplayerjosie.utils.EmployeeDiffCallback;
import com.josieAlan.videohdplayerjosie.viewholder.VideoListHolder;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter {
    private List<Folder> folderList = new ArrayList<>();
    private VideoListItemBinding videoListItemBinding;
    private VideoListHolder.OnVideoListListner videoListListner;
    public VideoListAdapter(VideoListHolder.OnVideoListListner videoListListner) {
        this.folderList = new ArrayList<>();
        this.videoListListner = videoListListner;
    }

    public void updateEmployeeListItems(List<Folder> folders) {
        final EmployeeDiffCallback diffCallback = new EmployeeDiffCallback(this.folderList, folders);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.folderList.clear();
        this.folderList.addAll(folders);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        videoListItemBinding = DataBindingUtil.inflate(inflater, R.layout.video_list_item,parent,false);
        return new VideoListHolder(videoListItemBinding,videoListListner);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoListHolder vlh = (VideoListHolder) holder;
        vlh.setItem(folderList.get(position));
    }

    @Override
    public int getItemCount() {
        return folderList != null?folderList.size():0;
    }
}
