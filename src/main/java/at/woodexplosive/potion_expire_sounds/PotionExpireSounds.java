package at.woodexplosive.potion_expire_sounds;

import at.woodexplosive.potion_expire_sounds.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PotionExpireSounds implements ModInitializer {

    public static final String MOD_ID = "potion_expire_sounds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        ModSounds.registerModSounds();

    }

    public static Identifier id(String s) {
        return Identifier.of(MOD_ID, s);
    }
}
