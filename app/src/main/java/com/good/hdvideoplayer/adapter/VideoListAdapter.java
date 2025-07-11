package com.good.hdvideoplayer.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.good.hdvideoplayer.R;
import com.good.hdvideoplayer.databinding.VideoListItem3Binding;
import com.good.hdvideoplayer.databinding.VideoListItemBinding;
import com.good.hdvideoplayer.model.Folder;
import com.good.hdvideoplayer.utils.EmployeeDiffCallback;
import com.good.hdvideoplayer.viewholder.VideoListHolder;


import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter {
    private List<Folder> folderList = new ArrayList<>();
    private VideoListItemBinding videoListItemBinding;
    private VideoListItem3Binding videoListItem3Binding;
    private VideoListHolder.OnVideoListListner videoListListner;


    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 3;

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;
    private GridLayoutManager mLayoutManager;

    public VideoListAdapter(VideoListHolder.OnVideoListListner videoListListner,GridLayoutManager layoutManager) {
        this.folderList = new ArrayList<>();
        this.videoListListner = videoListListner;
        this.mLayoutManager = layoutManager;
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
        ViewDataBinding binding;
        if (viewType == VIEW_TYPE_BIG) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = DataBindingUtil.inflate(inflater, R.layout.video_list_item,parent,false);
        }else{
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = DataBindingUtil.inflate(inflater, R.layout.video_list_item3,parent,false);
        }
        return new VideoListHolder(binding,videoListListner,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoListHolder vlh = (VideoListHolder) holder;
        if (getItemViewType(position) == VIEW_TYPE_BIG) {
            vlh.setItem(folderList.get(position),1);
        }else{
            vlh.setItem(folderList.get(position),2);
        }

    }

    @Override
    public int getItemCount() {
        return folderList != null?folderList.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return VIEW_TYPE_BIG;
        } else {
            return VIEW_TYPE_SMALL;
        }
    }
}
