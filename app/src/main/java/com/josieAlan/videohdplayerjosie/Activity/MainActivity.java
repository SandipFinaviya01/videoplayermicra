package com.josieAlan.videohdplayerjosie.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.josieAlan.videohdplayerjosie.PrefData;
import com.josieAlan.videohdplayerjosie.R;
import com.josieAlan.videohdplayerjosie.databinding.ActivityMainBinding;
import com.josieAlan.videohdplayerjosie.model.ImageUploadInfo;
import com.josieAlan.videohdplayerjosie.model.UploadUploadInfo;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private PrefData prefData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        prefData = PrefData.getInstance();
        if (prefData.isNetwork()) {
            mFirebaseInstance = FirebaseDatabase.getInstance();

            mFirebaseDatabase = mFirebaseInstance.getReference();

            Query query2 = mFirebaseDatabase.child("Upload").orderByChild("appName").equalTo(getString(R.string.name));
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey();
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    mFirebaseDatabase.child(path).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UploadUploadInfo imageUploadInfo = dataSnapshot.getValue(UploadUploadInfo.class);
                            if (imageUploadInfo == null){
                                setRefernceClass();
                                Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }

                            if (imageUploadInfo.value.equals("0")){
                                goAhead();
                            }else {
                                showNewVersionDialog(imageUploadInfo.appLink);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Failed to read user", databaseError.toException());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRefernceClass();

                    Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
    }

    private void setRefernceClass() {
        prefData.referClass = new ImageUploadInfo(
                "",
                "1300776820088166_1300777220088126",
                "1300776820088166_1300777366754778",
                "1300776820088166_1300811566751358",
                "1300776820088166_1300777256754789",
                "1300776820088166_1300777380088110",
                "1300776820088166_1300811566751358",
                "1300776820088166_1300777233421458",
                "1300776820088166_1300777370088111",
                "",
                "1300776820088166_1300777246754790",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "", "",
                "",
                "", "",
                "",
                "", "",
                "",
                "", "",
                "",
                "", "",
                "",
                ""

        );
    }

    private void showNewVersionDialog(final String appLink) {


        new AlertDialog.Builder(this)
                .setTitle("Update Available")
                .setMessage("New Version of App is Available Plz download it")
                .setPositiveButton("Open store", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent3 = new Intent("android.intent.action.VIEW");
                        intent3.setData(Uri.parse(appLink));
                        startActivity(intent3);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    private void goAhead() {
        Query query = mFirebaseDatabase.child("Keys").orderByChild("appName").equalTo(getString(R.string.name));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check for null
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                String key = nodeDataSnapshot.getKey();
                String path = "/" + dataSnapshot.getKey() + "/" + key;
//                        mFirebaseDatabase.child(path).updateChildren(result);
                mFirebaseDatabase.child(path).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        prefData.referClass = dataSnapshot.getValue(ImageUploadInfo.class);
//                    ReferClass referClass = dataSnapshot.getValue(ReferClass.class);
                        if (prefData.referClass == null) {
                            Log.e(TAG, "User data is null!");
                            setRefernceClass();
                            Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }

                        loadAds();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },1000);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Failed to read user", databaseError.toException());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read user", databaseError.toException());
            }
        });
    }

    private void loadAds() {
        if (prefData.isNetwork()) {
            prefData.showHomeAd();
            prefData.showDetailAd();
            prefData.showMoreAd();
            prefData.showSubAd();
        }
    }


}
