package com.example.hesham.baking.ui.composer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

public class StepFragment extends Fragment implements PlaybackPreparer {

    private static final String ON_SAVE_STEP = "on_save_step";
    //---------------------------
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "key_track_selector_parameters";
    private static final String KEY_AUTO_PLAY = "key_auto_play";
    private static final String KEY_WINDOW = "key_window";
    private static final String KEY_POSITION = "key_position";

    //---------------------------
    private Step mStep;
    private PlayerView playerView;
    private SimpleExoPlayer mPlayer;

    //--------------------------
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;
    private DefaultTrackSelector trackSelector;
    //--------------------------

    public StepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mStep = savedInstanceState.getParcelable(ON_SAVE_STEP);
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }

        View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        playerView = rootView.findViewById(R.id.playerView);
        playerView.setVisibility(View.VISIBLE);


        if (!mStep.getVideoURL().equals("")) {
            initializePlayer(mStep.getVideoURL());
        } else if (!mStep.getThumbnailURL().equals("")) {
            initializePlayer(mStep.getThumbnailURL());
        } else {
            playerView.setPlayer(null);
            mPlayer = null;
            playerView.setVisibility(View.GONE);
        }

        TextView descriptionTextView = rootView.findViewById(R.id.step_description_text_view);
        descriptionTextView.setText(mStep.getDescription());


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ON_SAVE_STEP, mStep);

        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (mPlayer != null) {
            startAutoPlay = mPlayer.getPlayWhenReady();
            startWindow = mPlayer.getCurrentWindowIndex();
            startPosition = Math.max(0, mPlayer.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }


    public void setStep(Step step) {
        mStep = step;
    }

    private void initializePlayer(String mediaUrl) {
        if (mPlayer == null) {
            trackSelector = new DefaultTrackSelector();
            trackSelector.setParameters(trackSelectorParameters);
            mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            mPlayer.setPlayWhenReady(startAutoPlay);
            mPlayer.addAnalyticsListener(new EventLogger(trackSelector));
            playerView.setPlayer(mPlayer);
            playerView.setPlaybackPreparer(this);
        }

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), String.valueOf(R.string.app_name)));
        final ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrl));
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(false);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            updateTrackSelectorParameters();
            updateStartPosition();

            mPlayer.release();
            mPlayer = null;
            playerView.setPlayer(null);

            trackSelector = null;

        }
    }

    @Override
    public void preparePlayback() {
        if (!mStep.getVideoURL().equals("")) {
            initializePlayer(mStep.getVideoURL());
        } else if (!mStep.getThumbnailURL().equals("")) {
            initializePlayer(mStep.getThumbnailURL());
        }

    }
}
