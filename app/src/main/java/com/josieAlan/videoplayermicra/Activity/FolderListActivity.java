package com.josieAlan.videoplayermicra.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
import com.josieAlan.videoplayermicra.PrefData;
import com.josieAlan.videoplayermicra.adapter.VideoListAdapter;
import com.josieAlan.videoplayermicra.model.Folder;
import com.josieAlan.videoplayermicra.R;
import com.josieAlan.videoplayermicra.databinding.ActivityVideoListBinding;
import com.josieAlan.videoplayermicra.utils.MediaQuery;
import com.josieAlan.videoplayermicra.viewholder.VideoListHolder;

import java.util.ArrayList;
import java.util.List;

public class FolderListActivity extends AppCompatActivity implements VideoListHolder.OnVideoListListner {
    ActivityVideoListBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    List<Folder> folderList = new ArrayList<>();
    VideoListAdapter adapter;
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
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_list);
        prefData = PrefData.getInstance();
        if (prefData.isNetwork()){
            showBanner();
            loadNativeAds();
            if (prefData.homeAd != null && prefData.homeAd.isAdLoaded()) {
                prefData.homeAd.show();
            }
        }
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
            if (checkPermission(FolderListActivity.this)) {
                updateData();
            } else {
                requestPermission(FolderListActivity.this);
            }
        } else {
            updateData();
        }
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
                                adChoicesView = new AdChoicesView((Context) FolderListActivity.this, (NativeAdBase) nativeAd, true);
                                adChoicesContainer.addView(adChoicesView, 0);
                                binding.nativeAdContainer.setVisibility(View.VISIBLE);
                                prefData.viewNative(nativeAd, adView, FolderListActivity.this);
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

    private void setUpRecyclerView() {
        binding.videoRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VideoListAdapter(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(FolderListActivity.this);
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

    @Override
    public void onVideoListItemClick(Folder folder) {
        Intent intent = new Intent(this, VideoListActivity.class);
        intent.putExtra("id", folder.getBid());
        intent.putExtra("name", folder.getBucket());
        startActivity(intent);
    }
}
