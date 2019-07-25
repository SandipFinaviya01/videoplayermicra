package com.josieAlan.videohdplayerjosie.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.josieAlan.videohdplayerjosie.PrefData;
import com.josieAlan.videohdplayerjosie.R;
import com.josieAlan.videohdplayerjosie.adapter.playlist_adapter;
import com.josieAlan.videohdplayerjosie.databinding.ActivityVideoPlayerBinding;
import com.josieAlan.videohdplayerjosie.model.VideoItem;
import com.josieAlan.videohdplayerjosie.services.floating;
import com.josieAlan.videohdplayerjosie.utils.PreferenceUtil;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityVideoPlayerBinding binding;
    private SimpleExoPlayer player;

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    private float brightnessv;
    private float speedv;
    private AudioManager audioManager;

    private int position;
    private int view = 0;
    private int width;
    private long current;
    private int volumes = -1;
    private int mMaxVolume;
    private List<VideoItem> list;

    private PlaybackParameters parameters;
    private MediaSource videoSource;

    private BottomSheetDialog bottomSheetDialog;

    private WindowManager.LayoutParams attributes;
    DisplayMetrics realDisplayMetrics;

    private Boolean lockstatus = false;
    private Boolean repeatstatus = false;


    private SeekBar vseekBar;
    private ImageButton rotate;
    private ImageButton lock;
    private ImageButton unlock;
    private ImageButton crop;
    private ImageButton back;
    private ImageButton share;
    private ImageButton brightness;
    private ImageButton volume;
    private ImageButton repeat;
    ImageButton more;
    private ImageButton popup;
    ImageButton music;
    private ImageButton playlist;
    private TextView title;
    private TextView tvolume;
    private TextView pspeed;
    private TextView dspeed;
    private float brightnesses = -1;
    private PrefData prefData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player);
        prefData = PrefData.getInstance();
        init();

        position = getIntent().getIntExtra("position", 0);
        list = (List<VideoItem>) getIntent().getSerializableExtra("list");
        current = getIntent().getLongExtra("current", 0);
        int ori = getResources().getConfiguration().orientation;
        if (ori == Configuration.ORIENTATION_PORTRAIT) {
//            adView.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        setPlayer();
        findViewId();
        setBottomSheet();
        setListner();
        setdata();

        player.addListener(new Player.EventListener() {
            @Override
            public void onPositionDiscontinuity(int reason) {
                int change = player.getCurrentWindowIndex();
                if (change != position) {
                    position = change;
                    title.setText(list.get(change).DISPLAY_NAME);
                    bottomSheetDialog.dismiss();
                }
            }

        });

//        binding.playerView.setOnTouchListener(new OnSwipeTouchListener(this, player, binding.playerView, audioManager));
        final GestureDetector gestureDetector = new GestureDetector(VideoPlayerActivity.this, new PlayerGestureListener());
        binding.playerView.setClickable(true);
        binding.playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event))
                    return true;


                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }

                return false;
            }
        });
    }

    private void endGesture() {
        volumes = -1;
        brightnesses = -1f;
        binding.appVideoBrightnessBox.setVisibility(View.GONE);
        binding.appVideoVolumeBox.setVisibility(View.GONE);
        binding.appVideoFastForwardBox.setVisibility(View.GONE);
    }

    private void setdata() {
        pspeed.setText(String.format("%sX", speedv));
        int change = getResources().getConfiguration().orientation;
        if (change == Configuration.ORIENTATION_PORTRAIT) {
            width = realDisplayMetrics.widthPixels;
            titlepot();
        } else {
            width = realDisplayMetrics.heightPixels;
            titleland();
        }

        if (list != null) {
            title.setText(list.get(position).DISPLAY_NAME);
        } else title.setText(fileNameFromUri());
    }

    private String fileNameFromUri() {

        String ret = "";
        String scheme = getIntent().getData().getScheme();

        if (scheme.equals("file")) {
            ret = getIntent().getData().getLastPathSegment();
        } else if (scheme.equals("content")) {
            Cursor cursor = getContentResolver().query(getIntent().getData(), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                ret = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return ret;
    }

    private void titlepot() {
        ViewGroup.LayoutParams params = title.getLayoutParams();
        params.width = width / 7;
        title.setLayoutParams(params);
    }

    private void titleland() {
        ViewGroup.LayoutParams params = title.getLayoutParams();
        params.width = width;
        title.setLayoutParams(params);
    }

    private void setListner() {
        back.setOnClickListener(this);
        share.setOnClickListener(this);
        lock.setOnClickListener(this);
        unlock.setOnClickListener(this);
        crop.setOnClickListener(this);
        brightness.setOnClickListener(this);
        volume.setOnClickListener(this);
        pspeed.setOnClickListener(this);
        repeat.setOnClickListener(this);
        rotate.setOnClickListener(this);

        popup.setOnClickListener(this);
        playlist.setOnClickListener(this);

    }

    private void setBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
        View modal = getLayoutInflater().inflate(R.layout.playlist_sheet, null);
        RecyclerView r1 = modal.findViewById(R.id.r1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        r1.setLayoutManager(layoutManager);
        r1.getLayoutManager().scrollToPosition(position);
        playlist_adapter adapter = new playlist_adapter(this, list, player);
        r1.setAdapter(adapter);
        bottomSheetDialog.setContentView(modal);
    }

    private void findViewId() {
        title = findViewById(R.id.title);
        rotate = findViewById(R.id.rotate);
        lock = findViewById(R.id.lock);
        unlock = findViewById(R.id.unlock);
        crop = findViewById(R.id.exo_crop);
        back = findViewById(R.id.back);
        share = findViewById(R.id.share);
        brightness = findViewById(R.id.brightness);
        volume = findViewById(R.id.exo_volume);
        pspeed = findViewById(R.id.pspeed);
        repeat = findViewById(R.id.repeat);
        //more = findViewById(R.id.more);
        popup = findViewById(R.id.popup);
        //music = findViewById(R.id.music);
        playlist = findViewById(R.id.playlist);

    }

    private void setPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "all.format.video.hdplayer.hdvideoplayer.music.free.online.hd"));

        if (list != null) {
            MediaSource[] videoSources = new MediaSource[list.size()];
            for (int i = 0; i < list.size(); i++) {
                videoSources[i] = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(list.get(i).DATA));
            }

            videoSource = videoSources.length == 1 ? videoSources[0] :
                    new ConcatenatingMediaSource(videoSources);
        } else {
            videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(getIntent().getDataString()));
        }

        attributes = getWindow().getAttributes();
        attributes.screenBrightness = brightnessv;
        getWindow().setAttributes(attributes);

        parameters = new PlaybackParameters(speedv);
        player.setPlaybackParameters(parameters);

        binding.playerView.setPlayer(player);
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.seekTo(position, current);
    }

    private void init() {
        brightnessv = PreferenceUtil.getInstance(this).getLastBrightness();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        speedv = PreferenceUtil.getInstance(this).getLastSpeed();
        Display display = getWindowManager().getDefaultDisplay();
        realDisplayMetrics = new DisplayMetrics();
        display.getRealMetrics(realDisplayMetrics);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            onBackPressed();
        } else if (v == share) {
            if (prefData.isNetwork()) {
                if (prefData.homeAd != null && prefData.homeAd.isAdLoaded()) {
                    prefData.homeAd.show();
                }
            }
            player.setPlayWhenReady(false);
            Intent my = new Intent(Intent.ACTION_SEND);
            my.setType("video/*");
            my.putExtra(Intent.EXTRA_STREAM, Uri.parse(list.get(position).getDATA()));
            my.putExtra(Intent.EXTRA_TEXT, list.get(position).getDISPLAY_NAME());
            my.putExtra(Intent.EXTRA_SUBJECT, list.get(position).getDISPLAY_NAME());
            startActivity(Intent.createChooser(my, "Share Video"));
        } else if (v == lock) {
            lock();
        } else if (v == unlock) {
            unlock();
        } else if (v == crop) {
            switch (view) {
                case 0:
                    binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    view = 3;
                    crop.setImageResource(R.drawable.fit);
                    break;
                case 3:
                    binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    crop.setImageResource(R.drawable.zoom);
                    view = 4;
                    break;
                case 4:
                    binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    crop.setImageResource(R.drawable.full);
                    view = 0;
                    break;
            }
        } else if (v == brightness) {
            handleBrightness();
        } else if (v == volume) {
            handleVolume();
        } else if (v == pspeed) {
            handlePsSpeed();
        } else if (v == repeat) {
            handleRepeate();
        } else if (v == rotate) {
            handleRotate();
        } else if (v == popup) {
            handlePopUp();
        } else if (v == playlist) {
            bottomSheetDialog.show();
        }
    }

    private void handlePopUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            popup();
        }
    }

    private void popup() {
        Intent x = new Intent(VideoPlayerActivity.this, floating.class);
        x.putExtra("position", position);
        x.putExtra("list", (Serializable) list);
        x.putExtra("current", player.getCurrentPosition());
        startService(x);
        onBackPressed();
    }

    private void handleRotate() {
        int ori = getResources().getConfiguration().orientation;
        if (ori == Configuration.ORIENTATION_PORTRAIT) {
//            adView.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
//            adView.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void handleRepeate() {
        if (!repeatstatus) {
            repeat.setImageResource(R.drawable.repeatone);
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            repeatstatus = !repeatstatus;
        } else {
            repeat.setImageResource(R.drawable.repeat);
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            repeatstatus = !repeatstatus;
        }
    }

    private void handlePsSpeed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.control);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v1 = inflater.inflate(R.layout.playback_dialog, null);
        ImageButton sdown, sup;
        sdown = v1.findViewById(R.id.sdown);
        sup = v1.findViewById(R.id.sup);
        dspeed = v1.findViewById(R.id.dspeed);
        AtomicInteger d = new AtomicInteger((int) (PreferenceUtil.getInstance(this).getLastSpeed() * 100));
        dspeed.setText(Integer.toString(d.get()));
        sdown.setOnClickListener(v2 -> {
            if (d.get() <= 100) {
                d.set(d.get() - 5);
                if (d.get() < 24) {
                    d.set(d.get() + 5);
                }
            } else {
                d.set(d.get() - 10);
            }
            setSpeed(d.get());
        });
        sup.setOnClickListener(v22 -> {
            if (d.get() < 100) {
                d.set(d.get() + 5);
            } else {
                d.set(d.get() + 10);
                if (d.get() > 401) {
                    d.set(d.get() - 10);
                }
            }
            setSpeed(d.get());
        });

        builder.setView(v1);
        builder.show();
    }

    private void setSpeed(int d) {
        dspeed.setText(Integer.toString(d));
        float x = d / 100.0f;
        pspeed.setText(String.format("%sX", x));
        parameters = new PlaybackParameters(x);
        player.setPlaybackParameters(parameters);
        PreferenceUtil.getInstance(getApplicationContext()).saveLastSpeed(x);
    }


    private void handleVolume() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.control);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v1 = inflater.inflate(R.layout.volume_dialog, null);
        tvolume = v1.findViewById(R.id.progress);
        vseekBar = v1.findViewById(R.id.seekBar);
        int x = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        vseekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        vseekBar.setProgress(x);
        tvolume.setText(Integer.toString(x));
        vseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                tvolume.setText(Integer.toString(progress));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setView(v1);
        builder.show();
    }

    private void handleBrightness() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.control);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v1 = inflater.inflate(R.layout.brightness_dialog, null);
        TextView t1 = v1.findViewById(R.id.progress);
        SeekBar seekBar = v1.findViewById(R.id.seekBar);
        int x = (int) (PreferenceUtil.getInstance(this).getLastBrightness() * 100);
        seekBar.setProgress(x);
        t1.setText(Integer.toString(x));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                attributes.screenBrightness = (progress / 100.0f);
                getWindow().setAttributes(attributes);
                seekBar.setProgress(progress);
                t1.setText(Integer.toString(progress));
                PreferenceUtil.getInstance(getApplicationContext()).saveLastBrightness(attributes.screenBrightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setView(v1);
        builder.show();
    }

    private void unlock() {
        lockstatus = !lockstatus;
        LinearLayout l1, l2, l3, l4;
        l1 = findViewById(R.id.bottom_control);
        l1.setVisibility(View.VISIBLE);
        l2 = findViewById(R.id.center_left_control);
        l2.setVisibility(View.VISIBLE);
        l3 = findViewById(R.id.top_control);
        l3.setVisibility(View.VISIBLE);
        l4 = findViewById(R.id.center_right_control);
        l4.setVisibility(View.VISIBLE);
        unlock.setVisibility(View.INVISIBLE);
        PreferenceUtil.getInstance(getApplicationContext()).setLock(false);
    }

    private void lock() {
        lockstatus = !lockstatus;
        LinearLayout l1, l2, l3, l4;
        l1 = findViewById(R.id.bottom_control);
        l1.setVisibility(View.INVISIBLE);
        l2 = findViewById(R.id.center_left_control);
        l2.setVisibility(View.INVISIBLE);
        l3 = findViewById(R.id.top_control);
        l3.setVisibility(View.INVISIBLE);
        l4 = findViewById(R.id.center_right_control);
        l4.setVisibility(View.INVISIBLE);
        unlock.setVisibility(View.VISIBLE);
        PreferenceUtil.getInstance(getApplicationContext()).setLock(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int change = getResources().getConfiguration().orientation;
        if (change == Configuration.ORIENTATION_LANDSCAPE) titleland();
        else if (change == Configuration.ORIENTATION_PORTRAIT) titlepot();
    }

    @Override
    protected void onPause() {
        if (lockstatus) {
            unlock();
        }
        player.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (lockstatus) {
            return;
        }
        player.release();

        super.onBackPressed();
    }


    private void checkSetPer() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VideoPlayerActivity.this);
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
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
        }
        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                popup();
            } else { //Permission is not available
                checkSetPer();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private class PlayerGestureListener implements GestureDetector.OnGestureListener {

        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;


        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                if (binding.playerView.isControllerVisible()) {
                    binding.playerView.hideController();
                } else binding.playerView.showController();
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > width * 0.5f;
                firstTouch = false;
            }

            if (toSeek) {
               /* if (!isLive) {
                    onProgressSlide(-deltaX / videoView.getWidth());
                }*/
                if (!lockstatus) {
                    onProgressSlide(-deltaX / binding.playerView.getWidth());
                }
            } else {
                float percent = deltaY / binding.playerView.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    private void onProgressSlide(float percent) {
        long position = player.getCurrentPosition();
        long duration = player.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);


        long newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            binding.appVideoFastForwardBox.setVisibility(View.VISIBLE);
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
//            $.id(R.id.app_video_fastForward).text(text + "s");
            binding.appVideoFastForward.setText(text + "s");
//            $.id(R.id.app_video_fastForward_target).text(generateTime(newPosition)+"/");
            binding.appVideoFastForwardTarget.setText(generateTime(newPosition) + "/");
//            $.id(R.id.app_video_fastForward_all).text(generateTime(duration));
            binding.appVideoFastForwardAll.setText(generateTime(duration));
            player.seekTo(newPosition);

        }
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    private void onBrightnessSlide(float percent) {
        if (brightnesses < 0) {
            brightnesses = getWindow().getAttributes().screenBrightness;
            if (brightnesses <= 0.00f) {
                brightnesses = 0.50f;
            } else if (brightnesses < 0.01f) {
                brightnesses = 0.01f;
            }
        }
        binding.appVideoBrightnessBox.setVisibility(View.VISIBLE);
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = brightnesses + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        binding.appVideoBrightness.setText(((int) (lpa.screenBrightness * 100)) + "%");
        getWindow().setAttributes(lpa);
        PreferenceUtil.getInstance(getApplicationContext()).saveLastBrightness(lpa.screenBrightness);
    }

    private void onVolumeSlide(float percent) {
        if (volumes == -1) {
            volumes = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volumes < 0)
                volumes = 0;
        }

        int index = (int) (percent * mMaxVolume) + volumes;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }

        binding.appVideoVolumeIcon.setImageResource(i == 0 ? R.drawable.ic_volume_off_white_36dp : R.drawable.ic_volume_up_white_36dp);
        binding.appVideoBrightnessBox.setVisibility(View.GONE);
        binding.appVideoVolumeBox.setVisibility(View.VISIBLE);
        binding.appVideoVolume.setVisibility(View.VISIBLE);
        binding.appVideoVolume.setText(s);
    }
}
