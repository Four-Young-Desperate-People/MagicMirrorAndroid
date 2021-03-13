package com.example.heartratealarm.alarm;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AlarmJson {
    static Map<Integer, String> map = new HashMap<>();

    static {
        map.put(0, "squats.gif");
        map.put(1, "jumping_jacks.gif");
        map.put(2, "burpees.gif");
    }

    @SerializedName("LED")
    LEDJson ledJson = new LEDJson();

    @SerializedName("ExerciseToDo")
    String exercise;

    @SerializedName("HRThreshold")
    int hrThresh;

    public AlarmJson(int w, int hrThresh, boolean[] exercises) {
        List<Integer> validExercises = new ArrayList<>();
        for (int i = 0; i < exercises.length; i++) {
            if (exercises[i]) {
                validExercises.add(i);
            }
        }
        this.exercise = map.get(validExercises.get(new Random().nextInt(validExercises.size())));
        ledJson.w = w;
        this.hrThresh = hrThresh;
    }

}

class LEDJson {
    @SerializedName("r")
    int r = 0;

    @SerializedName("g")
    int g = 0;

    @SerializedName("b")
    int b = 0;

    @SerializedName("w")
    int w;
}
