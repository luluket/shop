package com.luka.shop.model;

import java.lang.ref.Reference;

public class Product {
    private String mName, mDescription;
    private int mPrice, id;
    private Reference<Category> categoryReference;

    public int getId() {
        return id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public Reference<Category> getReference() {
        return categoryReference;
    }

    public void setCategoryReference(Reference<Category> reference) {
        categoryReference = reference;
    }
}
