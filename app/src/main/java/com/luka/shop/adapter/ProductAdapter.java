package com.luka.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.StorageReference;
import com.luka.shop.ProductActivity;
import com.luka.shop.holder.ProductHolder;
import com.luka.shop.R;
import com.luka.shop.model.Product;
import com.squareup.picasso.Picasso;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductHolder> {
    StorageReference mStorageRef;
    Context mContext;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options, Context context, StorageReference storageRef) {
        super(options);
        this.mContext = context;
        this.mStorageRef = storageRef;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {
        if (position % 2 == 0) {
            holder.container.setBackgroundColor(ContextCompat.getColor(mContext, R.color.even));
        } else {
            holder.container.setBackgroundColor(ContextCompat.getColor(mContext, R.color.odd));
        }
        holder.name.setText(model.getName());
        holder.price.setText(model.getPrice() + " kn");
        StorageReference productImageRef = mStorageRef.child("products/" + model.getId() + ".jpg");
        productImageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.img));
        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ProductActivity.class);
            intent.putExtra("name", model.getName());
            intent.putExtra("price", model.getPrice());
            intent.putExtra("description", model.getDescription());
            intent.putExtra("path", "products/" + model.getId() + ".jpg");
            intent.putExtra("id", String.valueOf(model.getId()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_recycler_view_item, parent, false);
        return new ProductHolder(view);
    }
}
