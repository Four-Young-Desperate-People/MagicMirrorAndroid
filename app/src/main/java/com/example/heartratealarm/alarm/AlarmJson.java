package com.example.heartratealarm.alarm;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class AlarmJson {
    static Map<Integer, String> map = new HashMap<>();

    static {
        map.put(0, "squats.gif");
        map.put(1, "jumping_jacks.gif");
    }

    @SerializedName("LED")
    LEDJson ledJson = new LEDJson();

    @SerializedName("ExerciseToDo")
    String exercise;

    // TODO: ACTUALLY PICK A HEARTRATE
    @SerializedName("HRThreshold")
    int hrThresh = 150;

    public AlarmJson(int exercise, int w) {
        this.exercise = map.get(exercise);
        ledJson.w = w;
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
