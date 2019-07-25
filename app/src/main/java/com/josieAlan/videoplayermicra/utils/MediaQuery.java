package com.josieAlan.videoplayermicra.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.josieAlan.videoplayermicra.model.Folder;
import com.josieAlan.videoplayermicra.model.VideoItem;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MediaQuery {
    private final Context context;

    public MediaQuery(Context context) {
        this.context = context;
    }


    public List<Folder> getfolderList() {
        String[] projection = {
                MediaStore.Video.Media.BUCKET_ID,
        };
        String selection = null;
        Cursor cursor;
        cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection, null,
                null);

        LinkedHashSet<String> v1 = new LinkedHashSet<>();
        List<String> f1;
        while (cursor.moveToNext()) {
            v1.add(cursor.getString(0));
        }
        f1 = new ArrayList<>(v1);


        String[] projection1 = {
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.BUCKET_ID
        };
        String selection1 = MediaStore.Video.Media.BUCKET_ID + " =?";
        List<Folder> folders = new ArrayList<>();
        for (int i = 0; i < f1.size(); i++) {
            String[] selectionArgs = new String[]{f1.get(i)};
            cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection1,
                    selection1, selectionArgs,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC");
            Folder folder;
            while (cursor.moveToNext()) {
                folder = new Folder();
                folder.setBucket(cursor.getString(0));
                folder.setData(cursor.getString(1));
                folder.setBid(cursor.getString(2));
                folder.setSize(String.valueOf(cursor.getCount()));
                folders.add(folder);
                break;
            }
        }
        return folders;
    }


    public List<VideoItem> getAllVideo(String foldername, int order) {
        Cursor cursor;
        String x = null;
        String selection = MediaStore.Video.Media.BUCKET_ID + " =?";
        String[] selectionArgs = new String[]{foldername};
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
        };
        switch (order) {
            case 0:
                x = MediaStore.Video.Media.DATE_MODIFIED + " DESC";
                break;
            case 1:
                x = MediaStore.Video.Media.DATE_MODIFIED;
                break;
            case 2:
                x = MediaStore.Video.Media.DURATION + " DESC";
                break;
            case 3:
                x = MediaStore.Video.Media.DURATION;
                break;
            case 4:
                x = MediaStore.Video.Media.DISPLAY_NAME;
                break;
            case 5:
                x = MediaStore.Video.Media.DISPLAY_NAME + " DESC";
                break;
            case 6:
                x = MediaStore.Video.Media.SIZE + " DESC";
                break;
            case 7:
                x = MediaStore.Video.Media.SIZE;
                break;
        }
        cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection, selectionArgs,
                x);

        List<VideoItem> videoItems = new ArrayList<>();
        VideoItem videoItem;
        while (cursor.moveToNext()) {
            videoItem = new VideoItem();
            videoItem.set_ID(cursor.getString(0));
            videoItem.setSIZE(size(cursor.getString(1)));
            videoItem.setDATE(date(cursor.getString(2)));
            videoItem.setDATA(cursor.getString(3));
            videoItem.setDISPLAY_NAME(cursor.getString(4));
            videoItem.setDURATION(duration(cursor.getString(5)));
            videoItems.add(videoItem);
        }
        return videoItems;
    }

    private String duration(String x) {
        int y = Integer.parseInt(x);

        long ho = TimeUnit.MILLISECONDS.toHours(y);
        long mo = TimeUnit.MILLISECONDS.toMinutes(y) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(y));
        long so = TimeUnit.MILLISECONDS.toSeconds(y) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(y));

        if (ho >= 1) return String.format("%02d:%02d:%02d", ho, mo, so);
        else return String.format("%02d:%02d", mo, so);

    }

    private String date(String x) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date(Long.parseLong(x) * 1000));
    }

    private String size(String x) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        double s = Double.parseDouble(x);
        s = s / 1024;
        if (s < 1024) {
            return df.format(s) + " KB";
        } else {
            s = s / 1024;
            if (s < 1024) {
                return df.format(s) + " MB";
            } else {
                s = s / 1024;
                return df.format(s) + " GB";
            }

        }
    }

}
