package com.luka.shop;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryHolder extends RecyclerView.ViewHolder {
    TextView name;
    public CategoryHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.category);
    }
}
