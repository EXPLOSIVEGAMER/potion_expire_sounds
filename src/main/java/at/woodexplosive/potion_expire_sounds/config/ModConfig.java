package at.woodexplosive.potion_expire_sounds.config;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.sound.ModSounds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
            .getConfigDir().resolve(MOD_ID+".json");

    public boolean playExpireSound = true;
    public boolean playWarningSound = true;
    public boolean playWarningSound2 = true;

    public int warningThreshold = 200;
    public float volumeExpire = 1.0f;
    public float volumeWarning = 1.0f;
    public float pitchExpire = 1.0f;
    public float pitchWarning = 1.0f;

    // Filter
    public FilterType filterType = FilterType.BOTH;
    public ListType listType = ListType.BLACKLIST;
    public Map<String, Boolean> effectMap = new HashMap<>();

    public Identifier soundPotionExpire = ModSounds.POTION_EXPIRE.id();
    public Identifier soundPotionWarning = ModSounds.POTION_WARNING.id();

    // Potion Hud
    public boolean displayPotionHud = true;
    public boolean compactHud = false;
    public boolean showInfEffects = true;
    public int potionHudItemSize = 1;
    public float potionHudX = 0.550F;
    public float potionHudY = 0.440F;

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

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = new Gson().fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                INSTANCE = new ModConfig();
            }
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(INSTANCE, writer);
        } catch (IOException e) {
            PotionExpireSounds.LOGGER.error("Error while saving Config!", e);
        }
    }
}