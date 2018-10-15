package com.example.hesham.baking.data.local;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.hesham.baking.data.model.Recipe;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {
    private LiveData<List<Recipe>> recipes;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        recipes = database.recipeDao().loadAllRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
}
