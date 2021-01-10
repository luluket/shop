package com.luka.shop.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luka.shop.R;

public class ProductHolder extends RecyclerView.ViewHolder {
    public TextView price;
    public TextView name;
    public ImageView img;
    public LinearLayout container;

    public ProductHolder(@NonNull View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        img = itemView.findViewById(R.id.product_img);
    }
}
