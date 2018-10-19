package com.example.hesham.baking;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.hesham.baking.data.service.BakingWidgetService;
import com.example.hesham.baking.ui.BakingAppWidgetConfig;
import com.example.hesham.baking.ui.MainActivity;


/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    static RemoteViews updateWidgetListView(Context context, int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        Intent imageIntent = new Intent(context, MainActivity.class);
        PendingIntent imagePendingIntent = PendingIntent.getActivity(context, 0, imageIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_baking_image, imagePendingIntent);

        Intent buttonIntent = new Intent(context, BakingAppWidgetConfig.class);
        buttonIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent buttonPendingIntent = PendingIntent.getActivity(context, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_baking_button, buttonPendingIntent);

        Intent serviceIntent = new Intent(context, BakingWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(appWidgetId, R.id.widget_baking_list_view, serviceIntent);
        views.setEmptyView(R.id.widget_baking_list_view, R.id.widget_baking_empty_list);


        // Instruct the widget manager to update the widget
        return views;

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = updateWidgetListView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_baking_list_view);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }


    @Override
    public void onEnabled(Context context) {
        Toast.makeText(context, "onEnabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context) {
        Toast.makeText(context, "onDisabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Toast.makeText(context, "onDeleted", Toast.LENGTH_SHORT).show();
    }
}

