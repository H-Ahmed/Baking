package com.example.hesham.baking.ui.composer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Step;

import java.util.List;

public class RecipeDetailsAdapter extends RecyclerView.Adapter<RecipeDetailsAdapter.RecipeDetailsAdapterViewHolder> {
    private List<Step> mSteps;
    private final RecipeDetailsAdapterOnClickHandler mClickHandler;

    public interface RecipeDetailsAdapterOnClickHandler {
        void onClick(int stepIndex);
    }

    public RecipeDetailsAdapter(List<Step> steps, RecipeDetailsAdapterOnClickHandler clickHandler) {
        mSteps = steps;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public RecipeDetailsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_steps_rv_item, parent, false);

        return new RecipeDetailsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDetailsAdapterViewHolder holder, int position) {
        holder.stepButton.setText(mSteps.get(position).getShortDescription());
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    public class RecipeDetailsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button stepButton;

        public RecipeDetailsAdapterViewHolder(View itemView) {
            super(itemView);
            stepButton = (Button) itemView.findViewById(R.id.step_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }
}
