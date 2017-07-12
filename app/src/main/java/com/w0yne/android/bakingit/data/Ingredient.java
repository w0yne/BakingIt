package com.w0yne.android.bakingit.data;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Ingredient extends RealmObject {

    @PrimaryKey
    public int uid;

    @SerializedName("quantity")
    public float quantity;
    @SerializedName("measure")
    public String measure;
    @SerializedName("ingredient")
    public String ingredient;

    public void normalizedData(int recipeId, int index) {
        uid = recipeId * 100 + index;
    }
}