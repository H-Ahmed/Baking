package com.example.hesham.baking.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.local.RecipeViewModel;
import com.example.hesham.baking.data.model.Recipe;
import com.example.hesham.baking.data.service.BakingWidgetService;

import java.util.ArrayList;
import java.util.List;

public class BakingAppWidgetConfig extends AppCompatActivity {

    public static final String SHARE_PREF_KEY = "share_pref_key";
    public static final String RECIPE_INDEX_KEY = "recipe_index_key";

    private List<Recipe> mRecipes;
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baking_app_widget_config);

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        sp = getSharedPreferences(SHARE_PREF_KEY, Activity.MODE_PRIVATE);
        editor = sp.edit();

        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mRecipes = recipes;
                if (mRecipes == null) {
                    // Do nothing
                } else {
                    widgetSetupView();
                }
            }
        });


    }

    private void widgetSetupView() {
        Spinner spinner = findViewById(R.id.widget_spinner);
        List<String> recipeNames = new ArrayList<>();

        for (Recipe recipe : mRecipes) {
            recipeNames.add(recipe.getName());
            getResources().getStringArray(R.array.recipe_names);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                editor.putInt(RECIPE_INDEX_KEY, position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                editor.putInt(RECIPE_INDEX_KEY, 1);
                editor.commit();
            }
        });
    }

    public void confirmConfiguration(View view) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);


        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.baking_widget);

        Intent serviceIntent = new Intent(this, BakingWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        int recipeIndex = sp.getInt(RECIPE_INDEX_KEY, -1);
        views.setTextViewText(R.id.widget_baking_title, mRecipes.get(recipeIndex).getName());
        views.setRemoteAdapter(appWidgetId, R.id.widget_baking_list_view, serviceIntent);
        views.setEmptyView(R.id.widget_baking_list_view, R.id.widget_baking_empty_list);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_baking_list_view);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
