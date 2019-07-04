package com.micra.videoplayermicra.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.micra.videoplayermicra.R;
import com.micra.videoplayermicra.adapter.VideoListSubAdapater;
import com.micra.videoplayermicra.databinding.ActivityVideoList2Binding;
import com.micra.videoplayermicra.model.VideoItem;
import com.micra.videoplayermicra.utils.BindUtils;
import com.micra.videoplayermicra.utils.MediaQuery;
import com.micra.videoplayermicra.viewholder.VideoListSubHolder;

import java.io.File;
import java.util.List;

public class VideoListActivity extends AppCompatActivity implements VideoListSubHolder.OnVideoCellListner {

    private ActivityVideoList2Binding binding;
    private String folderId;
    private MediaQuery mediaQuery;
    private List<VideoItem> videoItemList;
    private VideoListSubAdapater videoListSubAdapater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_list2);
        setSupportActionBar(binding.toolbarVideoList);
        String name = getIntent().getStringExtra("name");
        binding.toolbarVideoList.setTitle(name);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        folderId = getIntent().getStringExtra("id");
        mediaQuery = new MediaQuery(getApplicationContext());
        videoItemList = mediaQuery.getAllVideo(folderId, 0);
        setRecylerview();
    }

    private void setRecylerview() {
        videoListSubAdapater = new VideoListSubAdapater(videoItemList, this);
        binding.recycleVideoList.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleVideoList.setAdapter(videoListSubAdapater);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onExtraDotClick(VideoItem videoItem, ImageView anchor,int position) {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.detailvideopopup, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, anchor);
        optionsMenu.setForceShowIcon(true);

        // Set Item Click Listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("video/*");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoItem.getDATA()));
                        intent.putExtra(Intent.EXTRA_TEXT, videoItem.getDISPLAY_NAME());
                        intent.putExtra(Intent.EXTRA_SUBJECT, videoItem.getDISPLAY_NAME());
                        startActivity(Intent.createChooser(intent, "Share Video"));
                        return true;
                    case R.id.delete:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoListActivity.this);
                        alertDialog.setTitle("Delete video from device?");
                        alertDialog.setMessage("Video will be deleted permanently from device.");
                        alertDialog.setPositiveButton("DELETE", (dialog, which) -> {
                            File file = new File(videoItem.getDATA());
                            final String where = MediaStore.MediaColumns.DATA + "=?";
                            final String[] selectionArgs = new String[]{
                                    file.getAbsolutePath()
                            };
                            final ContentResolver contentResolver = VideoListActivity.this.getContentResolver();
                            final Uri filesUri = MediaStore.Files.getContentUri("external");

                            contentResolver.delete(filesUri, where, selectionArgs);
                            if (file.exists()) {
                                contentResolver.delete(filesUri, where, selectionArgs);
                            }
                            videoListSubAdapater.remove(position);

                        });
                        alertDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
                        alertDialog.show();
                        return true;
                    case R.id.properties:
                        return true;
                    default:
                        return false;
                }

            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {
            }
        });


        // Display the menu
        optionsMenu.show();
    }
}
