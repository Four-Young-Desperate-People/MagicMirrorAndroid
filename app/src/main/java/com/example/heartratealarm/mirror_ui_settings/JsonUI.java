package com.example.heartratealarm.mirror_ui_settings;

import com.google.gson.annotations.SerializedName;

import java.util.Queue;

public class JsonUI {
    @SerializedName("clock")
    ModuleValues clock;

    @SerializedName("compliments")
    ModuleValues compliments;

    @SerializedName("currentweather")
    ModuleValues currentWeather;

    @SerializedName("newsfeed")
    ModuleValues newsFeed;

    @SerializedName("weatherforecast")
    ModuleValues weatherForecast;

    public void addModule(MagicMirrorUISettings.Module module, MagicMirrorUISettings.Position position, Queue<MagicMirrorUISettings.Position> unusedPositions) {
        ModuleValues moduleValues = new ModuleValues();
        if (position == null) {
            moduleValues.visible = "false";
            moduleValues.position = unusedPositions.poll().toString().toLowerCase();
        } else {
            moduleValues.visible = "true";
            moduleValues.position = position.toString().toLowerCase();
        }
        switch (module) {
            case CLOCK:
                clock = moduleValues;
                break;
            case COMPLIMENTS:
                compliments = moduleValues;
                break;
            case CURRENT_WEATHER:
                currentWeather = moduleValues;
                break;
            case NEWS_FEED:
                newsFeed = moduleValues;
                break;
            case WEATHER_FORECAST:
                weatherForecast = moduleValues;
                break;
        }
    }
}


class ModuleValues {
    @SerializedName("visible")
    String visible;

    @SerializedName("position")
    String position;
}
