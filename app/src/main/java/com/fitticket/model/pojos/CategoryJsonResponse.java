package com.fitticket.model.pojos;

import com.fitticket.model.pojos.base.BaseResponseJson;

import java.util.ArrayList;

/**
 * Created by Fiticket on 25/11/15.
 */
public class CategoryJsonResponse extends BaseResponseJson{

    CategoryJsonList data;


    public CategoryJsonList getData() {
        return data;
    }

    public void setData(CategoryJsonList data) {
        this.data = data;
    }


    public class CategoryJsonList {
        ArrayList<CategoryJson> categories;

        public ArrayList<CategoryJson> getCategories() {
            return categories;
        }

        public void setCategories(ArrayList<CategoryJson> categories) {
            this.categories = categories;
        }


    }
}
