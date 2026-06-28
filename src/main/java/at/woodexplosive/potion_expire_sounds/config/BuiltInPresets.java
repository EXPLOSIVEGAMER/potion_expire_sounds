package at.woodexplosive.potion_expire_sounds.config;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;

import java.util.LinkedHashMap;
import java.util.Map;

public class BuiltInPresets {

    public static final Map<String, ModConfig> ALL = new LinkedHashMap<>();

    static {
        ALL.put("Default", new ModConfig());
        ALL.put("PVP", pvp());
    }

    private static ModConfig pvp() {
        ModConfig c = new ModConfig();

        c.warningThreshold = 200;
        c.playWarningSound2 = false;
        c.pitchWarning = 0.5f;
        c.pitchExpire = 1.3f;
        c.listType = ModConfig.ListType.WHITELIST;
        c.effectMap = Map.of(
                "effect.minecraft.strength", true,
                "effect.minecraft.speed", true,
                "effect.minecraft.fire_resistance", true
        );

        return c;
    }
}
