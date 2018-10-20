package com.example.hesham.baking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Ingredient;
import com.example.hesham.baking.data.model.Step;
import com.example.hesham.baking.ui.composer.IngredientFragment;
import com.example.hesham.baking.ui.composer.RecipeFragment;
import com.example.hesham.baking.ui.composer.StepFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hesham.baking.ui.composer.RecipeFragment.INGREDIENT_FOR_INGREDIENT_ACTIVITY;
import static com.example.hesham.baking.ui.composer.RecipeFragment.STEPS_FOR_STEP_ACTIVITY;
import static com.example.hesham.baking.ui.composer.RecipeFragment.STEP_INDEX;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeFragment.OnButtonClickListener, RecipeFragment.OnRecyclerItemClickListener {

    private static final String RECIPE_ACTIVITY_ON_SAVE_STEPS = "recipe_activity_on_save_steps";
    private static final String RECIPE_ACTIVITY_ON_SAVE_INGREDIENTS = "recipe_activity_on_save_ingredients";
    private static final String RECIPE_ACTIVITY_ON_SAVE_RECIPE_NAME = "recipe_activity_on_save_recipe_name";
    private static final String STEP_FRAGMENT_KEY = "step_fragment_key";
    private static final String RECIPE_STEP_INDEX = "recipe_step_index";

    @BindView(R.id.recipe_details_toolbar)
    Toolbar mToolbar;

    private String mRecipeName;
    private List<Step> mSteps;
    private List<Ingredient> mIngredients;

    private int mStepIndex = -1;

    private boolean mTwoPane;
    private FragmentManager fragmentManager;
    private StepFragment stepFragment;
    private IngredientFragment ingredientFragment;
    private RecipeFragment recipeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();
        recipeFragment = new RecipeFragment();
        ingredientFragment = new IngredientFragment();
        stepFragment = new StepFragment();

        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_ACTIVITY_ON_SAVE_STEPS)
                && savedInstanceState.containsKey(RECIPE_ACTIVITY_ON_SAVE_INGREDIENTS)
                && savedInstanceState.containsKey(RECIPE_ACTIVITY_ON_SAVE_RECIPE_NAME)) {

            mRecipeName = savedInstanceState.getString(RECIPE_ACTIVITY_ON_SAVE_RECIPE_NAME);
            mSteps = savedInstanceState.getParcelableArrayList(RECIPE_ACTIVITY_ON_SAVE_STEPS);
            mIngredients = savedInstanceState.getParcelableArrayList(RECIPE_ACTIVITY_ON_SAVE_INGREDIENTS);
            mStepIndex = savedInstanceState.getInt(RECIPE_STEP_INDEX);
            if (mStepIndex != -1) {
                stepFragment = (StepFragment) getSupportFragmentManager().getFragment(savedInstanceState, STEP_FRAGMENT_KEY);
            }

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
                }
            } else {
                closeActivity();
            }
        }

        getSupportActionBar().setTitle(mRecipeName);



        if (findViewById(R.id.two_pane_layout) != null) {
            mTwoPane = true;

            recipeFragment.setIngredients(mIngredients);
            recipeFragment.setSteps(mSteps);
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_container, recipeFragment)
                    .commit();

            if (mStepIndex == -1) {
                ingredientFragment.setIngredients(mIngredients);
                fragmentManager.beginTransaction()
                        .replace(R.id.details_container, ingredientFragment)
                        .commit();
            } else {
                stepFragment.setStep(mSteps.get(mStepIndex));
                fragmentManager.beginTransaction()
                        .replace(R.id.details_container, stepFragment)
                        .commit();
            }

        } else {
            mTwoPane = false;
            recipeFragment.setIngredients(mIngredients);
            recipeFragment.setSteps(mSteps);
            recipeFragment.setRecipeName(mRecipeName);
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_container, recipeFragment)
                    .commit();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_ACTIVITY_ON_SAVE_STEPS, (ArrayList<? extends Parcelable>) mSteps);
        outState.putParcelableArrayList(RECIPE_ACTIVITY_ON_SAVE_INGREDIENTS, (ArrayList<? extends Parcelable>) mIngredients);
        outState.putString(RECIPE_ACTIVITY_ON_SAVE_RECIPE_NAME, mRecipeName);
        outState.putInt(RECIPE_STEP_INDEX, mStepIndex);
        if (stepFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, STEP_FRAGMENT_KEY, stepFragment);
        }
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
    public void onClickRecyclerItem(int position) {
        if (mTwoPane) {
            stepFragment = new StepFragment();
            mStepIndex = position;
            stepFragment.setStep(mSteps.get(mStepIndex));
            fragmentManager.beginTransaction()
                    .replace(R.id.details_container, stepFragment)
                    .commit();

        } else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(STEP_INDEX, position);
            intent.putParcelableArrayListExtra(STEPS_FOR_STEP_ACTIVITY, (ArrayList<? extends Parcelable>) mSteps);
            intent.putExtra(Intent.EXTRA_TEXT, mRecipeName);
            startActivity(intent);
        }
    }

    @Override
    public void onClickButton() {
        if (mTwoPane) {
            mStepIndex = -1;
            ingredientFragment = new IngredientFragment();
            ingredientFragment.setIngredients(mIngredients);
            fragmentManager.beginTransaction()
                    .replace(R.id.details_container, ingredientFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, IngredientActivity.class);
            intent.putParcelableArrayListExtra(INGREDIENT_FOR_INGREDIENT_ACTIVITY, (ArrayList<? extends Parcelable>) mIngredients);
            intent.putExtra(Intent.EXTRA_TEXT, mRecipeName);
            startActivity(intent);
        }
    }
}
