package com.good.hdvideoplayer.utils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.good.hdvideoplayer.model.Folder;

import java.util.List;

public class EmployeeDiffCallback extends DiffUtil.Callback {

    private final List<Folder> mOldFolderList;
    private final List<Folder> mNewFolderList;

    public EmployeeDiffCallback(List<Folder> oldEmployeeList, List<Folder> newEmployeeList) {
        this.mOldFolderList = oldEmployeeList;
        this.mNewFolderList = newEmployeeList;
    }

    @Override
    public int getOldListSize() {
        return mOldFolderList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewFolderList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldFolderList.get(oldItemPosition).getBid() == mNewFolderList.get(
                newItemPosition).getBid();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Folder oldFolder = mOldFolderList.get(oldItemPosition);
        final Folder newFolder = mNewFolderList.get(newItemPosition);

        return oldFolder.getBucket().equals(newFolder.getBucket());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
