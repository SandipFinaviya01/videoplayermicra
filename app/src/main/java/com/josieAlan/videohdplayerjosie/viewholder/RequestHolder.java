package com.josieAlan.videohdplayerjosie.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.josieAlan.videohdplayerjosie.databinding.RequestCellBinding;
import com.josieAlan.videohdplayerjosie.model.RequestModel;

public class RequestHolder extends RecyclerView.ViewHolder {
    private RequestCellBinding requestCellBinding;
    public RequestHolder(RequestCellBinding requestCellBinding) {
        super(requestCellBinding.getRoot());
        this.requestCellBinding = requestCellBinding;
    }
    public void setItem(RequestModel requestModel){
        String text = requestModel.requestNo + ").\n" + "Mobile No : " + requestModel.mobileNo
                + "\nWithdraw Money Amount : " + requestModel.money + " rupee"
                + "\nStatus : " + requestModel.status;
        requestCellBinding.tvRequest.setText(text);
        requestCellBinding.executePendingBindings();
    }
}
