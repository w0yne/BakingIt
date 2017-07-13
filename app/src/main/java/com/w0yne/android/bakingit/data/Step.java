package com.w0yne.android.bakingit.data;

import android.support.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Step extends RealmObject {

    @PrimaryKey
    public int uid;

    @SerializedName("id")
    private int id;

    @SerializedName("shortDescription")
    public String shortDescription;
    @SerializedName("description")
    public String description;
    @SerializedName("videoURL")
    public String videoURL;
    @SerializedName("thumbnailURL")
    public String thumbnailURL;

    public void normalizedData(int recipeId) {
        uid = recipeId * 100 + id;
    }

    public int getUid() {
        return uid;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }
}