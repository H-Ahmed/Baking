package com.example.hesham.baking.data.local;

import android.arch.persistence.room.TypeConverter;

import com.example.hesham.baking.data.model.Step;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class StepTypeConverters {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<Step> stringToStepList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Step>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String StepListToString(List<Step> someObjects) {
        return gson.toJson(someObjects);
    }
}
