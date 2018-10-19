package com.example.hesham.baking.data.service;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
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
        private LiveData<List<Recipe>> mLiveRecipes;
        private Context mContext;
        private List<Recipe> mRecipes = new ArrayList<>();
        private List<Ingredient> mIngredients = new ArrayList<>();
        private int recipeIndex;

        public BakingWidgetItemFactory(Context context, Intent intent) {
            this.mContext = context;
        }

        @Override
        public void onCreate() {
            mDb = AppDatabase.getInstance(getApplicationContext());
            mLiveRecipes = mDb.recipeDao().loadAllRecipes();

            mLiveRecipes.observeForever(new Observer<List<Recipe>>() {
                @Override
                public void onChanged(@Nullable List<Recipe> recipes) {
                    mRecipes = recipes;
                }
            });

            recipeIndex = getSharedPreferences(SHARE_PREF_KEY, Activity.MODE_PRIVATE)
                    .getInt(RECIPE_INDEX_KEY, 1);
            if (mRecipes.size() != 0) {
                mIngredients = mRecipes.get(recipeIndex).getIngredients();
            }

        }


        @Override
        public void onDataSetChanged() {
            recipeIndex = getSharedPreferences(SHARE_PREF_KEY, Activity.MODE_PRIVATE)
                    .getInt(RECIPE_INDEX_KEY, 1);
            if (mRecipes.size() != 0) {
                mIngredients = mRecipes.get(recipeIndex).getIngredients();
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mIngredients.size();
        }


        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget_item);
            views.setTextViewText(R.id.quantity_value, String.valueOf(mIngredients.get(i).getQuantity()));
            views.setTextViewText(R.id.measure_value, mIngredients.get(i).getMeasure());
            views.setTextViewText(R.id.ingredient_value, mIngredients.get(i).getIngredient());

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
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
