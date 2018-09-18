package com.example.hesham.baking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Ingredient;
import com.example.hesham.baking.ui.composer.IngredientsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientActivity extends AppCompatActivity {

    private static final String INGREDIENT_ACTIVITY_ON_SAVE_INGREDIENTS = "ingredient_activity_on_save_ingredients";
    private static final String INGREDIENT_ACTIVITY_ON_SAVE_RECIPE_NAME = "ingredient_activity_on_save_recipe_name";

    @BindView(R.id.ingredient_toolbar)
    Toolbar mToolbar;
    private String mRecipeName;
    private List<Ingredient> mIngredients;
    private RecyclerView.LayoutManager layoutManager;
    private IngredientsAdapter adapter;

    @BindView(R.id.ingredients_recycler_view)
    RecyclerView mIngredientsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(INGREDIENT_ACTIVITY_ON_SAVE_INGREDIENTS)
                && savedInstanceState.containsKey(INGREDIENT_ACTIVITY_ON_SAVE_RECIPE_NAME)) {
            mIngredients = savedInstanceState.getParcelableArrayList(INGREDIENT_ACTIVITY_ON_SAVE_INGREDIENTS);
            mRecipeName = savedInstanceState.getString(INGREDIENT_ACTIVITY_ON_SAVE_RECIPE_NAME);
            setupIngredients();
        } else {

            Intent intent = getIntent();
            if (intent == null) {
                closeActivity();
            }

            if (intent.hasExtra(RecipeDetailsActivity.INGREDIENT_FOR_INGREDIENT_ACTIVITY) &&
                    intent.hasExtra(Intent.EXTRA_TEXT)) {

                mRecipeName = intent.getStringExtra(Intent.EXTRA_TEXT);
                mIngredients = intent.getParcelableArrayListExtra(RecipeDetailsActivity.INGREDIENT_FOR_INGREDIENT_ACTIVITY);

                if (mIngredients.size() == 0 || mIngredients == null) {
                    Toast.makeText(IngredientActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    closeActivity();
                } else {
                    setupIngredients();
                }
            } else {
                closeActivity();
            }
        }
    }

    private void setupIngredients() {
        getSupportActionBar().setTitle(mRecipeName);
        layoutManager = new LinearLayoutManager(this);
        mIngredientsRecyclerView.setLayoutManager(layoutManager);
        mIngredientsRecyclerView.setHasFixedSize(true);
        adapter = new IngredientsAdapter(mIngredients);
        mIngredientsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(INGREDIENT_ACTIVITY_ON_SAVE_INGREDIENTS, (ArrayList<? extends Parcelable>) mIngredients);
        outState.putString(INGREDIENT_ACTIVITY_ON_SAVE_RECIPE_NAME, mRecipeName);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    private void closeActivity() {
        finish();
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
}
