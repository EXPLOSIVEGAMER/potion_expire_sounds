package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.util.TimeUtil;
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

        List<StatusEffectInstance> allEffects = sortEffects(client.player.getStatusEffects().stream()
                .filter(e -> ModConfig.INSTANCE.showInfEffects || !e.isInfinite())
                .filter(e -> !ModConfig.FilterType.isFilterHud()
                        || ModConfig.INSTANCE.listType.equals(ModConfig.ListType.WHITELIST) == ModConfig.INSTANCE.effectMap.getOrDefault(e.getTranslationKey(), false))
                .toList());

        if (ModConfig.INSTANCE.compactHud) {

            int effectSize = client.player.getStatusEffects().stream()
                    .filter(e -> !e.isInfinite() || ModConfig.INSTANCE.showInfEffects)
                    .toList()
                    .size();

            int minDuration = client.player.getStatusEffects().stream()
                    .mapToInt(e -> e.isInfinite() ? Integer.MAX_VALUE : e.getDuration())
                    .min()
                    .orElse(0);

            List<StatusEffectInstance> lowestEffects = allEffects.stream()
                    .filter(e -> Math.abs(e.getDuration() - minDuration) <= 20 && !e.isInfinite())
                    .sorted(Comparator.comparingInt(e -> e.isInfinite() ? Integer.MAX_VALUE : e.getDuration()))
                    .toList();

            if (ModConfig.INSTANCE.potionHudItemSize == 1) {

                for (StatusEffectInstance effect : lowestEffects) {
                    renderEffect(drawContext, effect, x, y);
                    correctY();
                }

                if (effectSize - lowestEffects.size() > 0) {
                    drawContext.drawText(client.textRenderer, Text.translatable("hud." + PotionExpireSounds.MOD_ID + ".potion_hud.compact_hud.tooltip", effectSize - lowestEffects.size()), x, y, 0xFFFFFFFF, true);
                }

                return;
            }

            for (int i = 0; i < ModConfig.INSTANCE.potionHudItemSize && i < allEffects.size(); i++) {
                renderEffect(drawContext, allEffects.get(i), x, y);
                correctY();
            }

            if (effectSize - ModConfig.INSTANCE.potionHudItemSize > 0) {
                drawContext.drawText(client.textRenderer, Text.translatable("hud." + PotionExpireSounds.MOD_ID + ".potion_hud.compact_hud.tooltip", effectSize - ModConfig.INSTANCE.potionHudItemSize), x, y, 0xFFFFFFFF, true);
            }

        } else {

            for (StatusEffectInstance effect : allEffects) {

                renderEffect(drawContext, effect, x, y);
                correctY();

            }
        }
    }

    private void renderEffect(DrawContext drawContext, StatusEffectInstance effect, int x, int y) {
        String duration = effect.getDuration() == -1 ? "∞" : TimeUtil.formatDuration(effect.getDuration());

        String text = effect.getEffectType().value().getName().getString() + formatAmplifier(effect.getAmplifier())
                + ": " + duration;

        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(effect.getEffectType()), x - 20, y - 4, 18, 18);

        drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFFFF, true);
    }

    private boolean isAtBottom() {
        return ModConfig.INSTANCE.potionHudY > MinecraftClient.getInstance().getWindow().getScaledHeight() / 2;
    }

    private void correctY() {
        y = isAtBottom() ? y - 20 : y + 20;
    }

    private String formatAmplifier(int i) {
        if (i == 0) return "";

        String out;

        switch (i) {
            case 1 -> out = " II";
            case 2 -> out = " III";
            case 3 -> out = " IV";
            case 4 -> out = " V";
            case 5 -> out = " VI";
            case 6 -> out = " VII";
            case 7 -> out = " IIX";
            case 8 -> out = " IX";
            case 9 -> out = " X";

            default -> out = " " + (i + 1);
        }

        return out;
    }

    private List<StatusEffectInstance> sortEffects(Collection<StatusEffectInstance> effects) {
        return effects.stream()
                .sorted(Comparator.comparingInt(e -> e.isInfinite() ? Integer.MAX_VALUE : e.getDuration()))
                .toList();
    }
}
