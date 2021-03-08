package com.example.heartratealarm.mirror_ui_settings;


import com.example.heartratealarm.websocket.GenericData;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.Queue;

public class MagicMirrorUISettings {
    private static final BiMap<Module, String> moduleToString = EnumHashBiMap.create(Module.class);

    static {
        moduleToString.put(Module.COMPLIMENTS, "Compliments");
        moduleToString.put(Module.CLOCK, "Clock");
        moduleToString.put(Module.NEWS_FEED, "News Feed");
        moduleToString.put(Module.CURRENT_WEATHER, "Current Weather");
        moduleToString.put(Module.WEATHER_FORECAST, "Weather Forecast");
    }

    public BiMap<Module, Position> moduleToPosition = EnumBiMap.create(Module.class, Position.class);

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

    @Override
    public String toString() {
        return moduleToPosition.toString();
    }

    public String toJson() {
        JsonUI jsonUI = new JsonUI();
        Queue<Position> unusedPositions = new LinkedList<>();
        for (Position position : Position.values()) {
            if (moduleToPosition.inverse().get(position) == null) {
                unusedPositions.add(position);
            }
        }
        for (Module module : Module.values()) {
            Position position = moduleToPosition.get(module);
            jsonUI.moduleNames.addModule(module, position, unusedPositions);
        }
        GenericData gd = new GenericData("update_modules", jsonUI);
        Gson gson = new Gson();
        return gson.toJson(gd);
    }

    public void fromJson(String json) {
        Gson gson = new Gson();
        JsonUI jsonUI = gson.fromJson(json, JsonUI.class);
        moduleToPosition.clear();
        if (jsonUI.moduleNames.clock.visible.equals("true")) {
            moduleToPosition.put(Module.CLOCK, Position.valueOf(jsonUI.moduleNames.clock.position.toUpperCase()));
        }
        if (jsonUI.moduleNames.compliments.visible.equals("true")) {
            moduleToPosition.put(Module.COMPLIMENTS, Position.valueOf(jsonUI.moduleNames.compliments.position.toUpperCase()));
        }
        if (jsonUI.moduleNames.currentWeather.visible.equals("true")) {
            moduleToPosition.put(Module.CURRENT_WEATHER, Position.valueOf(jsonUI.moduleNames.currentWeather.position.toUpperCase()));
        }
        if (jsonUI.moduleNames.newsFeed.visible.equals("true")) {
            moduleToPosition.put(Module.NEWS_FEED, Position.valueOf(jsonUI.moduleNames.newsFeed.position.toUpperCase()));
        }
        if (jsonUI.moduleNames.weatherForecast.visible.equals("true")) {
            moduleToPosition.put(Module.WEATHER_FORECAST, Position.valueOf(jsonUI.moduleNames.weatherForecast.position.toUpperCase()));
        }
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


}

