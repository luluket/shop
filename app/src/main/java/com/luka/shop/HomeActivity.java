package com.luka.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luka.shop.model.Category;
import com.luka.shop.model.Product;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView banner;
    ImageView profileImg;

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    String userId;

    RecyclerView categories;
    RecyclerView products;
    FirestoreRecyclerAdapter adapter;
    FirestoreRecyclerAdapter ad;
    LinearLayoutManager layout;
    LinearLayoutManager lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        banner=findViewById(R.id.banner);
        profileImg=findViewById(R.id.profileImg);
        profileImg.setOnClickListener(this);

        layout=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        categories=findViewById(R.id.categories);
        categories.setLayoutManager(layout);

        lay=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        products=findViewById(R.id.products);
        products.setLayoutManager(lay);

        user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }else {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            userId = mAuth.getCurrentUser().getUid();

            StorageReference profileRef = mStorageRef.child("users/" + userId + "/profile");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profileImg);
                }
            });

            // setting up categories
            Query query=FirebaseFirestore.getInstance().collection("category");
            FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query,Category.class).build();

            adapter = new FirestoreRecyclerAdapter<Category,CategoryHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {
                    holder.name.setText(model.getName());
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

                @NonNull
                @Override
                public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category,parent,false);
                    return new CategoryHolder(view);
                }
            };
            categories.setAdapter(adapter);

            // setting up products
            Query q = FirebaseFirestore.getInstance().collection("products");
            FirestoreRecyclerOptions<Product> o = new FirestoreRecyclerOptions.Builder<Product>().setQuery(q,Product.class).build();
            ad = new FirestoreRecyclerAdapter<Product,ProductHolder>(o) {
                @Override
                protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {
                    holder.name.setText(model.getName());
                    holder.price.setText(String.valueOf(model.getPrice())+" kn");
                    StorageReference productImageRef = mStorageRef.child("products/" + model.getId() +".jpg");
                    productImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.img);
                        }
                    });
                    holder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(),ProductActivity.class);
                            intent.putExtra("name", model.getName());
                            intent.putExtra("price", model.getPrice());
                            intent.putExtra("description", model.getDescription());
                            intent.putExtra("path","products/"+ model.getId() +".jpg");
                            startActivity(intent);
                        }
                    });

                }

                @NonNull
                @Override
                public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product,parent,false);
                    return new ProductHolder(view);
                }
            };
            products.setAdapter(ad);
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.profileImg:
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user==null){
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }
        else {
            adapter.startListening();
            ad.startListening();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(user==null){
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }
        else{
            adapter.stopListening();
            ad.stopListening();
        }

    }
}