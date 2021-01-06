package com.luka.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luka.shop.model.Product;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView cartItems;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter cartProductsAdapter;
    StorageReference mStorageRef;
    Button btnContinue, btnCheckout;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // instantiate widgets
        cartItems = (RecyclerView) findViewById(R.id.cartItems);
        LinearLayoutManager cartLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartItems.setLayoutManager(cartLayout);

        btnContinue=findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        btnCheckout=findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DisplayCartProducts();

    }

    private void DisplayCartProducts() {
        Query query = db.collection("cart").document(userId).collection("products");
        FirestoreRecyclerOptions<Product> setCartProducts = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
        cartProductsAdapter = new FirestoreRecyclerAdapter<Product, CartProductHolder>(setCartProducts) {
            @Override
            protected void onBindViewHolder(@NonNull CartProductHolder holder, int position, @NonNull Product model) {
                holder.name.setText(model.getName());
                holder.price.setText(model.getPrice() + " kn");
                holder.close.setOnClickListener(v -> {
                    DocumentReference mRef = db.collection("cart").document(userId).collection("products").document(String.valueOf(model.getId()));
                    mRef.delete();
                });
                StorageReference productImageRef = mStorageRef.child("products/" + model.getId() + ".jpg");
                productImageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.img));
            }

            @NonNull
            @Override
            public CartProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_product, parent, false);
                return new CartProductHolder(view);
            }
        };
        cartItems.setAdapter(cartProductsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartProductsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cartProductsAdapter.stopListening();
    }

    @Override
    public void onClick(View v) {

    }
}