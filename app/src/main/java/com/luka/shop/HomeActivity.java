package com.luka.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
            // if user not authenticated, redirect to login page and remove this activity from stack
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            finish();
            startActivity(intent);
        }
    }

    private void displayCategories() {
        // fetch every document in category collection
        Query query = db.collection("category");

        // Configure recycler adapter options:
        // * query is the Query object defined above
        // * Category.class instructs adapter to convert each DocumentSnapshot to a Category object
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();

        // create adapter object and set horizontal layout manager
        categoryAdapter = new CategoryAdapter(options, getApplicationContext());
        categories = (RecyclerView) findViewById(R.id.categories);
        LinearLayoutManager categoriesLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categories.setLayoutManager(categoriesLayout);

        // attach the adapter to RecyclerView widget
        categories.setAdapter(categoryAdapter);

        // Listen for category filter
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("filter-categories")); // get category id,name on click
    }

    private void displayProducts() {
        // fetch every document in products collection
        Query query = db.collection("products");

        // Configure recycler adapter options:
        // * query is the Query object defined above
        // * Product.class instructs adapter to convert each DocumentSnapshot to a Product object
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();

        // create adapter object and set vertical layout manager
        productAdapter = new ProductAdapter(options, getApplicationContext(), mStorageRef);
        products = (RecyclerView) findViewById(R.id.products);
        LinearLayoutManager productsLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        products.setLayoutManager(productsLayout);

        // attach the adapter to RecyclerView widget
        products.setAdapter(productAdapter);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            String category = intent.getStringExtra("category");

            if (category.equals("all")) {
                Query query = db.collection("products").whereEqualTo("all", db.document("category/" + id));
                FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
                productAdapter.updateOptions(options);
            } else {
                Query query = db.collection("products").whereEqualTo("category_id", db.document("category/" + id));
                FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
                productAdapter.updateOptions(options);
            }

        }

    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.miniprofileImg:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class)); // redirect, but remain on stack
                break;
            case R.id.cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class)); // redirect, but remain on stack
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // FirestoreRecyclerAdapter uses snapshot listener to the Firestore query
        // begin listening for data
        categoryAdapter.startListening();
        productAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();

        // removes snapshot listener and all data in the adapter
        categoryAdapter.stopListening();
        productAdapter.stopListening();

    }
}