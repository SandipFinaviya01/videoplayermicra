package com.micra.videoplayermicra.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.micra.videoplayermicra.R;
import com.micra.videoplayermicra.adapter.VideoListSubAdapater;
import com.micra.videoplayermicra.databinding.ActivityVideoList2Binding;
import com.micra.videoplayermicra.model.VideoItem;
import com.micra.videoplayermicra.utils.BindUtils;
import com.micra.videoplayermicra.utils.MediaQuery;

import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    private ActivityVideoList2Binding binding;
    private String folderId;
    private MediaQuery mediaQuery;
    private List<VideoItem> videoItemList;
    private VideoListSubAdapater videoListSubAdapater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_list2);
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
        videoListSubAdapater = new VideoListSubAdapater(videoItemList);
        binding.recycleVideoList.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleVideoList.setAdapter(videoListSubAdapater);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
