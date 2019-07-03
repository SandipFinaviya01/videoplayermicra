package com.micra.videoplayermicra.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.micra.videoplayermicra.Model.Folder;
import com.micra.videoplayermicra.R;
import com.micra.videoplayermicra.databinding.ActivityVideoListBinding;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {
    ActivityVideoListBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    List<Folder> folderList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_list);
        setSupportActionBar(binding.toolbar2);
    }
}
