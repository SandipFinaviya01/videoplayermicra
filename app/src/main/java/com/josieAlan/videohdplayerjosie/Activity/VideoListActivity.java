package com.josieAlan.videohdplayerjosie.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.josieAlan.videohdplayerjosie.PrefData;
import com.josieAlan.videohdplayerjosie.R;
import com.josieAlan.videohdplayerjosie.adapter.VideoListSubAdapater;
import com.josieAlan.videohdplayerjosie.databinding.ActivityVideoList2Binding;
import com.josieAlan.videohdplayerjosie.model.VideoItem;
import com.josieAlan.videohdplayerjosie.utils.MediaQuery;
import com.josieAlan.videohdplayerjosie.viewholder.VideoListSubHolder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity implements VideoListSubHolder.OnVideoCellListner {

    private ActivityVideoList2Binding binding;
    private String folderId;
    private MediaQuery mediaQuery;
    private List<VideoItem> videoItemList;
    private VideoListSubAdapater videoListSubAdapater;
    private ActionMode mActionMode;
    private PrefData prefData;
    public NativeBannerAd nativeBannerAd;
    private LinearLayout adViewBanner;

    NativeAd nativeAd;
    CardView adView;

    private LinearLayout adChoicesContainer;
    private AdChoicesView adChoicesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_list2);
        setSupportActionBar(binding.toolbarVideoList);
        prefData = PrefData.getInstance();
        if (prefData.isNetwork()){
            loadNativeAds();
            showBanner();
            if (prefData.homeAd != null && prefData.homeAd.isAdLoaded()) {
                prefData.homeAd.show();
            }
        }
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
        videoListSubAdapater.setContext(VideoListActivity.this);
        binding.recycleVideoList.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleVideoList.setAdapter(videoListSubAdapater);
    }


    private void showBanner() {
        this.nativeBannerAd = new NativeBannerAd(this, prefData.referClass.fbBannerKey);
        this.nativeBannerAd.setAdListener(new NativeAdListener() {
            public void onAdClicked(Ad ad) {
            }

            public void onLoggingImpression(Ad ad) {
            }

            public void onMediaDownloaded(Ad ad) {
            }

            public void onError(Ad ad, AdError adError) {
                Log.e("errorrshowbanner", adError.getErrorMessage());
            }

            public void onAdLoaded(Ad ad) {
                if (nativeBannerAd != null && nativeBannerAd == ad) {
                    inflateAd(nativeBannerAd);
                }
            }
        });
        this.nativeBannerAd.loadAd();
    }

    private void loadNativeAds() {
        this.adView = (CardView) getLayoutInflater().inflate(R.layout.native_ad_unit, binding.nativeAdContainer, false);
        binding.nativeAdContainer.addView(adView);
        binding.nativeAdContainer.setVisibility(View.GONE);
        this.nativeAd = new NativeAd((Context) this, prefData.referClass.fbNativeKey);
        this.nativeAd.setAdListener(new NativeAdListener() {
            public void onAdClicked(Ad ad) {
            }

            public void onLoggingImpression(Ad ad) {
            }

            public void onMediaDownloaded(Ad ad) {
            }

            public void onError(Ad ad, AdError adError) {
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(adError.getErrorMessage());
                Log.e("error", sb.toString());
            }

            public void onAdLoaded(Ad ad) {
                try {
                    if (nativeAd != null) {
                        if (nativeAd == ad) {
                            if (adView != null) {
                                nativeAd.unregisterView();
                                adChoicesContainer = (LinearLayout) adView.findViewById(R.id.adChoicesContainer);
                                adChoicesView = new AdChoicesView((Context) VideoListActivity.this, (NativeAdBase) nativeAd, true);
                                adChoicesContainer.addView(adChoicesView, 0);
                                binding.nativeAdContainer.setVisibility(View.VISIBLE);
                                prefData.viewNative(nativeAd, adView, VideoListActivity.this);
                                nativeAd.setOnTouchListener(new View.OnTouchListener() {
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        if (motionEvent.getAction() == 0) {
                                            int id = view.getId();
                                        }
                                        return false;
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.nativeAd.loadAd();

    }

    private void inflateAd(NativeBannerAd nativeBannerAd) {
        nativeBannerAd.unregisterView();
        int i = 0;
        this.adViewBanner = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.native_banner_ad_layout, binding.nativeBannerAdContainer, false);
        binding.nativeBannerAdContainer.addView(adViewBanner);
        RelativeLayout relativeLayout = (RelativeLayout) this.adViewBanner.findViewById(R.id.adChoicesContainer);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeBannerAd, this.binding.nativeBannerAdContainer);
        relativeLayout.removeAllViews();
        relativeLayout.addView(adOptionsView, 0);
        TextView textView = (TextView) this.adViewBanner.findViewById(R.id.nativeAdTitle);
        TextView textView2 = (TextView) this.adViewBanner.findViewById(R.id.nativeAdSocialContext);
        TextView textView3 = (TextView) this.adViewBanner.findViewById(R.id.nativeAdSponsoredLabel);
        AdIconView adIconView = (AdIconView) this.adViewBanner.findViewById(R.id.nativeIconView);
        Button button = (Button) this.adViewBanner.findViewById(R.id.nativeAdCallToAction);
        button.setText(nativeBannerAd.getAdCallToAction());
        if (!nativeBannerAd.hasCallToAction()) {
            i = 4;
        }
        button.setVisibility(i);
        textView.setText(nativeBannerAd.getAdvertiserName());
        textView2.setText(nativeBannerAd.getAdSocialContext());
        textView3.setText(nativeBannerAd.getSponsoredTranslation());
        ArrayList arrayList = new ArrayList();
        arrayList.add(textView);
        arrayList.add(button);
        nativeBannerAd.registerViewForInteraction(this.adViewBanner, adIconView, arrayList);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(VideoListActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(VideoListActivity.this);
                        builder.setTitle("Properties");
                        View v1 = inflater.inflate(R.layout.properties_dialog, null);
                        TextView t1 = v1.findViewById(R.id.filename);
                        t1.setText(videoItem.getDISPLAY_NAME());
                        TextView t2 = v1.findViewById(R.id.videoDuration);
                        t2.setText(videoItem.getDURATION());
                        TextView t3 = v1.findViewById(R.id.fileSize);
                        t3.setText(videoItem.getSIZE());
                        TextView t4 = v1.findViewById(R.id.fileLocation);
                        t4.setText(videoItem.getDATA());
                        TextView t5 = v1.findViewById(R.id.createdDate);
                        t5.setText(videoItem.getDATE());
                        builder.setView(v1)
                                .setPositiveButton("OK", (dialog, id) -> {
                                });
                        builder.show();
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

    @Override
    public void onVideoItemClick(List<VideoItem> videoItemFilterList, int position) {
        Intent my = new Intent(VideoListActivity.this, VideoPlayerActivity.class);
        my.putExtra("list", (Serializable) videoItemFilterList);
        my.putExtra("position", position);
        startActivity(my);

    }


}
