package com.example.hesham.baking.ui.composer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Step;

import com.example.hesham.baking.ui.PlayerActivity;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static android.support.constraint.Constraints.TAG;


public class StepFragment extends Fragment {

    private static final String ON_SAVE_STEP = "on_save_step";
    public static final String VIDEO_URL = "video_url";

    private PlayerView mVideoPlayerView;
    private PlayerView mThumbnailPlayerView;

    private SimpleExoPlayer mVideoPlayer;
    private SimpleExoPlayer mThumbnailPlayer;

    private Step mStep;


    public StepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mStep = savedInstanceState.getParcelable(ON_SAVE_STEP);
        }

        View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        LinearLayout videosLayout = rootView.findViewById(R.id.videoLayout);
        mVideoPlayerView = rootView.findViewById(R.id.video_player_view);
        mThumbnailPlayerView = rootView.findViewById(R.id.thumbnail_player_view);

        mVideoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());
        mThumbnailPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());

        TextView descriptionTextView = rootView.findViewById(R.id.step_description_text_view);

        if (mStep.getVideoURL().equals("") && mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.GONE);
            if (mVideoPlayer != null) {
                releaseVideo();
            }
            if (mThumbnailPlayer != null) {
                releaseThumbnail();
            }
        } else if (!mStep.getVideoURL().equals("") && mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.VISIBLE);
            mVideoPlayerView.setVisibility(View.VISIBLE);
            startPlayer(mVideoPlayer, mVideoPlayerView, mStep.getVideoURL());
            mThumbnailPlayerView.setVisibility(View.GONE);
            if (mThumbnailPlayer != null) {
                releaseThumbnail();
            }
        } else if (mStep.getVideoURL().equals("") && !mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.VISIBLE);
            mVideoPlayerView.setVisibility(View.GONE);
            if (mVideoPlayer != null) {
                releaseVideo();
            }
            mThumbnailPlayerView.setVisibility(View.VISIBLE);
            startPlayer(mThumbnailPlayer, mThumbnailPlayerView, mStep.getThumbnailURL());
        } else {
            videosLayout.setVisibility(View.VISIBLE);
            mVideoPlayerView.setVisibility(View.VISIBLE);
            mThumbnailPlayerView.setVisibility(View.VISIBLE);
            if (mVideoPlayer != null) {
                releaseVideo();
            }
            if (mThumbnailPlayer != null) {
                releaseThumbnail();
            }
        }


        mVideoPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra(VIDEO_URL, mStep.getVideoURL());
                startActivity(intent);
            }
        });

        mThumbnailPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra(VIDEO_URL, mStep.getThumbnailURL());
                startActivity(intent);
            }
        });

        descriptionTextView.setText(mStep.getDescription());
        return rootView;
    }

    private void startPlayer(SimpleExoPlayer simpleExoPlayer, PlayerView playerView, String videoURL) {

        playerView.setPlayer(simpleExoPlayer);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), String.valueOf(R.string.app_name)));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoURL));

        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ON_SAVE_STEP, mStep);
    }


    public void setStep(Step step) {
        mStep = step;
    }


    @Override
    public void onStop() {

        releaseVideo();
        releaseThumbnail();
        super.onStop();
    }

    private void releaseVideo() {
        mVideoPlayerView.setPlayer(null);
        mVideoPlayer = null;
        if (mVideoPlayer != null) {
            mVideoPlayer.setPlayWhenReady(false);
            mVideoPlayer.release();
        }
    }

    private void releaseThumbnail() {
        mThumbnailPlayerView.setPlayer(null);
        mThumbnailPlayer = null;
        if (mThumbnailPlayer != null) {
            mThumbnailPlayer.setPlayWhenReady(false);
            mThumbnailPlayer.release();
        }
    }
}
