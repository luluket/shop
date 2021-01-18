package com.luka.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luka.shop.holder.CategoryHolder;
import com.luka.shop.R;
import com.luka.shop.model.Category;


public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryHolder> {
    Context mContext;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options, Context context) {
        super(options);
        this.mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {
        // bind Category object to CategoryHolder

        holder.name.setText(model.getName());
        // fetch category id and name and pass to HomeActivity
        holder.name.setOnClickListener(v -> FirebaseFirestore.getInstance().collection("category").whereEqualTo("name", model.getName()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Intent intent = new Intent("filter-categories");
                    intent.putExtra("id", document.getId());
                    intent.putExtra("category", model.getName());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            }
        }));

    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.category_recycler_view_item for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler_view_item, parent, false);
        return new CategoryHolder(view);
    }

}
