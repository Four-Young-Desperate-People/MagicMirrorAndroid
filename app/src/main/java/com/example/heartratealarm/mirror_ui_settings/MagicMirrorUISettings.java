package com.example.heartratealarm.mirror_ui_settings;


import com.google.common.collect.BiMap;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

// TODO: Hi Dom: needs GSON
public class MagicMirrorUISettings {
    private static final BiMap<Module, String> moduleToString = EnumHashBiMap.create(Module.class);

    static {
        moduleToString.put(Module.COMPLIMENTS, "Compliments");
        moduleToString.put(Module.CLOCK, "Clock");
        moduleToString.put(Module.NEWS_FEED, "News Feed");
        moduleToString.put(Module.CURRENT_WEATHER, "Current Weather");
        moduleToString.put(Module.WEATHER_FORECAST, "Weather Forecast");
    }

    private static final BiMap<Module, String> moduleToGSONString = EnumHashBiMap.create(Module.class);

    static {
        moduleToGSONString.put(Module.COMPLIMENTS, "compliments");
        moduleToGSONString.put(Module.CLOCK, "clock");
        moduleToGSONString.put(Module.NEWS_FEED, "newsfeed");
        moduleToGSONString.put(Module.CURRENT_WEATHER, "currentweather");
        moduleToGSONString.put(Module.WEATHER_FORECAST, "weatherforecast");
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

    public ModuleMapping toModuleMapping() {
        ModuleMapping moduleMapping = new ModuleMapping();
        Queue<Position> unusedPositions = new LinkedList<>();
        for (Position position : Position.values()) {
            if (moduleToPosition.inverse().get(position) == null) {
                unusedPositions.add(position);
            }
        }
        for (Module module : Module.values()) {
            UIjson uiJson = new UIjson();
            Position position = moduleToPosition.get(module);
            if (position == null) {
                uiJson.visible = "false";
                uiJson.position = unusedPositions.poll().toString().toLowerCase();
            } else {
                uiJson.visible = "true";
                uiJson.position = moduleToPosition.get(module).toString().toLowerCase();
            }
            moduleMapping.map.put(moduleToGSONString.get(module), uiJson);
        }
        return moduleMapping;
    }

    public String toJson() {
        Gson gson = new Gson();
        Map<String, UIjson> map = this.toModuleMapping().map;
        return gson.toJson(map);
    }
}

