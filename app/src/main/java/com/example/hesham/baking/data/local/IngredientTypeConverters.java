package com.example.hesham.baking.data.local;

import android.arch.persistence.room.TypeConverter;

import com.example.hesham.baking.data.model.Ingredient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class IngredientTypeConverters {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<Ingredient> stringToIngredientList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Ingredient>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String IngredientListToString(List<Ingredient> someObjects) {
        return gson.toJson(someObjects);
    }


}
