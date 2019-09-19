package com.josieAlan.videohdplayerjosie.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.josieAlan.videohdplayerjosie.R;
import com.josieAlan.videohdplayerjosie.databinding.RequestCellBinding;
import com.josieAlan.videohdplayerjosie.model.RequestModel;
import com.josieAlan.videohdplayerjosie.viewholder.RequestHolder;

import java.util.ArrayList;
import java.util.List;

public class RequestListAdapter extends RecyclerView.Adapter {
    List<RequestModel> requestModelList = new ArrayList<>();
    private RequestCellBinding requestCellBinding;

    public RequestListAdapter(List<RequestModel> requestModelList) {
        this.requestModelList = requestModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        requestCellBinding = DataBindingUtil.inflate(inflater, R.layout.request_cell,parent,false);
        return new RequestHolder(requestCellBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RequestHolder requestHolder = (RequestHolder) holder;
        requestHolder.setItem(requestModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return requestModelList.size();
    }
}
