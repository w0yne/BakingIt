package com.w0yne.android.bakingit.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Recipe extends RealmObject {

    @PrimaryKey
    public int uid;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    public String name;
    @SerializedName("servings")
    public int servings;
    @SerializedName("image")
    public String image;

    @Ignore
    @SerializedName("ingredients")
    private List<Ingredient> ingredients;
    @Ignore
    @SerializedName("steps")
    private List<Step> steps;

    public RealmList<Ingredient> ingredientList;
    public RealmList<Step> stepList;

    public void normalizedData() {
        uid = id;
        ingredientList = new RealmList<>();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ingredient.normalizedData(id, i);
            ingredientList.add(ingredient);
        }
        stepList = new RealmList<>();
        for (Step step : steps) {
            step.normalizedData(id);
            stepList.add(step);
        }
    }

    public String getDisplayIngredients() {
        StringBuilder sb = new StringBuilder();
        for (Ingredient ingredient : ingredientList) {
            sb.append(ingredient.ingredient).append(" - ")
                    .append(ingredient.quantity).append(" ")
                    .append(ingredient.measure).append("\n");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}