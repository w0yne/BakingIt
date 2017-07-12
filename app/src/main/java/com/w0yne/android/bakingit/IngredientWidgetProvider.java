package com.w0yne.android.bakingit;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class IngredientWidgetProvider extends AppWidgetProvider {

    private int mRecipeId;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);
        mRecipeId = intent.getIntExtra(Intents.EXTRA_RECIPE_ID, -1);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        if (mRecipeId != -1) {
            for (int widgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId, mRecipeId, null);
            }
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int recipeId, String recipeName) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detail);

        Intent intent = new Intent(context, RecipeStepListActivity.class)
                .putExtra(Intents.EXTRA_RECIPE_ID, recipeId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
        if (recipeName != null) {
            views.setTextViewText(R.id.widget_title, recipeName);
        }

        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetRemoteViewsService.class)
                        .putExtra(Intents.EXTRA_RECIPE_ID, recipeId));

        views.setEmptyView(R.id.widget_list, R.id.widget_empty);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
