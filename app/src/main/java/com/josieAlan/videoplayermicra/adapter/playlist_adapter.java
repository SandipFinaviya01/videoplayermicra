package com.josieAlan.videoplayermicra.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.josieAlan.videoplayermicra.R;
import com.josieAlan.videoplayermicra.model.VideoItem;

import java.util.List;

public class playlist_adapter extends RecyclerView.Adapter<playlist_adapter.holder> {
    private final Context ctx;
    private final List<VideoItem> list;
    private final SimpleExoPlayer player;

    public playlist_adapter(Context ctx, List<VideoItem> list, SimpleExoPlayer player) {
        this.ctx = ctx;
        this.list = list;
        this.player = player;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.playlist_card, viewGroup, false);
        return new holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int i) {
        holder.position = i;
        Glide.with(ctx).load(list.get(i).getDATA()).into(holder.i1);
        holder.t1.setText(list.get(i).getDISPLAY_NAME());
        holder.t2.setText(list.get(i).getDURATION());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class holder extends RecyclerView.ViewHolder {
        final ImageView i1;
        final TextView t1;
        final TextView t2;
        int position;

        holder(@NonNull View itemView) {
            super(itemView);
            i1 = itemView.findViewById(R.id.thumb);
            t1 = itemView.findViewById(R.id.name);
            t2 = itemView.findViewById(R.id.duration);

            itemView.setOnClickListener(v -> player.seekTo(position, 0));
        }
    }
}
