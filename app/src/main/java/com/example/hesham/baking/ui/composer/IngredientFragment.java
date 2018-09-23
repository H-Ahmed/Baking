package com.example.hesham.baking.ui.composer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Ingredient;
import com.example.hesham.baking.ui.IngredientActivity;
import com.example.hesham.baking.ui.RecipeDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class IngredientFragment extends Fragment {

    private static final String ON_SAVE_INGREDIENTS = "on_save_ingredients";

    private List<Ingredient> mIngredients;

    public IngredientFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.containsKey(ON_SAVE_INGREDIENTS)) {
            mIngredients = savedInstanceState.getParcelableArrayList(ON_SAVE_INGREDIENTS);
        }
        View rootView = inflater.inflate(R.layout.fragment_ingredient, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.ingredients_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        IngredientsAdapter adapter = new IngredientsAdapter(mIngredients);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ON_SAVE_INGREDIENTS, (ArrayList<? extends Parcelable>) mIngredients);
    }
}
