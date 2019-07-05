package com.micra.videoplayermicra.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.micra.videoplayermicra.Activity.VideoListActivity;
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
    private VideoListItem2Binding binding;
    private VideoListSubHolder.OnVideoCellListner onVideoCellListner;
    private SparseBooleanArray mSparseBoolMultiSelect = new SparseBooleanArray();
    public static boolean action = false;
    private ActionMode mActionMode;
    private Context ctx;

    public VideoListSubAdapater(List<VideoItem> videoItemList,VideoListSubHolder.OnVideoCellListner onVideoCellListner) {
        this.videoItemList = videoItemList;
        this.onVideoCellListner = onVideoCellListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        videoListItem2Binding = DataBindingUtil.inflate(inflater, R.layout.video_list_item2,parent,false);
        return new VideoListSubHolder(videoListItem2Binding,onVideoCellListner);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoListSubHolder vlsh = (VideoListSubHolder) holder;
        vlsh.setItem(videoItemList.get(position),mSparseBoolMultiSelect.get(position));
        binding = vlsh.getBinding();
        binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mActionMode == null) {
                    AppCompatActivity activity = (AppCompatActivity) ctx;
                    mActionMode = activity.startSupportActionMode(mActioModeCallback);
                    notifyDataSetChanged();
                }
                action = true;
                if (mSparseBoolMultiSelect.get(position)) {
                    binding.deleteCheck.setChecked(false);
                    binding.getRoot().setSelected(false);
                    mSparseBoolMultiSelect.delete(position);
                    if (mSparseBoolMultiSelect.size() == 0) {
                        mActionMode.finish();
                        return true;
                    }
                } else {
                    binding.deleteCheck.setChecked(true);
                    binding.getRoot().setSelected(true);
                    mSparseBoolMultiSelect.put(position, true);
                }


                mActionMode.setTitle(String.format("%d selected", mSparseBoolMultiSelect.size()));

                return true;
            }
        });
    }

    private final ActionMode.Callback mActioModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
                    alertDialog.setTitle("Delete videos from device?");
                    alertDialog.setMessage(mSparseBoolMultiSelect.size() + " videos will be deleted permanently from device.");
                    alertDialog.setPositiveButton("DELETE", (dialog, which) -> deleteall());
                    alertDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
                    alertDialog.show();
                    return true;
                case R.id.play:
                    playall();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            action = false;
            mActionMode = null;
            mSparseBoolMultiSelect.clear();
            notifyDataSetChanged();
        }
    };

    private void playall() {

    }

    private void deleteall() {

    }


    @Override
    public int getItemCount() {
        return videoItemList != null?videoItemList.size():0;
    }

    public void remove(int position) {
        videoItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, videoItemList.size());
    }

    public SparseBooleanArray getmSparseBoolMultiSelect() {
        return mSparseBoolMultiSelect;
    }

    public void setmSparseBoolMultiSelect(SparseBooleanArray mSparseBoolMultiSelect) {
        this.mSparseBoolMultiSelect = mSparseBoolMultiSelect;
    }

    public void clearSParse() {
        mSparseBoolMultiSelect.clear();
    }

    public void delete(int position) {
        mSparseBoolMultiSelect.delete(position);
    }

    public void put(int position, boolean b) {
        mSparseBoolMultiSelect.put(position, true);
    }

    public void setContext(Context context) {
        ctx = context;
    }
}
