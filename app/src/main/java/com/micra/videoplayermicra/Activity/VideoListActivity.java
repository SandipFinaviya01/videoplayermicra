package com.micra.videoplayermicra.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.micra.videoplayermicra.adapter.VideoListAdapter;
import com.micra.videoplayermicra.model.Folder;
import com.micra.videoplayermicra.R;
import com.micra.videoplayermicra.databinding.ActivityVideoListBinding;
import com.micra.videoplayermicra.utils.MediaQuery;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {
    ActivityVideoListBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    List<Folder> folderList = new ArrayList<>();
    VideoListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_list);
        setSupportActionBar(binding.toolbar2);
        setUpRecyclerView();
        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
                binding.swipeToRefresh.setRefreshing(false);
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission(VideoListActivity.this)) {
                updateData();
            } else {
                requestPermission(VideoListActivity.this);
            }
        } else {
            updateData();
        }
    }

    private void setUpRecyclerView() {
        binding.videoRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VideoListAdapter();
        binding.videoRecycler.setAdapter(adapter);
    }

    private void updateData(){
        folderList.clear();

        MediaQuery mediaQuery = new MediaQuery(getApplicationContext());
        folderList = mediaQuery.getfolderList();
        adapter.updateEmployeeListItems(folderList);
    }

    private boolean checkPermission(Activity context) {

        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(Activity context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            checkSetPer();
        } else {
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateData();
            } else {
                checkSetPer();
            }
        }
    }

    private void checkSetPer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoListActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}
