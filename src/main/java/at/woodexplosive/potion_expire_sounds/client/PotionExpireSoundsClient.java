package at.woodexplosive.potion_expire_sounds.client;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.config.ModConfigScreen;
import at.woodexplosive.potion_expire_sounds.effect.PotionExpires;
import at.woodexplosive.potion_expire_sounds.screen.PotionHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class PotionExpireSoundsClient implements ClientModInitializer {

    private static final KeyBinding.Category CATEGORY = new KeyBinding.Category(PotionExpireSounds.id(""));

    public KeyBinding openConfig;
    public static boolean isModMenuEnabled;

    @Override
    public void onInitializeClient() {

        rKeyBindings();

        isModMenuEnabled = FabricLoader.getInstance().isModLoaded("modmenu");

        ModConfig.load();

        ClientTickEvents.START_CLIENT_TICK.register(new PotionExpires());

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.HOTBAR,
                PotionExpireSounds.id("potion_hud"),
                new PotionHud()
        );

        ClientTickEvents.END_CLIENT_TICK.register(this::openConfig);
    }

    private void openConfig(MinecraftClient client) {
        if (client.player == null || !openConfig.wasPressed()) return;

        client.setScreen(ModConfigScreen.createScreen(client.currentScreen));

    }

    private void rKeyBindings() {
        openConfig = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key." + MOD_ID + ".open_config",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_UNKNOWN,
                        CATEGORY
                )
        );
    }
}
