package com.luka.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luka.shop.adapter.CategoryAdapter;
import com.luka.shop.adapter.ProductAdapter;
import com.luka.shop.model.Category;
import com.luka.shop.model.Product;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user;
    FirestoreRecyclerAdapter categoryAdapter;
    FirestoreRecyclerAdapter productAdapter;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    RecyclerView categories;
    RecyclerView products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //check if user authenticated, if not, redirect to login form
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            // instantiate widgets
            ImageView profileImg = findViewById(R.id.miniprofileImg);
            profileImg.setOnClickListener(this);

            ImageView cart = findViewById(R.id.cart);
            cart.setOnClickListener(this);

            // load profile image for authenticated user
            mStorageRef = FirebaseStorage.getInstance().getReference();
            String userId = user.getUid();
            StorageReference profileRef = mStorageRef.child("users/" + userId + "/profile");
            profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImg));

            //db
            db = FirebaseFirestore.getInstance();

            displayCategories();
            displayProducts();
        } else {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
    }

    private void displayCategories() {
        Query query = db.collection("category");
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();
        categoryAdapter = new CategoryAdapter(options, getApplicationContext());
        categories = (RecyclerView) findViewById(R.id.categories);
        LinearLayoutManager categoriesLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categories.setLayoutManager(categoriesLayout);
        categories.setAdapter(categoryAdapter);
    }

    private void displayProducts() {
        Query query = db.collection("products");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
        productAdapter = new ProductAdapter(options, getApplicationContext(), mStorageRef);
        products = (RecyclerView) findViewById(R.id.products);
        LinearLayoutManager productsLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(productsLayout);
        products.setAdapter(productAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.miniprofileImg:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        } else {
            categoryAdapter.startListening();
            productAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (user == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        } else {
            categoryAdapter.stopListening();
            productAdapter.stopListening();
        }
    }
}