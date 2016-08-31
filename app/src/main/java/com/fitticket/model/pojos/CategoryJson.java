package com.fitticket.model.pojos;

import java.util.ArrayList;

/**
 * Created by Fiticket on 25/11/15.
 */
public class CategoryJson {

    private String id;
    private int count;
    private String name;
    private String image;
    private boolean isFavorite;
    private ArrayList<Integer> categoryList;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Integer> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<Integer> categoryList) {
        this.categoryList = categoryList;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryImage() {
        return image;
    }

    public void setCategoryImage(String categoryImage) {
        this.image = categoryImage;
    }
}
