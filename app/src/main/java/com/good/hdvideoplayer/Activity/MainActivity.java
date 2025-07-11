package com.good.hdvideoplayer.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.good.hdvideoplayer.PrefData;
import com.good.hdvideoplayer.R;
import com.good.hdvideoplayer.databinding.ActivityMainBinding;
import com.good.hdvideoplayer.model.ImageUploadInfo;
import com.good.hdvideoplayer.model.UploadUploadInfo;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
//    private DatabaseReference mFirebaseDatabase;
//    private FirebaseDatabase mFirebaseInstance;
    private PrefData prefData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        prefData = PrefData.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefernceClass();
                Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
        loadAds();
//        if (prefData.isNetwork()) {
//            mFirebaseInstance = FirebaseDatabase.getInstance();
//
//            mFirebaseDatabase = mFirebaseInstance.getReference();
//
//            Query query2 = mFirebaseDatabase.child("Upload").orderByChild("appName").equalTo(getString(R.string.name));
//            query2.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
//                    String key = nodeDataSnapshot.getKey();
//                    String path = "/" + dataSnapshot.getKey() + "/" + key;
//                    mFirebaseDatabase.child(path).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            UploadUploadInfo imageUploadInfo = dataSnapshot.getValue(UploadUploadInfo.class);
//                            if (imageUploadInfo == null){
//                                setRefernceClass();
//                                Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
//                                startActivity(intent);
//                                finish();
//                                return;
//                            }
//
//                            if (imageUploadInfo.value.equals("0")){
//                                goAhead();
//                            }else {
//                                showNewVersionDialog(imageUploadInfo.appLink);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Log.e(TAG, "Failed to read user", databaseError.toException());
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//        else {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    setRefernceClass();
//
//                    Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }, 2000);
//        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    private void setRefernceClass() {
        prefData.referClass = new ImageUploadInfo(
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

//    private void goAhead() {
//        Query query = mFirebaseDatabase.child("Keys").orderByChild("appName").equalTo(getString(R.string.name));
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Check for null
//                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
//                String key = nodeDataSnapshot.getKey();
//                String path = "/" + dataSnapshot.getKey() + "/" + key;
////                        mFirebaseDatabase.child(path).updateChildren(result);
//                  Log.i("DD","Val=="+path.toString());
//                mFirebaseDatabase.child(path).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        prefData.referClass = dataSnapshot.getValue(ImageUploadInfo.class);
////                    ReferClass referClass = dataSnapshot.getValue(ReferClass.class);
//                            Log.i("DD","Datasnap=="+dataSnapshot.toString());
//                        if (prefData.referClass == null) {
//                            Log.e(TAG, "User data is null!");
//                            setRefernceClass();
//                            Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
//                            startActivity(intent);
//                            finish();
//                            return;
//                        }
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(MainActivity.this, FolderListActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        },1000);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Log.e(TAG, "Failed to read user", databaseError.toException());
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to read user", databaseError.toException());
//            }
//        });
//    }

    private void loadAds() {
        if (prefData.isNetwork()) {
            prefData.showHomeAd();
            prefData.showDetailAd();
            prefData.showMoreAd();
            prefData.showSubAd();
        }
    }


}
