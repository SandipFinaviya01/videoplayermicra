package com.good.hdvideoplayer.viewholder;

import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.good.hdvideoplayer.databinding.VideoListItem3Binding;
import com.good.hdvideoplayer.databinding.VideoListItemBinding;
import com.good.hdvideoplayer.model.Folder;
;

public class VideoListHolder extends RecyclerView.ViewHolder {

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;

    public interface OnVideoListListner{
        void onVideoListItemClick(Folder folder);
    }
    private OnVideoListListner videoListListner;
    private VideoListItemBinding videoListItemBinding;
    private VideoListItem3Binding videoListItem3Binding;

    public VideoListHolder(ViewDataBinding binding,OnVideoListListner videoListListner,int viewType) {
        super(binding.getRoot());
        if (viewType == VIEW_TYPE_BIG) {
            this.videoListItemBinding = (VideoListItemBinding) binding;
            this.videoListListner = videoListListner;
        }else{
            this.videoListItem3Binding = (VideoListItem3Binding) binding;
            this.videoListListner = videoListListner;
        }
    }


    public void setItem(Folder folder,int val){
        if(val==1){
            videoListItemBinding.setFolder(folder);
            videoListItemBinding.executePendingBindings();
            videoListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoListListner.onVideoListItemClick(folder);
                }
            });
        }else if(val==2){
            videoListItem3Binding.setFolder(folder);
            videoListItem3Binding.executePendingBindings();
            videoListItem3Binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoListListner.onVideoListItemClick(folder);
                }
            });
        }

    }
}
