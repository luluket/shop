package com.luka.shop.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.luka.shop.R;

public class CartProductHolder extends RecyclerView.ViewHolder {
    public TextView price;
    public TextView name;
    public ImageView img, close;
    public ConstraintLayout container;

    public CartProductHolder(@NonNull View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.cart_container);
        name = itemView.findViewById(R.id.cart_product_name);
        price = itemView.findViewById(R.id.cart_product_price);
        img = itemView.findViewById(R.id.cart_product_img);
        close = itemView.findViewById(R.id.close);
    }
}
