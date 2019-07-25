package com.josieAlan.videohdplayerjosie.services;

import android.annotation.SuppressLint;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.josieAlan.videohdplayerjosie.Activity.VideoPlayerActivity;
import com.josieAlan.videohdplayerjosie.R;
import com.josieAlan.videohdplayerjosie.model.VideoItem;

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


    @SuppressLint("InflateParams")
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

                        if (playerView.isControllerVisible()) {
                            playerView.hideController();
                        } else playerView.showController();
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
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
        player = ExoPlayerFactory.newSimpleInstance(this);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "all.format.video.player.videoplayer.music.free.online.hd"));

        MediaSource[] videoSources = new MediaSource[list.size()];
        for (int i = 0; i < list.size(); i++) {
            videoSources[i] = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(list.get(i).DATA));
        }

        MediaSource videoSource = videoSources.length == 1 ? videoSources[0] :
                new ConcatenatingMediaSource(videoSources);

        playerView.setPlayer(player);
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.seekTo(position, current);
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
