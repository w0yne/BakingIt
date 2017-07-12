package com.w0yne.android.bakingit;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.w0yne.android.bakingit.data.Recipe;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class IngredientWidgetConfigure extends AppCompatActivity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_widget_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        List<Recipe> recipes = Realm.getDefaultInstance().where(Recipe.class).findAll();

        if (recipes.size() == 0) {
            Toast.makeText(this, R.string.error_no_data, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        List<String> recipeNames = new ArrayList<>();
        for (Recipe recipe : recipes) {
            recipeNames.add(recipe.name);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setItems(recipeNames.toArray(new String[recipeNames.size()]), (dialog, which) -> {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(IngredientWidgetConfigure.this);

                    IngredientWidgetProvider.updateAppWidget(
                            IngredientWidgetConfigure.this, appWidgetManager, mAppWidgetId, recipes.get(which).uid, recipeNames.get(which));

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
