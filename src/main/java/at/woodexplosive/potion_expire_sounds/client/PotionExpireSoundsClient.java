package at.woodexplosive.potion_expire_sounds.client;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.config.ModConfigScreen;
import at.woodexplosive.potion_expire_sounds.effect.PotionExpires;
import at.woodexplosive.potion_expire_sounds.screen.PotionHud;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;


import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class PotionExpireSoundsClient implements ClientModInitializer {

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(PotionExpireSounds.id("category"));

    public KeyMapping openConfig;
    public static boolean isModMenuEnabled;

    @Override
    public void onInitializeClient() {

        rKeyBindings();

        isModMenuEnabled = FabricLoader.getInstance().isModLoaded("modmenu");

        ModConfig.load();

        ClientTickEvents.START_CLIENT_TICK.register(new PotionExpires());

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(MOD_ID, "potion_hud"),
                new PotionHud()
        );

        ClientTickEvents.END_CLIENT_TICK.register(this::openConfig);
    }

    private void openConfig(Minecraft client) {
        if (client.player == null || !openConfig.consumeClick()) return;

        client.setScreen(ModConfigScreen.createScreen(client.screen));

    }

    private void rKeyBindings() {
        openConfig = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key." + MOD_ID + ".open_config",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_UNKNOWN,
                        CATEGORY
                )
        );
    }
}
