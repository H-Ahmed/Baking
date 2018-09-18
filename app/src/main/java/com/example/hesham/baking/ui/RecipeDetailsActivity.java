package com.example.hesham.baking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Ingredient;
import com.example.hesham.baking.data.model.Step;
import com.example.hesham.baking.ui.composer.RecipeDetailsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsAdapter.RecipeDetailsAdapterOnClickHandler {

    public final static String STEPS_FOR_STEP_ACTIVITY = "steps_for_step_activity";
    public final static String INGREDIENT_FOR_INGREDIENT_ACTIVITY = "ingredient_for_ingredient_activity";
    private static final String TAG = "RecipeDetailsActivity";
    public static final String STEP_INDEX = "step_index";

    private static final String RECIPE_ACTIVITY_ON_SAVE_STEPS = "recipe_activity_on_save_steps";
    private static final String RECIPE_ACTIVITY_ON_SAVE_INGREDIENTS = "recipe_activity_on_save_ingredients";
    private static final String RECIPE_ACTIVITY_ON_SAVE_RECIPE_NAME = "recipe_activity_on_save_recipe_name";

    @BindView(R.id.ingredients_button)
    Button mIngredientsButton;
    @BindView(R.id.recipe_steps_recycler_view)
    RecyclerView mRecipeStepsRecyclerView;
    @BindView(R.id.recipe_details_toolbar)
    Toolbar mToolbar;

    private String mRecipeName;
    private List<Step> mSteps;
    private List<Ingredient> mIngredients;
    private RecyclerView.LayoutManager layoutManager;
    private RecipeDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_ACTIVITY_ON_SAVE_STEPS)
                && savedInstanceState.containsKey(RECIPE_ACTIVITY_ON_SAVE_INGREDIENTS)
                && savedInstanceState.containsKey(RECIPE_ACTIVITY_ON_SAVE_RECIPE_NAME)) {

            mRecipeName = savedInstanceState.getString(RECIPE_ACTIVITY_ON_SAVE_RECIPE_NAME);
            mSteps = savedInstanceState.getParcelableArrayList(RECIPE_ACTIVITY_ON_SAVE_STEPS);
            mIngredients = savedInstanceState.getParcelableArrayList(RECIPE_ACTIVITY_ON_SAVE_INGREDIENTS);
            setupPage();
        } else {


            Intent intent = getIntent();
            if (intent == null) {
                closeActivity();
            }

            if (intent.hasExtra(MainActivity.STEP_FOR_DETAILS_ACTIVITY) &&
                    intent.hasExtra(MainActivity.INGREDIENT_FOR_DETAILS_ACTIVITY) &&
                    intent.hasExtra(Intent.EXTRA_TEXT)) {
                mRecipeName = intent.getStringExtra(Intent.EXTRA_TEXT);
                mSteps = intent.getParcelableArrayListExtra(MainActivity.STEP_FOR_DETAILS_ACTIVITY);
                mIngredients = intent.getParcelableArrayListExtra(MainActivity.INGREDIENT_FOR_DETAILS_ACTIVITY);

                if (mSteps.size() == 0 || mSteps == null) {
                    Toast.makeText(RecipeDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    closeActivity();
                } else {
                    setupPage();
                }
            } else {
                closeActivity();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_ACTIVITY_ON_SAVE_STEPS, (ArrayList<? extends Parcelable>) mSteps);
        outState.putParcelableArrayList(RECIPE_ACTIVITY_ON_SAVE_INGREDIENTS, (ArrayList<? extends Parcelable>) mIngredients);
        outState.putString(RECIPE_ACTIVITY_ON_SAVE_RECIPE_NAME, mRecipeName);
    }

    private void setupPage() {
        getSupportActionBar().setTitle(mRecipeName);
        mIngredientsButton.setText("Ingredients");
        mIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeDetailsActivity.this, IngredientActivity.class);
                intent.putParcelableArrayListExtra(INGREDIENT_FOR_INGREDIENT_ACTIVITY, (ArrayList<? extends Parcelable>) mIngredients);
                intent.putExtra(Intent.EXTRA_TEXT, mRecipeName);
                startActivity(intent);
            }
        });

        layoutManager = new LinearLayoutManager(this);
        mRecipeStepsRecyclerView.setLayoutManager(layoutManager);
        mRecipeStepsRecyclerView.setHasFixedSize(true);
        adapter = new RecipeDetailsAdapter(mSteps, RecipeDetailsActivity.this);
        mRecipeStepsRecyclerView.setAdapter(adapter);

    }

    private void closeActivity() {
        finish();
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    @Override
    public void onClick(int stepInex) {
        Intent intent = new Intent(this, StepActivity.class);
        intent.putExtra(STEP_INDEX, stepInex);
        intent.putParcelableArrayListExtra(STEPS_FOR_STEP_ACTIVITY, (ArrayList<? extends Parcelable>) mSteps);
        intent.putExtra(Intent.EXTRA_TEXT, mRecipeName);
        startActivity(intent);
    }
}
