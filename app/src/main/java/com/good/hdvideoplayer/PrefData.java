package com.good.hdvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
//import com.facebook.ads.AdIconView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.good.hdvideoplayer.model.ImageUploadInfo;


import java.util.ArrayList;
import java.util.List;

public class PrefData extends MultiDexApplication {
    private static final String TAG = "PrefData";
    public static String prefName = "mxhd_player";
    private static Context context;
    private static PrefData mInstance;

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public ImageUploadInfo referClass;

    public InterstitialAd homeAd;

    public static synchronized PrefData getInstance(){
        if (mInstance == null){
            mInstance = new PrefData();
            return mInstance;
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        context = getApplicationContext();
        mInstance = this;
        mSharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("c51eb358-26dc-4f4c-9c94-2b48d8772d3c");
    }

    public void setUserId(String id) {
        mEditor.putString("_id", id).commit();
    }

    public String getUserId() {
        return mSharedPreferences.getString("_id", "");
    }
    public void setReqUserId(String id) {
        mEditor.putString("req_id", id).commit();
    }

    public String getReqUserId() {
        return mSharedPreferences.getString("req_id", "");
    }


    public boolean isNetwork() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showHomeAd(){
        if (homeAd != null){
            homeAd.destroy();
            homeAd = null;
        }
        if (referClass == null){
            return;
        }
       // homeAd = new InterstitialAd(context,referClass.getFbFullKey());
        homeAd = new InterstitialAd(context,referClass.fbFullKey);
        homeAd.loadAd();
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

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e("DD::", "Interstitial ad failed to load: " + adError.getErrorMessage());

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
        }; homeAd.loadAd(
                homeAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }
    public void showDetailAd(){

    }
    public void showMoreAd(){

    }
    public void showSubAd(){

    }
//    public  void viewNative(NativeAd nativeAd2, View view, Context context) {
//        try {
//            AdIconView adIconView = view.findViewById(R.id.nativeAdIcon);
//            TextView textView = view.findViewById(R.id.nativeAdTitle);
//            TextView textView2 = view.findViewById(R.id.nativeAdBody);
//            MediaView mediaView = view.findViewById(R.id.nativeAdMedia);
//            TextView textView3 = view.findViewById(R.id.nativeAdSponsoredLabel);
//            mediaView.setListener(new MediaViewListener() {
//                public void onComplete(MediaView mediaView) {
//                }
//
//                public void onEnterFullscreen(MediaView mediaView) {
//                }
//
//                public void onExitFullscreen(MediaView mediaView) {
//                }
//
//                public void onFullscreenBackground(MediaView mediaView) {
//                }
//
//                public void onFullscreenForeground(MediaView mediaView) {
//                }
//
//                public void onPause(MediaView mediaView) {
//                }
//
//                public void onPlay(MediaView mediaView) {
//                }
//
//                public void onVolumeChange(MediaView mediaView, float f) {
//                }
//            });
//            TextView textView4 = view.findViewById(R.id.nativeAdSocialContext);
//            Button button = view.findViewById(R.id.nativeAdCallToAction);
//            textView4.setText(nativeAd2.getAdSocialContext());
//            button.setText(nativeAd2.getAdCallToAction());
//            button.setVisibility(nativeAd2.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
//            textView.setText(nativeAd2.getAdvertiserName());
//            textView2.setText(nativeAd2.getAdBodyText());
//            textView3.setText(nativeAd2.getSponsoredTranslation());
//            ArrayList arrayList = new ArrayList();
//            arrayList.add(adIconView);
//            arrayList.add(mediaView);
//            arrayList.add(button);
//            nativeAd2.registerViewForInteraction(view, mediaView, adIconView, (List<View>) arrayList);
//            NativeAdBase.NativeComponentTag.tagView(adIconView, NativeAdBase.NativeComponentTag.AD_ICON);
//            NativeAdBase.NativeComponentTag.tagView(textView, NativeAdBase.NativeComponentTag.AD_TITLE);
//            NativeAdBase.NativeComponentTag.tagView(textView2, NativeAdBase.NativeComponentTag.AD_BODY);
//            NativeAdBase.NativeComponentTag.tagView(textView4, NativeAdBase.NativeComponentTag.AD_SOCIAL_CONTEXT);
//            NativeAdBase.NativeComponentTag.tagView(button, NativeAdBase.NativeComponentTag.AD_CALL_TO_ACTION);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



}
