package com.example.hesham.baking.ui.composer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Ingredient;


import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsAdapterViewHolder> {

    private List<Ingredient> mIngredients;

    public IngredientsAdapter(List<Ingredient> ingredients) {
        mIngredients = ingredients;
    }


    @NonNull
    @Override
    public IngredientsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_rv_item, parent, false);
        return new IngredientsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapterViewHolder holder, int position) {

        holder.mQuantity.setText(String.valueOf(mIngredients.get(position).getQuantity()));
        holder.mMeasure.setText(mIngredients.get(position).getMeasure());
        holder.mIngredient.setText(mIngredients.get(position).getIngredient());
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    public class IngredientsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mQuantity;
        TextView mMeasure;
        TextView mIngredient;

        public IngredientsAdapterViewHolder(View itemView) {
            super(itemView);

            mQuantity = (TextView) itemView.findViewById(R.id.quantity_value);
            mMeasure = (TextView) itemView.findViewById(R.id.measure_value);
            mIngredient = (TextView) itemView.findViewById(R.id.ingredient_value);
        }

    }
}
