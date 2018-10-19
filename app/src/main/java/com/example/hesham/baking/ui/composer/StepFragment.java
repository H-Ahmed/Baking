package com.example.hesham.baking.ui.composer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


public class StepFragment extends Fragment {

    private static final String ON_SAVE_STEP = "on_save_step";
    public static final String VIDEO_URL = "video_url";
    private static final String THUMBNAIL_PLAYER_STATE = "thumbnail_player_state";
    private static final String VIDEO_PLAYER_STATE = "video_player_state";
    private static final String THUMBNAIL_PLAYER_POSITION = "thumbnail_player_position";
    private static final String VIDEO_PLAYER_POSITION = "video_player_position";

    private PlayerView mVideoPlayerView;
    private PlayerView mThumbnailPlayerView;

    private SimpleExoPlayer mVideoPlayer;
    private SimpleExoPlayer mThumbnailPlayer;

    private boolean videoPlayerState;
    private boolean thumbnailPlayerState;
    private Long videoPlayerPosition;
    private Long thumbnailPlayerPosition;

    private Step mStep;


    public StepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mStep = savedInstanceState.getParcelable(ON_SAVE_STEP);

            videoPlayerState = savedInstanceState.getBoolean(VIDEO_PLAYER_STATE);
            thumbnailPlayerState = savedInstanceState.getBoolean(THUMBNAIL_PLAYER_STATE);

            videoPlayerPosition = savedInstanceState.getLong(VIDEO_PLAYER_POSITION);
            thumbnailPlayerPosition = savedInstanceState.getLong(THUMBNAIL_PLAYER_POSITION);
        }

        View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        LinearLayout videosLayout = rootView.findViewById(R.id.videoLayout);
        mVideoPlayerView = rootView.findViewById(R.id.video_player_view);
        mThumbnailPlayerView = rootView.findViewById(R.id.thumbnail_player_view);

        TextView descriptionTextView = rootView.findViewById(R.id.step_description_text_view);

        if (mStep.getVideoURL().equals("") && mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.GONE);

        } else if (!mStep.getVideoURL().equals("") && mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.VISIBLE);
            mVideoPlayerView.setVisibility(View.VISIBLE);
            mThumbnailPlayerView.setVisibility(View.GONE);

        } else if (mStep.getVideoURL().equals("") && !mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.VISIBLE);
            mVideoPlayerView.setVisibility(View.GONE);
            mThumbnailPlayerView.setVisibility(View.VISIBLE);
        } else {
            videosLayout.setVisibility(View.VISIBLE);
            mVideoPlayerView.setVisibility(View.VISIBLE);
            mThumbnailPlayerView.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    private void initializePlayer() {
        mVideoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());
        mThumbnailPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());

        mVideoPlayer.setPlayWhenReady(videoPlayerState);
        mThumbnailPlayer.setPlayWhenReady(thumbnailPlayerState);


        if (mStep.getVideoURL().equals("") && mStep.getThumbnailURL().equals("")) {
            if (mVideoPlayer != null) {
                releaseVideo();
            }
            if (mThumbnailPlayer != null) {
                releaseThumbnail();
            }
        } else if (!mStep.getVideoURL().equals("") && mStep.getThumbnailURL().equals("")) {
            startPlayer(mVideoPlayer, mVideoPlayerView, mStep.getVideoURL());
            if (mThumbnailPlayer != null) {
                releaseThumbnail();
            }
        } else if (mStep.getVideoURL().equals("") && !mStep.getThumbnailURL().equals("")) {
            if (mVideoPlayer != null) {
                releaseVideo();
            }
            startPlayer(mThumbnailPlayer, mThumbnailPlayerView, mStep.getThumbnailURL());
        } else {
            if (mVideoPlayer != null) {
                releaseVideo();
            }
            if (mThumbnailPlayer != null) {
                releaseThumbnail();
            }
        }

        if (mVideoPlayer != null && videoPlayerPosition != null) {
            mVideoPlayer.seekTo(videoPlayerPosition);
        }
        if (mThumbnailPlayer != null && thumbnailPlayerPosition != null) {
            mThumbnailPlayer.seekTo(thumbnailPlayerPosition);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mThumbnailPlayer == null) {
            initializePlayer();
        }
        if (Util.SDK_INT <= 23 || mVideoPlayer == null) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releaseVideo();
            releaseThumbnail();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releaseVideo();
            releaseThumbnail();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ON_SAVE_STEP, mStep);
        outState.putBoolean(THUMBNAIL_PLAYER_STATE, mThumbnailPlayer.getPlayWhenReady());
        outState.putBoolean(VIDEO_PLAYER_STATE, mVideoPlayer.getPlayWhenReady());

        outState.putLong(THUMBNAIL_PLAYER_POSITION, mThumbnailPlayer.getCurrentPosition());
        outState.putLong(VIDEO_PLAYER_POSITION, mVideoPlayer.getCurrentPosition());
    }


    public void setStep(Step step) {
        mStep = step;
    }


    private void releaseVideo() {
        mVideoPlayer.release();
    }

    private void releaseThumbnail() {
        mThumbnailPlayer.release();
    }
}
