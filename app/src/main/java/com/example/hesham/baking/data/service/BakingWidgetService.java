package com.example.hesham.baking.data.service;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.local.AppDatabase;
import com.example.hesham.baking.data.model.Ingredient;
import com.example.hesham.baking.data.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import static com.example.hesham.baking.ui.BakingAppWidgetConfig.RECIPE_INDEX_KEY;
import static com.example.hesham.baking.ui.BakingAppWidgetConfig.SHARE_PREF_KEY;

public class BakingWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingWidgetItemFactory(getApplicationContext(), intent);
    }

    class BakingWidgetItemFactory implements RemoteViewsFactory {
        private AppDatabase mDb;
        private static final String TAG = "BakingWidgetItemFactory";
        private LiveData<List<Recipe>> mRecipes;
        private Context mContext;
        private List<Ingredient> ingredients = new ArrayList<>();
        private int recipeIndex;

        public BakingWidgetItemFactory(Context context, Intent intent) {
            this.mContext = context;
            recipeIndex = getSharedPreferences(SHARE_PREF_KEY, Activity.MODE_PRIVATE)
                    .getInt(RECIPE_INDEX_KEY, 1);
        }

        @Override
        public void onCreate() {

        }



        @Override
        public void onDataSetChanged() {
            mDb = AppDatabase.getInstance(getApplicationContext());
            mRecipes = mDb.recipeDao().loadAllRecipes();

            mRecipes.observeForever(new Observer<List<Recipe>>() {
                @Override
                public void onChanged(@Nullable List<Recipe> recipes) {
                    ingredients = recipes.get(recipeIndex).getIngredients();
                }
            });
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients != null ? ingredients.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget_item);
            views.setTextViewText(R.id.quantity_value, String.valueOf(ingredients.get(i).getQuantity()));
            Log.e(TAG, "getViewAt: " + String.valueOf(ingredients.get(i).getQuantity()));
            views.setTextViewText(R.id.measure_value, ingredients.get(i).getMeasure());
            Log.e(TAG, "getViewAt: " + ingredients.get(i).getMeasure());
            views.setTextViewText(R.id.ingredient_value, ingredients.get(i).getIngredient());
            Log.e(TAG, "getViewAt: " + String.valueOf(ingredients.get(i).getIngredient()));
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null ;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
