package at.woodexplosive.potion_expire_sounds.config;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class ModConfig {
    public enum ListType {
        BLACKLIST,
        WHITELIST
    }

    public enum FilterType {
        BOTH,
        SOUND,
        HUD;

        public static boolean isFilterHud() {
            return INSTANCE.filterType.equals(HUD)
                    || INSTANCE.filterType.equals(BOTH);
        }

        public static boolean isFilterSound() {
            return INSTANCE.filterType.equals(SOUND)
                    || INSTANCE.filterType.equals(BOTH);
        }
    }

    public static ModConfig INSTANCE = new ModConfig();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve(MOD_ID);

    public boolean enableMod = true;

    public boolean playExpireSound = true;
    public boolean playWarningSound = true;
    public boolean playWarningSound2 = true;

    public int warningThreshold = 600;
    public float volumeExpire = 1.0f;
    public float volumeWarning = 1.0f;
    public float pitchExpire = 1.0f;
    public float pitchWarning = 1.0f;

    // Filter
    public FilterType filterType = FilterType.BOTH;
    public ListType listType = ListType.BLACKLIST;
    public Map<String, Boolean> effectMap = new HashMap<>();

    public Identifier soundPotionExpire = SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE.id();
    public Identifier soundPotionWarning = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP.id();

    // Potion Hud
    public boolean displayPotionHud = false;
    public boolean compactHud = false;
    public boolean showInfEffects = true;
    public int potionHudItemSize = 1;
    public float potionHudX = 0.64f;
    public float potionHudY = 0.9f;
    public int textColor = 0xFFFFFFFF;
    public boolean showIcons = true;
    public boolean showText = true;
    public float hudSize = 1.0f;

    // Combat Mode
    public Identifier soundStrengthExpire = null;
    public Identifier soundStrengthWarning = null;
    public float soundStrengthExpireVolume = 1.0F;
    public float soundStrengthExpirePitch = 1.0F;
    public float soundStrengthWarningVolume = 1.0F;
    public float soundStrengthWarningPitch = 1.0F;

    public Identifier soundSpeedExpire = null;
    public Identifier soundSpeedWarning = null;
    public float soundSpeedExpireVolume = 1.0F;
    public float soundSpeedExpirePitch = 1.0F;
    public float soundSpeedWarningVolume = 1.0F;
    public float soundSpeedWarningPitch = 1.0F;

    public Identifier soundFireResExpire = null;
    public Identifier soundFireResWarning = null;
    public float soundFireResExpireVolume = 1.0F;
    public float soundFireResExpirePitch = 1.0F;
    public float soundFireResWarningVolume = 1.0F;
    public float soundFireResWarningPitch = 1.0F;

    // Effect Overrides (dynamic per-effect sound list)
    public List<EffectSoundOverride> effectSoundOverrides = new ArrayList<>();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH.resolve(MOD_ID+".json"))) {
                INSTANCE = new Gson().fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                loadDefault();
            }
        }
    }

    public static void loadDefault() {
        INSTANCE = new ModConfig();
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH);
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH.resolve(MOD_ID + ".json"))) {
                new GsonBuilder().setPrettyPrinting().create().toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            PotionExpireSounds.LOGGER.error("Error while saving Config!", e);
        }
    }

    public static void savePreset(String name) {
        try {
            var dir = CONFIG_PATH.resolve("presets");
            Files.createDirectories(dir);
            try (Writer writer = Files.newBufferedWriter(dir.resolve(name + ".json"))) {
                new GsonBuilder().setPrettyPrinting().create().toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            PotionExpireSounds.LOGGER.error("Error while saving Preset: {}", name, e);
        }
    }

    public static void loadPreset(String fileName) {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH.resolve("presets").resolve(fileName))) {
            INSTANCE = new Gson().fromJson(reader, ModConfig.class);
        } catch (IOException e) {
            PotionExpireSounds.LOGGER.error("Error while loading Preset: {}\nLoading default settings", fileName, e);
            INSTANCE = new ModConfig();
        }
    }

    public static ModConfig loadPresetAsConfig(String fileName) {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH.resolve("presets").resolve(fileName))) {
            ModConfig cfg = new Gson().fromJson(reader, ModConfig.class);
            return cfg != null ? cfg : new ModConfig();
        } catch (IOException e) {
            PotionExpireSounds.LOGGER.error("Error while loading Preset: {}", fileName, e);
            return null;
        }
    }

    public static java.io.File getPresetsDir() {
        return CONFIG_PATH.resolve("presets").toFile();
    }

    public static boolean importPreset(java.nio.file.Path source) {
        try {
            var dir = CONFIG_PATH.resolve("presets");
            Files.createDirectories(dir);
            Files.copy(source, dir.resolve(source.getFileName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            PotionExpireSounds.LOGGER.error("Error while importing preset: {}", source, e);
            return false;
        }
    }

    public static boolean deletePreset(String fileName) {
        try {
            return Files.deleteIfExists(CONFIG_PATH.resolve("presets").resolve(fileName));
        } catch (IOException e) {
            PotionExpireSounds.LOGGER.error("Error while deleting Preset: {}", fileName, e);
            return false;
        }
    }

    public static List<String> getPresets() {
        try {
            var presetsDir = CONFIG_PATH.resolve("presets/");
            if (!java.nio.file.Files.exists(presetsDir)) return List.of();
            try (var stream = Files.list(presetsDir)) {
                return stream
                        .filter(p -> p.toString().endsWith(".json"))
                        .map(p -> p.getFileName().toString())
                        .sorted()
                        .toList();
            }
        } catch (IOException e) {
            PotionExpireSounds.LOGGER.error("Error while listing presets", e);
            return List.of();
        }
    }
}