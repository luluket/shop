package com.luka.shop;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.BreakIterator;

public class ProductHolder extends RecyclerView.ViewHolder {
    TextView price;
    TextView name;
    ImageView img;
    LinearLayout container;

    public ProductHolder(@NonNull View itemView) {
        super(itemView);
        container=itemView.findViewById(R.id.container);
        name=itemView.findViewById(R.id.name);
        price=itemView.findViewById(R.id.price);
        img=itemView.findViewById(R.id.product_img);
    }
}
