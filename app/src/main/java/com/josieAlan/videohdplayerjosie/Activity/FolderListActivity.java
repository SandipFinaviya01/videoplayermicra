package com.josieAlan.videohdplayerjosie.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.josieAlan.videohdplayerjosie.PrefData;
import com.josieAlan.videohdplayerjosie.adapter.VideoListAdapter;
import com.josieAlan.videohdplayerjosie.model.Folder;
import com.josieAlan.videohdplayerjosie.R;
import com.josieAlan.videohdplayerjosie.databinding.ActivityVideoListBinding;
import com.josieAlan.videohdplayerjosie.utils.MediaQuery;
import com.josieAlan.videohdplayerjosie.viewholder.VideoListHolder;

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
    private AdView adViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_list);
        prefData = PrefData.getInstance();
        AdSettings.addTestDevice("764f2cd5-1630-4a33-b384-10d94099b870");
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
        adViews = new AdView(this, prefData.referClass.fbBannerKey, AdSize.BANNER_HEIGHT_50);

        binding.bannerContainer.addView(adViews);
        // Request an ad
        adViews.loadAd();
        /*this.nativeBannerAd = new NativeBannerAd(this, prefData.referClass.fbBannerKey);
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
        this.nativeBannerAd.loadAd();*/
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

    @Override
    public void onBackPressed() {
        showConfiramationDialog();
    }

    private void showConfiramationDialog() {
        final AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        dialog.findViewById(R.id.tvYes).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        dialog.findViewById(R.id.tvNo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}
