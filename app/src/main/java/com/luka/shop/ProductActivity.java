package com.luka.shop;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProductActivity extends AppCompatActivity {
    TextView price, name,description;
    ImageView image;
    StorageReference mStorageRef;
    StorageReference productImageRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        image=findViewById(R.id.img);
        name=findViewById(R.id.name);
        price=findViewById(R.id.price);
        description=findViewById(R.id.description);

        name.setText(getIntent().getStringExtra("name"));
        price.setText(String.valueOf(getIntent().getIntExtra("price",0)) + " kn");
        description.setText(getIntent().getStringExtra("description"));

        mStorageRef = FirebaseStorage.getInstance().getReference();
        productImageRef = mStorageRef.child(getIntent().getStringExtra("path"));
        productImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(image);
            }
        });
    }
}
