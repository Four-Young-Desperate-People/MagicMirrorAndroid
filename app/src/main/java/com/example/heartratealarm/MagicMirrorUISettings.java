package com.example.heartratealarm;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

// TODO: needs GSON and underlying logic
public class MagicMirrorUISettings {
    private static final BiMap<Module, String> moduleToString = HashBiMap.create();

    static {
        moduleToString.put(Module.COMPLIMENTS, "Compliments");
        moduleToString.put(Module.CLOCK, "Clock");
        moduleToString.put(Module.NEWS_FEED, "News Feed");
        moduleToString.put(Module.CURRENT_WEATHER, "Current Weather");
        moduleToString.put(Module.WEATHER_FORECAST, "Weather Forecast");
    }

    public BiMap<Module, Position> moduleToPosition = HashBiMap.create();

    public MagicMirrorUISettings() {
        moduleToPosition.put(Module.COMPLIMENTS, Position.TOP_LEFT);
        moduleToPosition.put(Module.CLOCK, Position.TOP_RIGHT);
        moduleToPosition.put(Module.NEWS_FEED, Position.MIDDLE_CENTER);
        moduleToPosition.put(Module.CURRENT_WEATHER, Position.BOTTOM_LEFT);
        moduleToPosition.put(Module.WEATHER_FORECAST, Position.BOTTOM_RIGHT);
    }

    static public String moduleToString(Module module) {
        if (module == null) {
            return "";
        }
        switch (module) {
            case CLOCK:
                return "Clock";
            case COMPLIMENTS:
                return "Compliments";
            case CURRENT_WEATHER:
                return "Current Weather";
            case NEWS_FEED:
                return "News Feed";
            case WEATHER_FORECAST:
                return "Weather Forecast";
        }
        return "";
    }

    public void edit(String module, Position position) {
        moduleToPosition.inverse().remove(position);
        Module mod = moduleToString.inverse().get(module);
        if (mod == null) {
            return;
        }
        moduleToPosition.put(mod, position);
    }

    public String getModule(Position position) {
        return moduleToString(moduleToPosition.inverse().get(position));
    }

    public enum Position {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        MIDDLE_CENTER,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT,
    }

    public enum Module {
        CLOCK,
        COMPLIMENTS,
        CURRENT_WEATHER,
        NEWS_FEED,
        WEATHER_FORECAST
    }

    @Override
    public String toString() {
        return moduleToPosition.toString();
    }
}
