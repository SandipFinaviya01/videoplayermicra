package com.good.hdvideoplayer.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.source.ConcatenatingMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

import com.good.hdvideoplayer.Activity.VideoPlayerActivity;
import com.good.hdvideoplayer.R;
import com.good.hdvideoplayer.model.VideoItem;
//import com.google.android.exoplayer2.ExoPlayerFactory;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
//import com.google.android.exoplayer2.source.ExtractorMediaSource;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
//import com.google.android.exoplayer2.ui.PlayerView;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
//import com.google.android.exoplayer2.util.Util;


import java.io.Serializable;
import java.util.List;

public class floating extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private int LAYOUT_FLAG;
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private int position;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dothis(intent);
        return super.onStartCommand(intent, flags, startId);
    }


    @UnstableApi @SuppressLint("InflateParams")
    private void dothis(Intent intent) {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.popup_window, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        mFloatingView.findViewById(R.id.player_view).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Optional: Toggle visibility using performClick (or remove this block if not needed)
                        playerView.performClick(); // This triggers the default behavior of toggling controls

                        // Remember the initial position
                        initialX = params.x;
                        initialY = params.y;

                        // Get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Calculate the X and Y coordinates of the view
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        // Update the layout with new X & Y coordinates
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }

        });

        position = intent.getIntExtra("position", 0);
        List<VideoItem> list = (List<VideoItem>) intent.getSerializableExtra("list");
        long current = intent.getLongExtra("current", 0);
        playerView = mFloatingView.findViewById(R.id.player_view);
        player = new SimpleExoPlayer.Builder(this).build();

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                new DefaultHttpDataSource.Factory()
        );

        MediaSource[] videoSources = new MediaSource[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Uri uri = Uri.parse(list.get(i).DATA);
            videoSources[i] = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri));
        }

        MediaSource videoSource = videoSources.length == 1
                ? videoSources[0]
                : new ConcatenatingMediaSource(videoSources);

        player.setMediaSource(videoSource);
        player.prepare();
        player.setPlayWhenReady(true);
        player.seekTo(position, current);

        playerView.setPlayer(player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);



        ImageButton close = mFloatingView.findViewById(R.id.close);
        close.setOnClickListener(v -> stopSelf());

        ImageButton full = mFloatingView.findViewById(R.id.full);
        full.setOnClickListener(v -> {
            Intent x = new Intent(getApplicationContext(), VideoPlayerActivity.class);
            x.putExtra("position", player.getCurrentWindowIndex());
            x.putExtra("list", (Serializable) list);
            x.putExtra("current", player.getCurrentPosition());
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(x);
            stopSelf();
        });


    }
}
