package com.good.hdvideoplayer.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.good.hdvideoplayer.PrefData;
import com.good.hdvideoplayer.R;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.LoadAdError;

public class AppAdsActivity extends AppCompatActivity {
    Toolbar toolbar;
    CardView card1,card2,card3,card4,card5,card6,card7,card8,card9;
//    private com.google.android.gms.ads.AdView GAdView;
    LinearLayout linBannerAdview;
    private PrefData prefData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_activity_ads);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        prefData = PrefData.getInstance();
        card1 = (CardView)findViewById(R.id.cardnameart);
        card2 = (CardView)findViewById(R.id.cardinstagrid);
        card3 = (CardView)findViewById(R.id.cardvideoply);
        card4 = (CardView)findViewById(R.id.cardfancyfont);
        card5 = (CardView)findViewById(R.id.cardcharstyle);
        card6 = (CardView)findViewById(R.id.cardinststory);
        card7 = (CardView)findViewById(R.id.cardinstpic);
        card8 = (CardView)findViewById(R.id.cardstylish);
        card9 = (CardView)findViewById(R.id.carddslr);
        linBannerAdview = (LinearLayout)findViewById(R.id.adView);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.goodaccount248.nameart248"));
                startActivity(i);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.good.instagridmaker"));
                startActivity(i);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.textstyle.whatschat"));
                startActivity(i);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.indtxt.fancytextfont"));
                startActivity(i);
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.styler.chatstyler"));
                startActivity(i);
            }
        });

        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.story.instasaver"));
                startActivity(i);
            }
        });

        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.good.instamediasaver"));
                startActivity(i);
            }
        });
        card8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.good.chatstyler"));
                startActivity(i);
            }
        });

        card9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.good.dslrblurhdcam"));
                startActivity(i);
            }
        });

        if (prefData.referClass == null){
            return;
        }

//        GAdView = new com.google.android.gms.ads.AdView(this);
//        GAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
//        GAdView.setAdUnitId(prefData.referClass.fbBannerKey3);
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
//
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

    }
}
