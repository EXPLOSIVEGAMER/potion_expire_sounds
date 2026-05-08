package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.config.ModConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;

@Environment(EnvType.CLIENT)
public class PotionHud implements HudElement {

    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !ModConfig.INSTANCE.displayPotionHud) return;

        int x = ModConfig.INSTANCE.potionHudX;
        int y = ModConfig.INSTANCE.potionHudY;

        boolean isAtBottom = y > client.getWindow().getScaledHeight() / 2;

        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            String text = effect.getEffectType().value().getName().getString()
                    + " " + effect.getDuration() / 20 + "s";

            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(effect.getEffectType()), x - 20, y - 4, 18, 18);

            drawContext.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, true);
            y = isAtBottom ? y - 18 : y + 18;
        }
    }
}
