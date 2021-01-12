package com.luka.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luka.shop.holder.CartProductHolder;
import com.luka.shop.R;
import com.luka.shop.model.Product;
import com.squareup.picasso.Picasso;

public class CartProductsAdapter extends FirestoreRecyclerAdapter<Product, CartProductHolder> {
    Context mContext;
    String mUserId;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CartProductsAdapter(@NonNull FirestoreRecyclerOptions<Product> options, Context context, String userId) {
        super(options);
        this.mContext = context;
        this.mUserId = userId;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartProductHolder holder, int position, @NonNull Product model) {
        holder.name.setText(model.getName());
        holder.price.setText(model.getPrice() + " kn");
        holder.close.setOnClickListener(v -> {
            DocumentReference mRef = FirebaseFirestore.getInstance().collection("cart").document(mUserId).collection("products").document(String.valueOf(model.getId()));
            mRef.delete();
        });
        StorageReference productImageRef = FirebaseStorage.getInstance().getReference().child("products/" + model.getId() + ".jpg");
        productImageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.img));
    }

    @NonNull
    @Override
    public CartProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_product_recycler_view_item, parent, false);
        return new CartProductHolder(view);
    }
}
