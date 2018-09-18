package com.example.hesham.baking.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Step;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StepActivity extends AppCompatActivity {

    private static final String STEP_ACTIVITY_ON_SAVE_STEPS = "step_activity_on_save_steps";
    private static final String STEP_ACTIVITY_ON_SAVE_STEP_INDEX = "step_activity_on_save_step_index";
    private static final String STEP_ACTIVITY_ON_SAVE_RECIPE_NAME = "step_activity_on_save_recipe_name";


    @BindView(R.id.playerView)
    PlayerView mPlayerView;
    @BindView(R.id.step_description_text_view)
    TextView mStepDescriptionTextView;
    @BindView(R.id.next_step_button)
    Button mNextStepButton;
    @BindView(R.id.previous_step_button)
    Button mPreviousStepButton;
    @BindView(R.id.step_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_layout)
    LinearLayout mNavigationLayout;

    private SimpleExoPlayer mPlayer;
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

            if (intent.hasExtra(RecipeDetailsActivity.STEPS_FOR_STEP_ACTIVITY) &&
                    intent.hasExtra(Intent.EXTRA_TEXT) &&
                    intent.hasExtra(RecipeDetailsActivity.STEP_INDEX)) {

                mRecipeName = intent.getStringExtra(Intent.EXTRA_TEXT);
                mSteps = intent.getParcelableArrayListExtra(RecipeDetailsActivity.STEPS_FOR_STEP_ACTIVITY);
                mStepIndex = intent.getIntExtra(RecipeDetailsActivity.STEP_INDEX, -1);
                if (mStepIndex != -1) {
                    startStep();
                } else {
                    closeActivity();
                }

            } else {
                closeActivity();
            }
        }
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

            mStepDescriptionTextView.setText(mStep.getDescription());
            mPlayerView.setVisibility(View.VISIBLE);

            if (!mStep.getVideoURL().equals("")) {
                initializePlayer(mStep.getVideoURL());
            } else if (!mStep.getThumbnailURL().equals("")) {
                initializePlayer(mStep.getThumbnailURL());
            } else {
                mPlayerView.setPlayer(null);
                mPlayer = null;
                mPlayerView.setVisibility(View.GONE);
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
                releasePlayer();
                mStep = mSteps.get(mStepIndex);
                startStep();
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
                releasePlayer();
                mStep = mSteps.get(mStepIndex);
                startStep();
            }
        });
    }

    private void initializePlayer(String mediaUrl) {
        mPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        mPlayerView.setPlayer(mPlayer);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, String.valueOf(R.string.app_name)));
        final ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrl));
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(false);

        mPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StepActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mStepDescriptionTextView.setVisibility(View.GONE);
                mToolbar.setVisibility(View.GONE);
                mNavigationLayout.setVisibility(View.GONE);
            }
        });

    }


    private void closeActivity() {
        finish();
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayerView.setPlayer(null);
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }



}
