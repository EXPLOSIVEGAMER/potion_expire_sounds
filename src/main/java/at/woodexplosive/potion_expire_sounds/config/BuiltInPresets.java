package at.woodexplosive.potion_expire_sounds.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class BuiltInPresets {

    public static final Map<String, ModConfig> ALL = new LinkedHashMap<>();

    static {
        ALL.put("Default", new ModConfig());
    }
}
