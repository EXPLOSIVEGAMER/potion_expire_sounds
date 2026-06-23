package at.woodexplosive.potion_expire_sounds.sound;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final ResourceKey<SoundEvent> POTION_WARNING_KEY;
    public static final SoundEvent POTION_WARNING;
    public static final ResourceKey<SoundEvent> POTION_EXPIRE_KEY;
    public static final SoundEvent POTION_EXPIRE;

    public static void registerModSounds() {

        Registry.register(BuiltInRegistries.SOUND_EVENT, POTION_EXPIRE_KEY, POTION_EXPIRE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, POTION_WARNING_KEY, POTION_WARNING);

        PotionExpireSounds.LOGGER.info("Registered Mod Sounds!");
    }

    static {
        POTION_WARNING_KEY = ResourceKey.create(Registries.SOUND_EVENT, PotionExpireSounds.id("potion_warning"));
        POTION_WARNING = SoundEvent.createVariableRangeEvent(POTION_WARNING_KEY.identifier());
        POTION_EXPIRE_KEY = ResourceKey.create(Registries.SOUND_EVENT, PotionExpireSounds.id("potion_expire"));
        POTION_EXPIRE = SoundEvent.createVariableRangeEvent(POTION_EXPIRE_KEY.identifier());
    }
}
