package com.example.hesham.baking.ui.composer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Ingredient;
import com.example.hesham.baking.data.model.Recipe;
import com.example.hesham.baking.data.model.Step;
import com.squareup.picasso.Picasso;

import java.util.List;


public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesAdapterViewHolder> {
    private List<Recipe> mRecipes;
    private final RecipesAdapterOnClickHandler mClickHandler;

    public interface RecipesAdapterOnClickHandler{
        void onClick(String recipeName, List<Step> steps, List<Ingredient> ingredients);
    }

    public RecipesAdapter(List<Recipe> recipes, RecipesAdapterOnClickHandler clickHandler) {
        mRecipes = recipes;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public RecipesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recepes_rv_item, parent, false);

        return new RecipesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapterViewHolder holder, int position) {
        if (mRecipes.get(position).getImage() == null || mRecipes.get(position).getImage().equals("")){
            holder.recipeImage.setVisibility(View.GONE);
        }else {
            Picasso.get()
                    .load(mRecipes.get(position).getImage())
                    .into(holder.recipeImage);
        }
        holder.recipeTitle.setText(mRecipes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public class RecipesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView recipeImage;
        TextView recipeTitle;

        public RecipesAdapterViewHolder(View itemView) {
            super(itemView);
            recipeImage = (ImageView) itemView.findViewById(R.id.recipe_image_view);
            recipeTitle = (TextView) itemView.findViewById(R.id.recipe_title_text_view);

            itemView.setOnClickListener(this);
        }

        public void onClick(View view){
            int adapterPosition = getAdapterPosition();
            List<Step> steps = mRecipes.get(adapterPosition).getSteps();
            List<Ingredient> ingredients = mRecipes.get(adapterPosition).getIngredients();
            mClickHandler.onClick(mRecipes.get(adapterPosition).getName(), steps, ingredients);
        }
    }
}
