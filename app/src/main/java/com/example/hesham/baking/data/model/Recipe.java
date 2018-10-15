package com.example.hesham.baking.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.hesham.baking.data.local.IngredientTypeConverters;
import com.example.hesham.baking.data.local.StepTypeConverters;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "recipe")
public class Recipe implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private int mId;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String mName;

    @ColumnInfo(name = "ingredients")
    @SerializedName("ingredients")
    @TypeConverters(IngredientTypeConverters.class)
    private List<Ingredient> mIngredients = null;

    @ColumnInfo(name = "steps")
    @SerializedName("steps")
    @TypeConverters(StepTypeConverters.class)
    private List<Step> mSteps = null;

    @ColumnInfo(name = "servings")
    @SerializedName("servings")
    private int mServings;

    @ColumnInfo(name = "image")
    @SerializedName("image")
    private String mImage;

    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        mId = id;
        mName = name;
        mIngredients = ingredients;
        mSteps = steps;
        mServings = servings;
        mImage = image;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public List<Ingredient> getIngredients() {
        return mIngredients;
    }

    public List<Step> getSteps() {
        return mSteps;
    }

    public int getServings() {
        return mServings;
    }

    public String getImage() {
        return mImage;
    }

    protected Recipe(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mServings = in.readInt();
        mImage = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeInt(mServings);
        parcel.writeString(mImage);
    }
}







