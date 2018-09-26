package com.example.hesham.baking.ui.composer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.hesham.baking.R;
import com.example.hesham.baking.data.model.Ingredient;
import com.example.hesham.baking.data.model.Step;
import java.util.List;

public class RecipeFragment extends Fragment implements RecipeDetailsAdapter.RecipeDetailsAdapterOnClickHandler{

    public final static String INGREDIENT_FOR_INGREDIENT_ACTIVITY = "ingredient_for_ingredient_activity";
    public final static String STEPS_FOR_STEP_ACTIVITY = "steps_for_step_activity";

    public static final String STEP_INDEX = "step_index";

    private List<Ingredient> mIngredients;
    private List<Step> mSteps;
    private String mRecipeName;

    private RecyclerView mStepsRecyclerView;
    private Button mIngredientsButton;

    OnRecyclerItemClickListener mRecyclerCallback;
    OnButtonClickListener mButtonCallback;

    public interface OnRecyclerItemClickListener {
        void onClickRecyclerItem(int position);
    }

    public interface OnButtonClickListener {
        void onClickButton ();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mRecyclerCallback = (OnRecyclerItemClickListener) context;
            mButtonCallback = (OnButtonClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement OnClickListener");
        }
    }

    public RecipeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        mStepsRecyclerView = rootView.findViewById(R.id.recipe_steps_recycler_view);
        mIngredientsButton = rootView.findViewById(R.id.ingredients_button);
        mIngredientsButton.setText("Ingredients");

        mIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonCallback.onClickButton();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mStepsRecyclerView.setLayoutManager(layoutManager);
        mStepsRecyclerView.setHasFixedSize(true);
        RecipeDetailsAdapter adapter = new RecipeDetailsAdapter(mSteps, this);
        mStepsRecyclerView.setAdapter(adapter);


        return rootView;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    public void setSteps(List<Step> steps) {
        mSteps = steps;
    }

    public void setRecipeName(String recipeName) {
        mRecipeName = recipeName;
    }

    @Override
    public void onClick(int stepIndex) {
        mRecyclerCallback.onClickRecyclerItem(stepIndex);
    }
}
