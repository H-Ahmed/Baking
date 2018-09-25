package com.example.hesham.baking.ui.composer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Step;

import com.example.hesham.baking.ui.PlayerActivity;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;


public class StepFragment extends Fragment {

    private static final String ON_SAVE_STEP = "on_save_step";
    public static final String VIDEO_URL = "video_url";
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
        LinearLayout videoURLLayout = rootView.findViewById(R.id.videoURL_layout);
        LinearLayout thumbnailURLLayout = rootView.findViewById(R.id.thumbnailURL_layout);


        ImageButton videoURLButton = rootView.findViewById(R.id.videoURL_button);
        ImageButton thumbnailButton = rootView.findViewById(R.id.thumbnailURL_button);

        TextView descriptionTextView = rootView.findViewById(R.id.step_description_text_view);

        if (mStep.getVideoURL().equals("") && mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.GONE);
        } else if (!mStep.getVideoURL().equals("") && mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.VISIBLE);
            thumbnailURLLayout.setVisibility(View.GONE);
            videoURLLayout.setVisibility(View.VISIBLE);
        } else if (mStep.getVideoURL().equals("") && !mStep.getThumbnailURL().equals("")) {
            videosLayout.setVisibility(View.VISIBLE);
            videoURLLayout.setVisibility(View.GONE);
            thumbnailURLLayout.setVisibility(View.VISIBLE);
        } else {
            videosLayout.setVisibility(View.VISIBLE);
            videoURLLayout.setVisibility(View.VISIBLE);
            thumbnailURLLayout.setVisibility(View.VISIBLE);
        }


        videoURLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra(VIDEO_URL, mStep.getVideoURL());
                startActivity(intent);
            }
        });

        thumbnailButton.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ON_SAVE_STEP, mStep);
    }


    public void setStep(Step step) {
        mStep = step;
    }
}
