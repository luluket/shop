package com.luka.shop;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CartProductHolder extends RecyclerView.ViewHolder {
    TextView price;
    TextView name;
    ImageView img,close;
    ConstraintLayout container;

    public CartProductHolder(@NonNull View itemView) {
        super(itemView);
        container=itemView.findViewById(R.id.cart_container);
        name=itemView.findViewById(R.id.cart_product_name);
        price=itemView.findViewById(R.id.cart_product_price);
        img=itemView.findViewById(R.id.cart_product_img);
        close=itemView.findViewById(R.id.close);
    }
}
