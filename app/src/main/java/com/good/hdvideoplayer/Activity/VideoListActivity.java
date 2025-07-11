package com.good.hdvideoplayer.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.good.hdvideoplayer.PrefData;
import com.good.hdvideoplayer.R;
import com.good.hdvideoplayer.adapter.VideoListSubAdapater;
import com.good.hdvideoplayer.databinding.ActivityVideoList2Binding;
import com.good.hdvideoplayer.model.VideoItem;
import com.good.hdvideoplayer.utils.MediaQuery;
import com.good.hdvideoplayer.viewholder.VideoListSubHolder;
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
    private LinearLayout linadView;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adChoicesContainer;
//    private AdChoicesView adChoicesView;
    private AdView adViews;
    ProgressDialog progressDialog;
    RelativeLayout adlayout;
    ImageView imggift;
//    private com.google.android.gms.ads.AdView GAdView;
    LinearLayout linBannerAdview;
//    private UnifiedNativeAd GnativeAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_list2);
        setSupportActionBar(binding.toolbarVideoList);
        prefData = PrefData.getInstance();
        imggift = (ImageView)findViewById(R.id.imggift);
        imggift.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink));

        imggift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(VideoListActivity.this,AppAdsActivity.class);
                startActivity(in);
            }
        });

        progressDialog = new ProgressDialog(VideoListActivity.this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("loading Ads...");
        progressDialog.setTitle("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        adlayout = (RelativeLayout)findViewById(R.id.adlayout);
        linBannerAdview = (LinearLayout)findViewById(R.id.adView);

        if (prefData.referClass == null){
            return;
        }

        nativeAdLayout = findViewById(R.id.nativeAdContainer);
//        loadGNative();

        adViews = new AdView(this, prefData.referClass.fbBannerKey2, AdSize.BANNER_HEIGHT_50);
        binding.bannerContainer.addView(adViews);

//        GAdView = new com.google.android.gms.ads.AdView(this);
//        GAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
//        GAdView.setAdUnitId(prefData.referClass.fbBannerKey4);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        GAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//            }
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError adError) {
//                // Code to be executed when an ad request fails.
//                linBannerAdview.setVisibility(View.GONE);
//                adViews.loadAd();
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//            }
//
//            @Override
//            public void onAdClicked() {
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//
//            }
//
//            @Override
//            public void onAdClosed() {
//
//            }
//        });
//        if(GAdView.getAdSize() != null || GAdView.getAdUnitId() != null)
//            GAdView.loadAd(adRequest);
//        linBannerAdview.addView(GAdView);

        if (prefData.isNetwork()){ ;
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

        binding.refreshVideoList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshVideoList.setRefreshing(false);
            }
        });
    }

    private void setRecylerview() {
        videoListSubAdapater = new VideoListSubAdapater(videoItemList, this);
        videoListSubAdapater.setContext(VideoListActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.isAutoMeasureEnabled();
        binding.recycleVideoList.setLayoutManager(layoutManager);
        binding.recycleVideoList.setAdapter(videoListSubAdapater);
    }



    private void loadNativeAds() {
        nativeAd = new NativeAd(this, prefData.referClass.fbNativeKey2);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e("DD:", "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
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

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.nativeAdContainer);

        LayoutInflater inflater = LayoutInflater.from(VideoListActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        linadView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdLayout, false);
        nativeAdLayout.addView(linadView);
        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(VideoListActivity.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = linadView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = linadView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = linadView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = linadView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = linadView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = linadView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = linadView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                linadView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

//    private void loadGNative() {
//        AdLoader.Builder builder = new AdLoader.Builder(this, prefData.referClass.fbNativeKey4);
//
//        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
//            @Override
//            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
//                if (GnativeAd != null) {
//                    GnativeAd.destroy();
//                }
//                GnativeAd = unifiedNativeAd;
//                FrameLayout frameLayout =
//                        findViewById(R.id.fl_adplaceholder);
//                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
//                        .inflate(R.layout.ad_unified, null);
//                populateUnifiedNativeAdView(unifiedNativeAd, adView);
//                frameLayout.removeAllViews();
//                frameLayout.addView(adView);
//            }
//
//        });
//
//        VideoOptions videoOptions = new VideoOptions.Builder().build();
//        com.google.android.gms.ads.nativead.NativeAdOptions adOptions = new NativeAdOptions.Builder()
//                .setVideoOptions(videoOptions)
//                .build();
//
//        builder.withNativeAdOptions(adOptions);
//
//        AdLoader adLoader = builder.withAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                Log.e("DD:","Failed to load native ad: "+errorCode);
//                nativeAdLayout.setVisibility(View.VISIBLE);
//                loadNativeAds();
//            }
//        }).build();
//
//        adLoader.loadAd(new AdRequest.Builder().build());
//
//    }
//
//    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
//        adView.setMediaView((com.google.android.gms.ads.formats.MediaView) adView.findViewById(R.id.ad_media));
//
//        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//        adView.setBodyView(adView.findViewById(R.id.ad_body));
//        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//        adView.setPriceView(adView.findViewById(R.id.ad_price));
//        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
//
//        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
//        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
//
//        if (nativeAd.getBody() == null) {
//            adView.getBodyView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getBodyView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//        }
//
//        if (nativeAd.getCallToAction() == null) {
//            adView.getCallToActionView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getCallToActionView().setVisibility(View.VISIBLE);
//            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//        }
//
//        if (nativeAd.getIcon() == null) {
//            adView.getIconView().setVisibility(View.GONE);
//        } else {
//            ((ImageView) adView.getIconView()).setImageDrawable(
//                    nativeAd.getIcon().getDrawable());
//            adView.getIconView().setVisibility(View.VISIBLE);
//        }
//
//        if (nativeAd.getPrice() == null) {
//            adView.getPriceView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getPriceView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//        }
//
//        if (nativeAd.getStore() == null) {
//            adView.getStoreView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getStoreView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//        }
//
//        if (nativeAd.getStarRating() == null) {
//            adView.getStarRatingView().setVisibility(View.INVISIBLE);
//        } else {
//            ((RatingBar) adView.getStarRatingView())
//                    .setRating(nativeAd.getStarRating().floatValue());
//            adView.getStarRatingView().setVisibility(View.VISIBLE);
//        }
//
//        if (nativeAd.getAdvertiser() == null) {
//            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
//        } else {
//            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//            adView.getAdvertiserView().setVisibility(View.VISIBLE);
//        }
//        adView.setNativeAd(nativeAd);
//
//        VideoController vc = nativeAd.getVideoController();
//        if (vc.hasVideoContent()) {
//            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//                @Override
//                public void onVideoEnd() {
//                    super.onVideoEnd();
//                }
//            });
//        }
//    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onExtraDotClick(VideoItem videoItem, ImageView anchor, int position) {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.detailvideopopup, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, anchor);
        optionsMenu.setForceShowIcon(true);

        // Set Item Click Listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {

            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.share) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("video/*");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoItem.getDATA()));
                    intent.putExtra(Intent.EXTRA_TEXT, videoItem.getDISPLAY_NAME());
                    intent.putExtra(Intent.EXTRA_SUBJECT, videoItem.getDISPLAY_NAME());
                    startActivity(Intent.createChooser(intent, "Share Video"));
                    return true;

                } else if (id == R.id.delete) {
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

                } else if (id == R.id.properties) {
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
                            .setPositiveButton("OK", (dialog, id1) -> {});
                    builder.show();
                    return true;
                }

                return false;
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

    @Override
    public void onDestroy(){
//        if (GnativeAd != null) {
//            GnativeAd.destroy();
//        }
        if (adViews != null) {
            adViews.destroy();
        }
        super.onDestroy();
        //  hideProgressDialog();
    }


}
