package com.josieAlan.videohdplayerjosie;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.josieAlan.videohdplayerjosie.model.ImageUploadInfo;

import java.util.ArrayList;
import java.util.List;

public class PrefData extends Application {
    private static final String TAG = "PrefData";
    public static String AppName = "mxhd_player";
    private static Context context;
    private static PrefData mInstance;

    public ImageUploadInfo referClass;

    public InterstitialAd homeAd;
    public InterstitialAd detailAd;
    public InterstitialAd moreAd;
    public InterstitialAd subAd;

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
        context = getApplicationContext();
        mInstance = this;
        AudienceNetworkAds.initialize(this);
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

        homeAd = new InterstitialAd(context,referClass.getFbFullKey());
        homeAd.loadAd();
        homeAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                homeAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
    }
    public void showDetailAd(){
        if (detailAd != null){
            detailAd.destroy();
            detailAd = null;
        }
        detailAd = new InterstitialAd(context,referClass.getFbFullKey2());
        detailAd.loadAd();
        detailAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                detailAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
    }
    public void showMoreAd(){
        if (moreAd != null){
            moreAd.destroy();
            moreAd = null;
        }
        moreAd = new InterstitialAd(context,referClass.getFbFullKey3());
        moreAd.loadAd();
        moreAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                moreAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
    }
    public void showSubAd(){
        if (subAd != null){
            subAd.destroy();
            subAd = null;
        }
        subAd = new InterstitialAd(context,referClass.getFbFullKey4());
        subAd.loadAd();
        subAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                subAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
    }
    public  void viewNative(NativeAd nativeAd2, View view, Context context) {
        try {
            AdIconView adIconView = (AdIconView) view.findViewById(R.id.nativeAdIcon);
            TextView textView = (TextView) view.findViewById(R.id.nativeAdTitle);
            TextView textView2 = (TextView) view.findViewById(R.id.nativeAdBody);
            MediaView mediaView = (MediaView) view.findViewById(R.id.nativeAdMedia);
            TextView textView3 = (TextView) view.findViewById(R.id.nativeAdSponsoredLabel);
            mediaView.setListener(new MediaViewListener() {
                public void onComplete(MediaView mediaView) {
                }

                public void onEnterFullscreen(MediaView mediaView) {
                }

                public void onExitFullscreen(MediaView mediaView) {
                }

                public void onFullscreenBackground(MediaView mediaView) {
                }

                public void onFullscreenForeground(MediaView mediaView) {
                }

                public void onPause(MediaView mediaView) {
                }

                public void onPlay(MediaView mediaView) {
                }

                public void onVolumeChange(MediaView mediaView, float f) {
                }
            });
            TextView textView4 = (TextView) view.findViewById(R.id.nativeAdSocialContext);
            Button button = (Button) view.findViewById(R.id.nativeAdCallToAction);
            textView4.setText(nativeAd2.getAdSocialContext());
            button.setText(nativeAd2.getAdCallToAction());
            button.setVisibility(nativeAd2.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            textView.setText(nativeAd2.getAdvertiserName());
            textView2.setText(nativeAd2.getAdBodyText());
            textView3.setText(nativeAd2.getSponsoredTranslation());
            ArrayList arrayList = new ArrayList();
            arrayList.add(adIconView);
            arrayList.add(mediaView);
            arrayList.add(button);
            nativeAd2.registerViewForInteraction(view, mediaView, (MediaView) adIconView, (List<View>) arrayList);
            NativeAdBase.NativeComponentTag.tagView(adIconView, NativeAdBase.NativeComponentTag.AD_ICON);
            NativeAdBase.NativeComponentTag.tagView(textView, NativeAdBase.NativeComponentTag.AD_TITLE);
            NativeAdBase.NativeComponentTag.tagView(textView2, NativeAdBase.NativeComponentTag.AD_BODY);
            NativeAdBase.NativeComponentTag.tagView(textView4, NativeAdBase.NativeComponentTag.AD_SOCIAL_CONTEXT);
            NativeAdBase.NativeComponentTag.tagView(button, NativeAdBase.NativeComponentTag.AD_CALL_TO_ACTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
