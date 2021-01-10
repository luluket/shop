package com.luka.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
        holder.name.setText(model.getName());
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoryHolder(view);
    }
}
