package com.example.hesham.baking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Step;
import com.example.hesham.baking.ui.composer.StepFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


import static com.example.hesham.baking.ui.composer.RecipeFragment.STEPS_FOR_STEP_ACTIVITY;
import static com.example.hesham.baking.ui.composer.RecipeFragment.STEP_INDEX;

public class StepActivity extends AppCompatActivity {

    private static final String STEP_ACTIVITY_ON_SAVE_STEPS = "step_activity_on_save_steps";
    private static final String STEP_ACTIVITY_ON_SAVE_STEP_INDEX = "step_activity_on_save_step_index";
    private static final String STEP_ACTIVITY_ON_SAVE_RECIPE_NAME = "step_activity_on_save_recipe_name";

    private static final String TAG = "StepActivity";


    @BindView(R.id.next_step_button)
    Button mNextStepButton;
    @BindView(R.id.previous_step_button)
    Button mPreviousStepButton;
    @BindView(R.id.step_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_layout)
    LinearLayout mNavigationLayout;


    private String mRecipeName;
    private List<Step> mSteps;
    private int mStepIndex;
    private Step mStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null
                && savedInstanceState.containsKey(STEP_ACTIVITY_ON_SAVE_STEPS)
                && savedInstanceState.containsKey(STEP_ACTIVITY_ON_SAVE_STEP_INDEX)
                && savedInstanceState.containsKey(STEP_ACTIVITY_ON_SAVE_RECIPE_NAME)) {
            mSteps = savedInstanceState.getParcelableArrayList(STEP_ACTIVITY_ON_SAVE_STEPS);
            mStepIndex = savedInstanceState.getInt(STEP_ACTIVITY_ON_SAVE_STEP_INDEX);
            mRecipeName = savedInstanceState.getString(STEP_ACTIVITY_ON_SAVE_RECIPE_NAME);

            startStep();
        } else {


            Intent intent = getIntent();
            if (intent == null) {
                closeActivity();
                Toast.makeText(this, "First Close", Toast.LENGTH_SHORT).show();
            }

            if (intent.hasExtra(STEPS_FOR_STEP_ACTIVITY) &&
                    intent.hasExtra(Intent.EXTRA_TEXT) &&
                    intent.hasExtra(STEP_INDEX)) {

                mRecipeName = intent.getStringExtra(Intent.EXTRA_TEXT);
                mSteps = intent.getParcelableArrayListExtra(STEPS_FOR_STEP_ACTIVITY);
                mStepIndex = intent.getIntExtra(STEP_INDEX, -1);
                if (mStepIndex != -1) {
                    startStep();
                } else {
                    closeActivity();
                }

            } else {
                closeActivity();
            }
        }

        mNextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStepIndex >= mSteps.size() - 1) {
                    mStepIndex = 0;
                } else {
                    mStepIndex++;
                }
                mStep = mSteps.get(mStepIndex);
                startStep();
                Log.d(TAG, "onClick: Next");
            }
        });
        mPreviousStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStepIndex <= 0) {
                    mStepIndex = mSteps.size() - 1;
                } else {
                    mStepIndex--;
                }
                mStep = mSteps.get(mStepIndex);
                startStep();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STEP_ACTIVITY_ON_SAVE_STEPS, (ArrayList<? extends Parcelable>) mSteps);
        outState.putInt(STEP_ACTIVITY_ON_SAVE_STEP_INDEX, mStepIndex);
        outState.putString(STEP_ACTIVITY_ON_SAVE_RECIPE_NAME, mRecipeName);
    }

    private void startStep() {
        getSupportActionBar().setTitle(mRecipeName);
        mStep = mSteps.get(mStepIndex);
        if (mStep == null) {
            Toast.makeText(StepActivity.this, "No Steps", Toast.LENGTH_SHORT).show();
            closeActivity();
        } else {

            FragmentManager fragmentManager = getSupportFragmentManager();
            StepFragment stepFragment = new StepFragment();
            stepFragment.setStep(mStep);
            fragmentManager.beginTransaction()
                    .replace(R.id.step_container,stepFragment)
                    .commit();
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

}
