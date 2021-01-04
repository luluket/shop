package com.luka.shop;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luka.shop.model.Product;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {
    TextView price, name,description;
    ImageView image;
    StorageReference mStorageRef;
    Button addToCart;
    String productId;
    int cost;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        image=findViewById(R.id.img);
        name=findViewById(R.id.name);
        price=findViewById(R.id.price);
        description=findViewById(R.id.description);
        productId=getIntent().getStringExtra("id");
        cost=getIntent().getIntExtra("price",0);

        name.setText(getIntent().getStringExtra("name"));
        price.setText(cost + " kn");
        description.setText(getIntent().getStringExtra("description"));

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference productImageRef = mStorageRef.child(productId);
        productImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(image);
            }
        });

        db=FirebaseFirestore.getInstance();
        addToCart=findViewById(R.id.addtoCart);

        DocumentReference mRef = db.collection("cart").document(productId); // where to push data
        // check if product exists in cart and update button if true, if it doesnt exist in cart, allow clicking on button and adding to cart
        mRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document=task.getResult();
                if(task.isSuccessful()){
                    if(document.exists()){
                        addToCart.setText("In cart");
                    }
                    else{
                        addToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // get the reference and check if document already exist, if it doesnt, add to cart
                                mRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Map<String,Object> product = new HashMap<>();
                                        product.put("id",Integer.parseInt(productId));
                                        product.put("name",name.getText());
                                        product.put("price",cost);
                                        product.put("description",description.getText());
                                        mRef.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProductActivity.this, name.getText() + " added to cart", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }
}
