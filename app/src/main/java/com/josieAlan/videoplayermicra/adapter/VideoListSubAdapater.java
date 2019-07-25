package com.josieAlan.videoplayermicra.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
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

import com.josieAlan.videoplayermicra.R;
import com.josieAlan.videoplayermicra.databinding.VideoListItem2Binding;
import com.josieAlan.videoplayermicra.model.VideoItem;
import com.josieAlan.videoplayermicra.viewholder.VideoListSubHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListSubAdapater extends RecyclerView.Adapter {

    private List<VideoItem> videoItemList = new ArrayList<>();
    private List<VideoItem> videoItemFilterList = new ArrayList<>();
    private VideoListItem2Binding videoListItem2Binding;
    private VideoListItem2Binding binding;
    private VideoListSubHolder.OnVideoCellListner onVideoCellListner;
    private SparseBooleanArray mSparseBoolMultiSelect = new SparseBooleanArray();
    public static boolean action = false;
    private ActionMode mActionMode;
    private Context ctx;

    public VideoListSubAdapater(List<VideoItem> videoItemList, VideoListSubHolder.OnVideoCellListner onVideoCellListner) {
        this.videoItemList = videoItemList;
        this.videoItemFilterList = videoItemList;
        this.onVideoCellListner = onVideoCellListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        videoListItem2Binding = DataBindingUtil.inflate(inflater, R.layout.video_list_item2, parent, false);
        return new VideoListSubHolder(videoListItem2Binding, onVideoCellListner);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoListSubHolder vlsh = (VideoListSubHolder) holder;
        vlsh.setItem(videoItemFilterList.get(position), mSparseBoolMultiSelect.get(position));
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
                    vlsh.ChechChange(false);
                    binding.getRoot().setSelected(false);
                    mSparseBoolMultiSelect.delete(position);
                    if (mSparseBoolMultiSelect.size() == 0) {
                        mActionMode.finish();
                        return true;
                    }
                } else {
                    vlsh.ChechChange(true);
                    binding.getRoot().setSelected(true);
                    mSparseBoolMultiSelect.put(position, true);
                }


                mActionMode.setTitle(String.format("%d selected", mSparseBoolMultiSelect.size()));

                return true;
            }
        });

        binding.getRoot().setOnClickListener(v -> {
            if (!action) {
                ((VideoListSubHolder) holder).getVideoCellListner().onVideoItemClick(videoItemFilterList,position);
            } else {
                if (mSparseBoolMultiSelect.get(position)) {
                    v.setSelected(false);
                    vlsh.ChechChange(false);
                    mSparseBoolMultiSelect.delete(position);
                    if (mSparseBoolMultiSelect.size() == 0) {
                        mActionMode.finish();
                        notifyDataSetChanged();
                        return;
                    }
                } else {
                    v.setSelected(true);
                    vlsh.ChechChange(true);
                    mSparseBoolMultiSelect.put(position, true);
                }

                mActionMode.setTitle(String.format("%d selected", mSparseBoolMultiSelect.size()));
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
        for (int i = 0; i < mSparseBoolMultiSelect.size(); i++) {
            int position = mSparseBoolMultiSelect.keyAt(i);

            File file = new File(videoItemFilterList.get(position).getDATA());
            final String where = MediaStore.MediaColumns.DATA + "=?";
            final String[] selectionArgs = new String[]{
                    file.getAbsolutePath()
            };
            final ContentResolver contentResolver = ctx.getContentResolver();
            final Uri filesUri = MediaStore.Files.getContentUri("external");

            contentResolver.delete(filesUri, where, selectionArgs);
            if (file.exists()) {
                contentResolver.delete(filesUri, where, selectionArgs);
            }
        }

        for (int i = videoItemList.size() - 1; i > -1; i--) {
            if (mSparseBoolMultiSelect.get(i)) {
                videoItemList.remove(i);
            }
        }
        notifyDataSetChanged();
        mActionMode.finish();
    }


    @Override
    public int getItemCount() {
        return videoItemFilterList != null ? videoItemFilterList.size() : 0;
    }

    public void remove(int position) {
        videoItemFilterList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, videoItemFilterList.size());
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
