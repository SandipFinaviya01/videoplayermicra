package com.good.hdvideoplayer.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.ads.Ad;
//import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.good.hdvideoplayer.Fragment.MusicFragment;
import com.good.hdvideoplayer.Fragment.PlaylistFragment;
import com.good.hdvideoplayer.PrefData;
import com.good.hdvideoplayer.R;
import com.good.hdvideoplayer.adapter.VideoListAdapter;
import com.good.hdvideoplayer.databinding.ActivityVideoListBinding;
import com.good.hdvideoplayer.model.CommonModel;
import com.good.hdvideoplayer.model.Folder;
import com.good.hdvideoplayer.model.Users;
import com.good.hdvideoplayer.utils.MediaQuery;
import com.good.hdvideoplayer.utils.MyNumFormatter;
import com.good.hdvideoplayer.viewholder.VideoListHolder;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdLoader;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.LoadAdError;
//import com.google.android.gms.ads.VideoController;
//import com.google.android.gms.ads.VideoOptions;
//import com.google.android.gms.ads.formats.UnifiedNativeAd;
//import com.google.android.gms.ads.formats.UnifiedNativeAdView;
//import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import softpro.naseemali.ShapedNavigationView;

import static com.good.hdvideoplayer.adapter.VideoListAdapter.SPAN_COUNT_ONE;
import static com.good.hdvideoplayer.adapter.VideoListAdapter.SPAN_COUNT_THREE;

public class FolderListActivity extends AppCompatActivity implements VideoListHolder.OnVideoListListner {
    ActivityVideoListBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    List<Folder> folderList = new ArrayList<>();
    VideoListAdapter adapter;
    private PrefData prefData;
    private LinearLayout adViewBanner;
    NativeAd nativeAd;
    CardView adView;
    private LinearLayout linadView;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adChoicesContainer;
//    private AdChoicesView adChoicesView;
    private AdView adViews;
  //  private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
     Dialog dialog;
     RelativeLayout adlayout;
    public static int counter=0;
    private InterstitialAd interstitialAd;
//    private com.google.android.gms.ads.InterstitialAd mInterstitialAd;
    private String folderid,foldername;
    AccountHeader headerResult;
    Drawer resultDrawer;
    ImageView imggift;
//    private com.google.android.gms.ads.AdView GAdView;
    LinearLayout linBannerAdview;
//    private UnifiedNativeAd GnativeAd;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_list);
        prefData = PrefData.getInstance();
        //mDatabase = FirebaseDatabase.getInstance().getReference();
        AdSettings.addTestDevice("c51eb358-26dc-4f4c-9c94-2b48d8772d3c");
        imggift = (ImageView)findViewById(R.id.imggift);
        nativeAdLayout = findViewById(R.id.nativeAdContainer);
        linBannerAdview = (LinearLayout)findViewById(R.id.adView);
        TextView tabMusic = findViewById(R.id.tab_music);
        TextView tabPlaylist = findViewById(R.id.tab_playlist);
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
        LinearLayout videoSection = findViewById(R.id.videoLayout);

        TextView tabVideo = findViewById(R.id.Video);
        LinearLayout videoLayout = findViewById(R.id.videoLayout);

        tabVideo.setOnClickListener(v -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            }

            videoLayout.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        });


//        TextView videoLayout; // ya TextView, ImageView etc.
//        videoLayout = findViewById(R.id.Video);
//
//        videoLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FolderListActivity.this, FolderListActivity.class);
//                startActivity(intent);
//            }
//        });


        tabMusic.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new MusicFragment())
                    .commit();
            fragmentContainer.setVisibility(View.VISIBLE);
            videoSection.setVisibility(View.GONE);
        });

        tabPlaylist.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new PlaylistFragment())
                    .commit();
            fragmentContainer.setVisibility(View.VISIBLE);
            videoSection.setVisibility(View.GONE);
        });



        if (prefData.referClass == null){
            return;
        }

        adViews = new com.facebook.ads.AdView(this,prefData.referClass.fbBannerKey, AdSize.BANNER_HEIGHT_50);
        binding.bannerContainer.addView(adViews);


        LoadAdmobFullAd();
        LoadFullAd();

        setSupportActionBar(binding.toolbar2);

        imggift.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink));

        imggift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(FolderListActivity.this,AppAdsActivity.class);
                startActivity(in);
            }
        });

        this.headerResult = new AccountHeaderBuilder().withActivity(this).withHeaderBackground((int) R.drawable.drawerbg).build();
        this.resultDrawer = new DrawerBuilder().withActivity(this).withSelectedItem(-1).withFullscreen(true).withAccountHeader(this.headerResult).withActionBarDrawerToggle(true).withCloseOnClick(true).withMultiSelect(false).withTranslucentStatusBar(true).withToolbar(binding.toolbar2).addDrawerItems((IDrawerItem) ((PrimaryDrawerItem) new PrimaryDrawerItem().withSelectable(false)).withName((int) R.string.app_name), (IDrawerItem) ((PrimaryDrawerItem) ((PrimaryDrawerItem) ((PrimaryDrawerItem) new PrimaryDrawerItem().withSelectable(false)).withName(getString(R.string.shareapp))).withIcon((int) R.drawable.ic_menu_share)).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                int i2 = getApplicationInfo().labelRes;
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", getString(i2));
                String sb = "Install this cool application: " +
                        " " +
                        "https://play.google.com/store/apps/details?id=" + getPackageName();
                intent.putExtra("android.intent.extra.TEXT", sb);
                startActivity(Intent.createChooser(intent, "Share link:"));
                return false;
            }
        }), (IDrawerItem) ( ((PrimaryDrawerItem) new PrimaryDrawerItem().withSelectable(false)).withName(getString(R.string.more)).withIcon((int) R.drawable.ic_baseline_store_24px)).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Shreeji+Developer+Apps&hl=en"));
                    intent.addFlags(268435456);
                    getApplicationContext().startActivity(intent);
                } catch (Exception unused) {
                    Toast.makeText(getApplicationContext(), "please connect internet connection", 0).show();
                }
                return false;
            }
        }), (IDrawerItem) (((PrimaryDrawerItem) new PrimaryDrawerItem().withSelectable(false)).withName(getString(R.string.rate)).withIcon((int) R.drawable.ic_baseline_star_24px)).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                String packageName = getPackageName();
                String str = "https://play.google.com/store/apps/details?id=";
                String str2 = "android.intent.action.VIEW";
                try {
                    String sb = "http://play.google.com/store/apps/details?id=" +
                            packageName;
                    startActivity(new Intent(str2, Uri.parse(sb)));
                } catch (ActivityNotFoundException unused) {
                    String sb2 = str +
                            packageName;
                    startActivity(new Intent(str2, Uri.parse(sb2)));
                }
                return false;
            }
        }),(IDrawerItem) ( ((PrimaryDrawerItem) new PrimaryDrawerItem().withSelectable(false)).withName(getString(R.string.ads)).withIcon((int) R.drawable.ic_ad)).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                Intent in = new Intent(FolderListActivity.this,AppAdsActivity.class);
                startActivity(in);
                return false;
            }
        })).withSavedInstance(savedInstanceState).build();

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

//        binding.walletSection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FolderListActivity.this,WalletActivity.class);
//                startActivity(intent);
//            }
//        });
      /*  if (!prefData.getUserId().equals("")) {
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
        }*/

      /*  mDatabase.child("Common").addValueEventListener(new ValueEventListener() {
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
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Super HD Max player");
                String sAux = "\n\"Indian Android Apps\" \n" +
                        "Indian HD Max player is one of the best app to play all type of video including 4K and other high formate Video from your device, it supports all format of video including MP4, AVI, AAC3. This is the best player you find on the store for enjoying your video, movie, songs, etc\n\n";
                sAux = sAux + "http://play.google.com/store/apps/details?id=" + getPackageName();
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {
                //e.printStackTrace();
            }

        } else if (id == R.id.ic_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));

        } else if (id == R.id.ic_more) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=Shreeji+Developer+Apps")));

        } else if (id == R.id.menu_switch_layout) {
            switchLayout();
            switchIcon(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void switchLayout() {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
            gridLayoutManager.setSpanCount(SPAN_COUNT_THREE);
        } else {
            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);
        }
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
    }

    private void switchIcon(MenuItem item) {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_THREE) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_span_3));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_span_1));
        }
    }


    private void loadNativeAds() {
        nativeAd = new NativeAd(this, prefData.referClass.fbNativeKey);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e("DD:", "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, @NonNull AdError adError) {
                // Native ad failed to load

                Log.e("DD:", "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

                // Native ad is loaded and ready to be displayed
                Log.d("DD:", "Native ad is loaded and ready to be displayed!");
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d("DD:", "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d("DD:", "Native ad impression logged!");
            }
        };

        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());

    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        nativeAdLayout = findViewById(R.id.nativeAdContainer);

        LayoutInflater inflater = LayoutInflater.from(FolderListActivity.this);

        linadView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdLayout, false);
        nativeAdLayout.addView(linadView);

        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(FolderListActivity.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);


        MediaView nativeAdIcon = linadView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = linadView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = linadView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = linadView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = linadView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = linadView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = linadView.findViewById(R.id.native_ad_call_to_action);


        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());


        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);


        nativeAd.registerViewForInteraction(
                linadView, nativeAdMedia, nativeAdIcon, clickableViews);
    }


    private void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.isAutoMeasureEnabled();
        binding.videoRecycler.setLayoutManager(layoutManager);
        gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT_ONE);
        adapter = new VideoListAdapter(this,gridLayoutManager);
        binding.videoRecycler.setAdapter(adapter);
        binding.videoRecycler.setNestedScrollingEnabled(true);
        binding.videoRecycler.setHasFixedSize(false);
        binding.videoRecycler.setLayoutManager(gridLayoutManager);
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            checkSetPer();
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
        folderid = folder.getBid();
        foldername = folder.getBucket();
        if(counter%4==0){
            if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                showProgressDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        interstitialAd.show();
                        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                // Interstitial ad displayed callback
                                Log.e("DD::", "Interstitial ad displayed.");
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                // Interstitial dismissed callback
                                Log.e("DD::", "Interstitial ad dismissed.");
                                LoadFullAd();
                                Intent intent = new Intent(FolderListActivity.this, VideoListActivity.class);
                                intent.putExtra("id", folderid);
                                intent.putExtra("name", foldername);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                // Ad error callback
                                Log.e("DD::", "Interstitial ad failed to load: " + adError.getErrorMessage());
                                Intent intent = new Intent(FolderListActivity.this, VideoListActivity.class);
                                intent.putExtra("id", folderid);
                                intent.putExtra("name", foldername);
                                startActivity(intent);
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Interstitial ad is loaded and ready to be displayed
                                Log.d("DD::", "Interstitial ad is loaded and ready to be displayed!");
                                // Show the ad
                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                // Ad clicked callback
                                Log.d("DD::", "Interstitial ad clicked!");
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                // Ad impression logged callback
                                Log.d("DD:", "Interstitial ad impression logged!");
                            }
                        }; interstitialAd.loadAd(
                                interstitialAd.buildLoadAdConfig()
                                        .withAdListener(interstitialAdListener)
                                        .build());
                    }
                }, 1000);

            }

            else{
                Intent intent = new Intent(FolderListActivity.this, VideoListActivity.class);
                intent.putExtra("id", folderid);
                intent.putExtra("name", foldername);
                startActivity(intent);
            }
        }else {
            Intent intent = new Intent(this, VideoListActivity.class);
            intent.putExtra("id", folderid);
            intent.putExtra("name", foldername);
            startActivity(intent);
        }
        counter++;

    }

    @Override
    public void onBackPressed() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        if (isFinishing()) return; // ✅ Prevent leak if activity closing

        dialog = new AppCompatDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_dialog);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnYes = dialog.findViewById(R.id.btn_yes);
        Button btnNo = dialog.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        if (!isFinishing()) {
            dialog.show(); // ✅ Show only if activity alive
        }
    }


    @Override
    public void onDestroy(){
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        if (adViews != null) {
            adViews.destroy();
        }

        super.onDestroy();
        if (dialog!=null && dialog.isShowing() ){
            dialog.dismiss();
        }
        hideProgressDialog();
    }

    public void LoadFullAd(){
        if (prefData.referClass == null){
            return;
        }
        interstitialAd = new InterstitialAd(FolderListActivity.this, prefData.referClass.fbFullKey);
        interstitialAd.loadAd();
    }

    public void LoadAdmobFullAd(){
        if (prefData.referClass == null){
            return;
        }

    }


    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setMessage("loading Ads..."); // Setting Message
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }


    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
