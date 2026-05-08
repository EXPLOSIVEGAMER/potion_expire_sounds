package at.woodexplosive.potion_expire_sounds.config;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.sound.ModSounds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class ModConfig {
    public enum ListType {
        BLACKLIST,
        WHITELIST
    }

    public static ModConfig INSTANCE = new ModConfig();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve(MOD_ID+".json");

    public boolean playExpireSound = true;
    public boolean playWarningSound = true;
    public boolean playWarningSound2 = true;
    public int warningThreshold = 200;
    public float volume_expire = 1.0f;
    public float volume_warning = 1.0f;
    public float pitch_expire = 1.0f;
    public float pitch_warning = 1.0f;
    public ListType listType = ListType.BLACKLIST;
    public List<String> effectList = new ArrayList<>();
    public Identifier soundPotionExpire = ModSounds.POTION_EXPIRE.id();
    public Identifier soundPotionWarning = ModSounds.POTION_WARNING.id();

    // Potion Hud
    public boolean displayPotionHud = true;
    public int potionHudX = 0;
    public int potionHudY = 0;

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