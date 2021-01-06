package com.luka.shop.model;

public class Product {
    private String mName, mDescription;
    private int mPrice, id;

    public Product() {

    }

    public Product(String name, String description, int price) {

        this.mName = name;
        this.mDescription = description;
        this.mPrice = price;
    }

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


}
