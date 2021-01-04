package com.luka.shop.model;

public class Category {
    private String mName;
    public Category(){

    }
    public Category(String name){
        this.mName=name;
    }

    public String getName(){return mName;}

    public void setName(String name) { mName = name; }

}
