package com.example.hesham.baking.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hesham.baking.util.EspressoIdlingResource;
import com.example.hesham.baking.R;

import com.example.hesham.baking.data.model.Ingredient;
import com.example.hesham.baking.data.model.Recipe;
import com.example.hesham.baking.data.model.Step;
import com.example.hesham.baking.data.remote.NetworkUtils;
import com.example.hesham.baking.ui.composer.RecipesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.RecipesAdapterOnClickHandler {

    private static final String TAG = "MainActivity";
    private static final String MAIN_ACTIVITY_ON_SAVE_RECIPES = "main_activity_on_save";

    public static final String STEP_FOR_DETAILS_ACTIVITY = "recipe_for_details_activity";
    public static final String INGREDIENT_FOR_DETAILS_ACTIVITY = "ingredient_for_details_activity";

    @BindView(R.id.recipes_recycler_view)
    RecyclerView recipesRecyclerView;
    @BindView(R.id.text_message)
    TextView textMessage;

    private Recipe[] mRecipes;
    private GridLayoutManager layoutManager;
    private RecipesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        showLoadMessage();

        EspressoIdlingResource.increment();

        int noOfColumns = calculateNoOfColumns(this);
        layoutManager = new GridLayoutManager(this, noOfColumns);
        recipesRecyclerView.setHasFixedSize(true);
        recipesRecyclerView.setLayoutManager(layoutManager);


        if (savedInstanceState != null && savedInstanceState.containsKey(MAIN_ACTIVITY_ON_SAVE_RECIPES)) {
            showRecipesData();
            mRecipes = (Recipe[]) savedInstanceState.getParcelableArray(MAIN_ACTIVITY_ON_SAVE_RECIPES);
            adapter = new RecipesAdapter(mRecipes, MainActivity.this);
            recipesRecyclerView.setAdapter(adapter);
        } else {
            Call<Recipe[]> recipeCall = NetworkUtils.getService().getRecipes();
            recipeCall.enqueue(new Callback<Recipe[]>() {
                @Override
                public void onResponse(Call<Recipe[]> call, Response<Recipe[]> response) {
                    showRecipesData();
                    mRecipes = response.body();
                    adapter = new RecipesAdapter(mRecipes, MainActivity.this);
                    recipesRecyclerView.setAdapter(adapter);
                    EspressoIdlingResource.decrement();
                }

                @Override
                public void onFailure(Call<Recipe[]> call, Throwable t) {
                    showErrorMessage();
                    Log.d(TAG, "onFailure: " + t.toString());
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MAIN_ACTIVITY_ON_SAVE_RECIPES, mRecipes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 400;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 1) {
            noOfColumns = 1;
        }
        return noOfColumns;
    }

    private void showRecipesData() {
        textMessage.setVisibility(View.GONE);
        recipesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoadMessage() {
        textMessage.setVisibility(View.VISIBLE);
        textMessage.setText("Loading....");
        recipesRecyclerView.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        textMessage.setVisibility(View.VISIBLE);
        textMessage.setText("Error");
        recipesRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(String recipeName, List<Step> steps, List<Ingredient> ingredients) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putParcelableArrayListExtra(STEP_FOR_DETAILS_ACTIVITY, (ArrayList<? extends Parcelable>) steps);
        intent.putParcelableArrayListExtra(INGREDIENT_FOR_DETAILS_ACTIVITY, (ArrayList<? extends Parcelable>) ingredients);
        intent.putExtra(Intent.EXTRA_TEXT, recipeName);
        startActivity(intent);
    }
}
