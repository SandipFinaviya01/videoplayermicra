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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.josieAlan.videohdplayerjosie.PrefData;
import com.josieAlan.videohdplayerjosie.adapter.VideoListAdapter;
import com.josieAlan.videohdplayerjosie.model.CommonModel;
import com.josieAlan.videohdplayerjosie.model.Folder;
import com.josieAlan.videohdplayerjosie.R;
import com.josieAlan.videohdplayerjosie.databinding.ActivityVideoListBinding;
import com.josieAlan.videohdplayerjosie.model.Users;
import com.josieAlan.videohdplayerjosie.utils.MediaQuery;
import com.josieAlan.videohdplayerjosie.utils.MyNumFormatter;
import com.josieAlan.videohdplayerjosie.viewholder.VideoListHolder;

import java.util.ArrayList;
import java.util.List;

public class FolderListActivity extends AppCompatActivity implements VideoListHolder.OnVideoListListner {
    ActivityVideoListBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    List<Folder> folderList = new ArrayList<>();
    VideoListAdapter adapter;
    private PrefData prefData;
    private LinearLayout adViewBanner;

    NativeAd nativeAd;
    CardView adView;

    private LinearLayout adChoicesContainer;
    private AdChoicesView adChoicesView;
    private AdView adViews;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_list);
        prefData = PrefData.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        AdSettings.addTestDevice("49b79231-5d29-4ad9-ba42-dbc4e058e2b2");
        progressDialog = new ProgressDialog(FolderListActivity.this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Wait while loading Ads..."); // Setting Message
        progressDialog.setTitle("Loading"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
        if (prefData.isNetwork()){
            if (prefData.referClass != null) {
                showBanner();
                loadNativeAds();
            }
            if (prefData.homeAd != null && prefData.homeAd.isAdLoaded()) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        prefData.homeAd.show();

                    }
                }, 1000);
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

        binding.walletSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FolderListActivity.this,WalletActivity.class);
                startActivity(intent);
            }
        });
        if (!prefData.getUserId().equals("")) {
            DatabaseReference userRef = mDatabase.child("users").child(prefData.getUserId());
            ValueEventListener eventListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Users users = dataSnapshot.getValue(Users.class);
                        binding.tvAmount.setText(MyNumFormatter.getFormatted(users.coins));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            userRef.addValueEventListener(eventListner);
        }

        mDatabase.child("Common").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CommonModel commonModel = dataSnapshot.getValue(CommonModel.class);
                if (commonModel == null){
                    return;
                }
                if (commonModel.showwallet == 1){
                    binding.walletSection.setVisibility(View.VISIBLE);
                }else {
                    binding.walletSection.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.ic_share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Super HD Max player");
                    String sAux = "\nSuper HD Max player is one of the best app to play all type of video including 4K and other high formate Video from your device, it supports all format of video including MP4, AVI, AAC3. This is the best player you find on the store for enjoying your video, movie, songs, etc\n\n";
                    sAux = sAux + "http://play.google.com/store/apps/details?id=" + getPackageName();
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            case R.id.ic_rate:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                break;
            case R.id.ic_more:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/developer?id=josie+Alan+Apps")));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadNativeAds() {
        this.adView = (CardView) getLayoutInflater().inflate(R.layout.native_ad_unit, binding.nativeAdContainer, false);
        binding.nativeAdContainer.addView(adView);
        binding.nativeAdContainer.setVisibility(View.GONE);
        if (prefData.referClass == null){
            return;
        }
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
        if (prefData.referClass != null) {
            adViews = new AdView(this, prefData.referClass.fbBannerKey, AdSize.BANNER_HEIGHT_50);

            binding.bannerContainer.addView(adViews);
            // Request an ad
            adViews.loadAd();
        }

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
