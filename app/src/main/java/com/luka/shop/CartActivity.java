package com.luka.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luka.shop.model.Product;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {

    RecyclerView cartItems;
    LinearLayoutManager layout;
    FirestoreRecyclerAdapter adapter;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    Button btnContinue, btnCheckout;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItems=findViewById(R.id.cartItems);
        layout=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        cartItems.setLayoutManager(layout);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        query=FirebaseFirestore.getInstance().collection("cart");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query,Product.class).build();
        adapter=new FirestoreRecyclerAdapter<Product,CartProductHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartProductHolder holder, int position, @NonNull Product model) {
                holder.name.setText(model.getName());
                holder.price.setText(model.getPrice() +" kn");
                StorageReference productImageRef = mStorageRef.child("products/" + model.getId() +".jpg");
                productImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.img);
                    }
                });
            }

            @NonNull
            @Override
            public CartProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_product,parent,false);
                return new CartProductHolder(view);
            }
        };
        cartItems.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}