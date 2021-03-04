package com.example.heartratealarm;


import java.util.HashSet;
import java.util.Set;

// TODO: needs GSON and underlying logic
class MagicMirrorUISettings {
    public static final int TOP_LEFT = 0;
    public static final int TOP_CENTER = 1;
    public static final int TOP_RIGHT = 2;
    public static final int MIDDLE_CENTER = 3;
    public static final int BOTTOM_LEFT = 4;
    public static final int BOTTOM_CENTER = 5;
    public static final int BOTTOM_RIGHT = 6;
    Module[] modules = new Module[7];

    public MagicMirrorUISettings() {
        modules[TOP_CENTER] = Module.COMPLIMENTS;
        modules[TOP_RIGHT]= Module.CLOCK;
        modules[MIDDLE_CENTER]= Module.NEWS_FEED;
        modules[BOTTOM_LEFT] = Module.CURRENT_WEATHER;
        modules[BOTTOM_RIGHT] = Module.WEATHER_FORECAST;
    }

    // check for duplicates
    boolean isValid() {
        Set<Module> set = new HashSet<>();
        for (Module module: modules){
            if (module == null){
                continue;
            }
            if (!set.add(module)){
                return false;
            }
        }
        return true;
    }

    public enum Module {
        COMPLIMENTS,
        CLOCK,
        CURRENT_WEATHER,
        WEATHER_FORECAST,
        NEWS_FEED
    }

}

