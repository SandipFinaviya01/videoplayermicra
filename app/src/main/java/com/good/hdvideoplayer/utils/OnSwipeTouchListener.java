package com.good.hdvideoplayer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.ui.PlayerView;

import com.good.hdvideoplayer.R;
//
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.ui.PlayerView;


import java.util.concurrent.TimeUnit;

@UnstableApi public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private final SimpleExoPlayer player;
    private final PlayerView playerView;
    private final AudioManager audioManager;
    private Context context;

    public OnSwipeTouchListener(Context c, SimpleExoPlayer player, PlayerView playerView, AudioManager audioManager) {
        gestureDetector = new GestureDetector(c, new GestureListener());
        this.player = player;
        this.playerView = playerView;
        this.audioManager = audioManager;
        this.context = c;
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            if (!PreferenceUtil.getInstance(context).getLock()) {
                                onSwipeRight(Math.abs(diffX));
                            }
                        } else {
                            if (!PreferenceUtil.getInstance(context).getLock()) {
                                onSwipeLeft(Math.abs(diffX));
                            }
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (e1.getX() > playerView.getRootView().getWidth() / 2) {
                            if (diffY > 0) {
                                if (!PreferenceUtil.getInstance(context).getLock()) {
                                    volumeDown(Math.abs(diffY));
                                }
                            } else {
                                if (!PreferenceUtil.getInstance(context).getLock()) {
                                    volumeUp(Math.abs(diffY));
                                }
                            }
                        } else {
                            if (diffY > 0) {
                                if (!PreferenceUtil.getInstance(context).getLock()) {
                                    brightnessDown(Math.abs(diffY));
                                }
                            } else {
                                if (!PreferenceUtil.getInstance(context).getLock()) {
                                    brightnessUp(Math.abs(diffY));
                                }
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return true;
        }
    }

    private void onSwipeRight(float abs) {
        TextView t1 = ((Activity) this.context).findViewById(R.id.text);
        if (player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.seekTo((long) (player.getCurrentPosition() + Math.abs(abs) * 60));
            player.setPlayWhenReady(true);
            t1.setText(duration(player.getCurrentPosition()));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        } else {
            player.seekTo((long) (player.getCurrentPosition() + Math.abs(abs) * 60));
            t1.setText(duration(player.getCurrentPosition()));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        }

    }

    private void onSwipeLeft(float abs) {
        TextView t1 = ((Activity) this.context).findViewById(R.id.text);
        if (player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.seekTo((long) (player.getCurrentPosition() - Math.abs(abs) * 60));
            player.setPlayWhenReady(true);
            t1.setText(duration(player.getCurrentPosition()));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        } else {
            player.seekTo((long) (player.getCurrentPosition() - Math.abs(abs) * 60));
            t1.setText(duration(player.getCurrentPosition()));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        }
    }

    @SuppressLint("DefaultLocale")
    private void brightnessUp(float abs) {
        WindowManager.LayoutParams attributes = ((Activity) this.context).getWindow().getAttributes();
        float x = PreferenceUtil.getInstance(context).getLastBrightness();
        TextView t1 = ((Activity) this.context).findViewById(R.id.text);
        if (x + (abs / 1000.0f) < 1) {
            attributes.screenBrightness = x + abs / 1000.0f;
            ((Activity) this.context).getWindow().setAttributes(attributes);
            PreferenceUtil.getInstance(context).saveLastBrightness(attributes.screenBrightness);
            t1.setText(String.format("Brightness:%d%%", (int) Math.floor(attributes.screenBrightness * 100)));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        } else {
            attributes.screenBrightness = 1;
            ((Activity) this.context).getWindow().setAttributes(attributes);
            PreferenceUtil.getInstance(context).saveLastBrightness(attributes.screenBrightness);
            t1.setText(String.format("Brightness:%d%%", (int) Math.floor(attributes.screenBrightness * 100)));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        }
    }

    @SuppressLint("DefaultLocale")
    private void brightnessDown(float abs) {
        WindowManager.LayoutParams attributes = ((Activity) this.context).getWindow().getAttributes();
        float x = PreferenceUtil.getInstance(context).getLastBrightness();
        TextView t1 = ((Activity) this.context).findViewById(R.id.text);
        if (x - (abs / 1000.0f) > 0) {
            attributes.screenBrightness = x - (abs / 1000.0f);
            ((Activity) this.context).getWindow().setAttributes(attributes);
            PreferenceUtil.getInstance(context).saveLastBrightness(attributes.screenBrightness);
            t1.setText(String.format("Brightness:%d%%", (int) Math.floor(attributes.screenBrightness * 100)));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        } else {
            attributes.screenBrightness = 0;
            ((Activity) this.context).getWindow().setAttributes(attributes);
            PreferenceUtil.getInstance(context).saveLastBrightness(attributes.screenBrightness);
            t1.setText(String.format("Brightness:%d%%", (int) Math.floor(attributes.screenBrightness * 100)));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        }
    }


    private void setIn(TextView t1) {
        new Handler().postDelayed(() -> t1.setVisibility(View.INVISIBLE), 1500);
    }

    @SuppressLint("DefaultLocale")
    private void volumeUp(float abs) {
       int c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        TextView t1 = ((Activity) this.context).findViewById(R.id.text);
        int x = (int) (abs / 100);
        if (x + c < max) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, x + c, 0);
            t1.setText(String.format("Volume:%d", x + c));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
            t1.setText(String.format("Volume:%d", max));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        }
    }

    @SuppressLint("DefaultLocale")
    private void volumeDown(float abs) {
        int c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        TextView t1 = ((Activity) this.context).findViewById(R.id.text);
        int x = (int) (abs / 100);
        if (c - x > 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, c - x, 0);
            t1.setText(String.format("Volume:%d", c - x));
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            t1.setText("Volume:0");
            t1.setVisibility(View.VISIBLE);
            setIn(t1);
        }

    }


    @SuppressLint("DefaultLocale")
    private String duration(Long x) {

        long ho = TimeUnit.MILLISECONDS.toHours(x);
        long mo = TimeUnit.MILLISECONDS.toMinutes(x) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(x));
        long so = TimeUnit.MILLISECONDS.toSeconds(x) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(x));

        if (ho >= 1) return String.format("%02d:%02d:%02d", ho, mo, so);
        else return String.format("%02d:%02d", mo, so);

    }


}