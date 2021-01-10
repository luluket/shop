package com.luka.shop.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luka.shop.R;


public class CategoryHolder extends RecyclerView.ViewHolder {
    public TextView name;

    public CategoryHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.category);
    }
}
