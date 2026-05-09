package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Environment(EnvType.CLIENT)
public class PotionHud implements HudElement {

    private int x;
    private int y;

    public void render(@NotNull DrawContext drawContext, @NotNull RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !ModConfig.INSTANCE.displayPotionHud) return;

        x = ModConfig.INSTANCE.potionHudX;
        y = ModConfig.INSTANCE.potionHudY;

        if (ModConfig.INSTANCE.compactHud) {

            int effectSize = client.player.getStatusEffects().size();
            int minDuration = client.player.getStatusEffects().stream()
                    .mapToInt(StatusEffectInstance::getDuration)
                    .min()
                    .orElse(0);

            Collection<StatusEffectInstance> allEffects = client.player.getStatusEffects();

            List<StatusEffectInstance> lowestEffects = allEffects.stream()
                    .filter(e -> e.getDuration() - minDuration <= 20)
                    .toList();

            List<StatusEffectInstance> allEffectsSorted = allEffects.stream()
                    .sorted(Comparator.comparingInt(StatusEffectInstance::getDuration))
                    .toList();

            if (ModConfig.INSTANCE.potionHudItemSize == 1) {

                for (StatusEffectInstance effect : lowestEffects) {
                    renderEffect(drawContext, effect, x, y);
                    correctY();
                }

                if (effectSize -1 > 0) {
                    drawContext.drawText(client.textRenderer, Text.translatable("hud." + PotionExpireSounds.MOD_ID + ".potion_hud.compact_hud.tooltip", effectSize - 1), x, y, 0xFFFFFFFF, true);
                }

                return;
            }

            for (int i = 0; i < ModConfig.INSTANCE.potionHudItemSize && i < allEffectsSorted.size(); i++) {
                renderEffect(drawContext, allEffectsSorted.get(i), x, y);
                correctY();
            }

            if (effectSize - ModConfig.INSTANCE.potionHudItemSize > 0) {
                drawContext.drawText(client.textRenderer, Text.translatable("hud." + PotionExpireSounds.MOD_ID + ".potion_hud.compact_hud.tooltip", effectSize - ModConfig.INSTANCE.potionHudItemSize), x, y, 0xFFFFFFFF, true);
            }

        } else {

            for (StatusEffectInstance effect : client.player.getStatusEffects()) {
                renderEffect(drawContext, effect, x, y);
                correctY();
            }
        }
    }

    private void renderEffect(DrawContext drawContext, StatusEffectInstance effect, int x, int y) {
        String text = effect.getEffectType().value().getName().getString()
                + " " + effect.getDuration() / 20 + "s";

        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(effect.getEffectType()), x - 20, y - 4, 18, 18);

        drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFFFF, true);
    }

    private boolean isAtBottom() {
        return ModConfig.INSTANCE.potionHudY > MinecraftClient.getInstance().getWindow().getScaledHeight() / 2;
    }

    private void correctY() {
        y = isAtBottom() ? y - 20 : y + 20;
    }
}
