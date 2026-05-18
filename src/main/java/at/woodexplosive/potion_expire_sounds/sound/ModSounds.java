package at.woodexplosive.potion_expire_sounds.sound;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;

public class ModSounds {

    public static final RegistryKey<SoundEvent> POTION_WARNING_KEY;
    public static final SoundEvent POTION_WARNING;
    public static final RegistryKey<SoundEvent> POTION_EXPIRE_KEY;
    public static final SoundEvent POTION_EXPIRE;

    public static void registerModSounds() {

        Registry.register(Registries.SOUND_EVENT, POTION_EXPIRE_KEY, POTION_EXPIRE);
        Registry.register(Registries.SOUND_EVENT, POTION_WARNING_KEY, POTION_WARNING);

        PotionExpireSounds.LOGGER.info("Registered Mod Sounds!");
    }

    static {
        POTION_WARNING_KEY = RegistryKey.of(RegistryKeys.SOUND_EVENT, PotionExpireSounds.id("potion_warning"));
        POTION_WARNING = SoundEvent.of(POTION_WARNING_KEY.getValue());
        POTION_EXPIRE_KEY = RegistryKey.of(RegistryKeys.SOUND_EVENT, PotionExpireSounds.id("potion_expire"));
        POTION_EXPIRE = SoundEvent.of(POTION_EXPIRE_KEY.getValue());
    }
}
