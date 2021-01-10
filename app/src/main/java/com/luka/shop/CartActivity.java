package com.luka.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.luka.shop.adapter.CartProductsAdapter;
import com.luka.shop.model.Product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView cartItems;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter cartProductsAdapter;
    Button btnContinue, btnCheckout;
    String userId;
    TextView emptyCartNote, yourCartNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // instantiate widgets
        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(this);

        yourCartNote = findViewById(R.id.yourCartNote);
        emptyCartNote = findViewById(R.id.emptyCartNote);

        db = FirebaseFirestore.getInstance();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DisplayCartProducts();

    }


    private void DisplayCartProducts() {
        Query query = db.collection("cart").document(userId).collection("products");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
        cartProductsAdapter = new CartProductsAdapter(options, getApplicationContext(), userId);
        cartItems = (RecyclerView) findViewById(R.id.cartItems);
        LinearLayoutManager cartLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartItems.setLayoutManager(cartLayout);
        cartItems.setAdapter(cartProductsAdapter);

        //is cart empty
        db.collection("cart").document(userId).collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot doc = task.getResult();
                if (doc.isEmpty()) {
                    btnCheckout.setVisibility(View.GONE);
                    btnContinue.setVisibility(View.GONE);
                    emptyCartNote.setVisibility(View.VISIBLE);
                    yourCartNote.setVisibility(View.INVISIBLE);

                }
            }
        });
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
        switch (v.getId()) {
            case R.id.btnCheckout:
                completeOrder();
        }
    }

    private void completeOrder() {

        // using date as order id
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // fetch cart products and order's total cost
        db.collection("cart").document(userId).collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int totalCost = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Map<String, Object> data = new HashMap<>();
                    data.put("id", document.getData().get("id"));
                    data.put("name", document.getData().get("name"));
                    data.put("price", document.getData().get("price"));
                    totalCost += Integer.parseInt(document.getData().get("price").toString());
                    db.collection("orders").document(userId).collection(formatter.format(date)).document(document.getId()).set(data);
                }
                Map<String, Object> cost = new HashMap<>();
                cost.put("total cost", totalCost);
                db.collection("orders").document(userId).collection(formatter.format(date)).document("total cost").set(cost);
            }
        });
        db.collection("cart").document(userId).collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
            }
        });
    }
}