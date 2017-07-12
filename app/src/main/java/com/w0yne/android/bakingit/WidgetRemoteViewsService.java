package com.w0yne.android.bakingit;

import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.w0yne.android.bakingit.data.Ingredient;
import com.w0yne.android.bakingit.data.Recipe;

import io.realm.Realm;

public class WidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {

            private Recipe mRecipe;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                final long identityToken = Binder.clearCallingIdentity();
                int recipeId = intent.getIntExtra(Intents.EXTRA_RECIPE_ID, -1);
                Realm realm = Realm.getDefaultInstance();
                Recipe recipe = realm.where(Recipe.class).equalTo("uid", recipeId).findFirst();
                if (recipe != null) {
                    mRecipe = realm.copyFromRealm(recipe);
                }
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return mRecipe == null ? 0 : mRecipe.ingredientList.size();
            }

            @Override
            public RemoteViews getViewAt(final int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        mRecipe == null) {
                    return null;
                }

                Ingredient ingredient = mRecipe.ingredientList.get(position);
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_ingredient_list_item);
                remoteViews.setTextViewText(R.id.ingredient, ingredient.ingredient);
                remoteViews.setTextViewText(R.id.quantity, String.valueOf(ingredient.quantity));
                remoteViews.setTextViewText(R.id.measure, ingredient.measure);
                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_ingredient_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(final int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
