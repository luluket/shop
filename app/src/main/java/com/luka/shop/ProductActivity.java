package com.luka.shop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class ProductActivity extends AppCompatActivity {
    TextView price, name, description;
    ImageView image;
    Button addToCart;
    String productId;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        // instantiate widgets
        image = findViewById(R.id.img);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        addToCart = findViewById(R.id.addtoCart);

        // load intents
        productId = getIntent().getStringExtra("id");
        String path = getIntent().getStringExtra("path");
        name.setText(getIntent().getStringExtra("name"));
        price.setText(getIntent().getIntExtra("price", 0) + " kn");
        description.setText(getIntent().getStringExtra("description"));

        // load product picture
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference productImageRef = mStorageRef.child(path);
        productImageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(image));

        //get authenticated user id
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        displayProduct();

    }

    private void displayProduct() {
        // check if product exists in cart and update button if true, if it doesn't exist in cart, allow clicking on button and adding to cart
        DocumentReference mRef = FirebaseFirestore.getInstance().collection("cart").document(userId).collection("products").document(productId); // cart/userId/products/productId
        mRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (task.isSuccessful()) {
                if (document.exists()) {
                    addToCart.setText("In cart");
                } else {
                    addToCart.setOnClickListener(v -> {
                        // get the reference and check if document already exist, if it doesn't, add to cart
                        mRef.get().addOnCompleteListener(task1 -> {
                            Map<String, Object> product = new HashMap<>();
                            product.put("id", Integer.parseInt(productId));
                            product.put("name", name.getText());
                            product.put("price", getIntent().getIntExtra("price", 0));
                            product.put("description", description.getText());
                            mRef.set(product).addOnSuccessListener(aVoid -> Toast.makeText(ProductActivity.this, name.getText() + " added to cart", Toast.LENGTH_SHORT).show());
                        });

                    });
                }
            }
        });
    }
}
