package com.example.hesham.baking.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hesham.baking.data.local.AppDatabase;
import com.example.hesham.baking.data.local.AppExecutors;
import com.example.hesham.baking.data.local.RecipeViewModel;
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

    public static final String STEP_FOR_DETAILS_ACTIVITY = "recipe_for_details_activity";
    public static final String INGREDIENT_FOR_DETAILS_ACTIVITY = "ingredient_for_details_activity";

    @BindView(R.id.recipes_recycler_view)
    RecyclerView recipesRecyclerView;
    @BindView(R.id.text_message)
    TextView textMessage;

    private List<Recipe> mRecipes;
    private GridLayoutManager layoutManager;
    private RecipesAdapter adapter;

    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = AppDatabase.getInstance(getApplicationContext());
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        showLoadMessage();
        readRecipes();


        EspressoIdlingResource.increment();

        int noOfColumns = calculateNoOfColumns(this);
        layoutManager = new GridLayoutManager(this, noOfColumns);
        recipesRecyclerView.setHasFixedSize(true);
        recipesRecyclerView.setLayoutManager(layoutManager);


    }


    private void readRecipes() {
        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mRecipes = recipes;
                if (mRecipes == null) {
                    downloadData();
                } else {
                    setupView();
                }
            }
        });
    }

    private void setupView() {
        showRecipesData();
        adapter = new RecipesAdapter(mRecipes, this);
        recipesRecyclerView.setAdapter(adapter);
    }

    private void downloadData() {
        final Call<List<Recipe>> recipeCall = NetworkUtils.getService().getRecipes();
        recipeCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                showRecipesData();
                final List<Recipe> recipes = response.body();
                AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (Recipe recipe : recipes) {
                            mDb.recipeDao().insertRecipe(recipe);
                            Log.e(TAG, "Done");
                        }
                    }
                });
                readRecipes();
                EspressoIdlingResource.decrement();
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                String message = "Internet Connection Error";
                showErrorMessage(message);
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    private void showErrorMessage(String message) {
        textMessage.setVisibility(View.VISIBLE);
        textMessage.setText(message);
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
