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

    private static final String[] ROMAN_NUMERALS = {"", " II", " III", " IV", " V", " VI", " VII", " IIX", " IX", " X"};

    private final List<StatusEffectInstance> cachedEffects = new ArrayList<>();
    private final List<StatusEffectInstance> cachedLowestEffects = new ArrayList<>();
    private int lastPlayerAge = -1;
    private int cachedEffectSize = 0;

    public void render(@NotNull DrawContext drawContext, @NotNull RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !ModConfig.INSTANCE.displayPotionHud) return;

        int currentPlayerAge = client.player.age;
        if (this.lastPlayerAge != currentPlayerAge) {
            this.lastPlayerAge = currentPlayerAge;
            this.updateEffectCache(client);
        }

        if (this.cachedEffects.isEmpty()) return;

        int startX = (int) (ModConfig.INSTANCE.potionHudX * client.getWindow().getScaledWidth());
        int currentY = getCurrentY(client);

        if (ModConfig.INSTANCE.compactHud) {
            if (ModConfig.INSTANCE.potionHudItemSize == 1) {
                for (StatusEffectInstance cachedLowestEffect : this.cachedLowestEffects) {
                    renderEffect(drawContext, client, cachedLowestEffect, startX, currentY);
                    currentY += 20;
                }

                int remaining = this.cachedEffectSize - this.cachedLowestEffects.size();
                if (remaining > 0) {
                    Text tooltip = Text.translatable("hud." + PotionExpireSounds.MOD_ID + ".potion_hud.compact_hud.tooltip", remaining);
                    drawContext.drawText(client.textRenderer, tooltip, startX + 26, currentY + 4, 0xFFFFFFFF, true);
                }
            } else {
                int toRender = Math.min(ModConfig.INSTANCE.potionHudItemSize, this.cachedEffects.size());
                for (int i = 0; i < toRender; i++) {
                    renderEffect(drawContext, client, this.cachedEffects.get(i), startX, currentY);
                    currentY += 20;
                }

                int remaining = this.cachedEffectSize - ModConfig.INSTANCE.potionHudItemSize;
                if (remaining > 0) {
                    Text tooltip = Text.translatable("hud." + PotionExpireSounds.MOD_ID + ".potion_hud.compact_hud.tooltip", remaining);
                    drawContext.drawText(client.textRenderer, tooltip, startX + 26, currentY + 4, 0xFFFFFFFF, true);
                }
            }
        } else {
            for (StatusEffectInstance cachedEffect : this.cachedEffects) {
                renderEffect(drawContext, client, cachedEffect, startX, currentY);
                currentY += 20;
            }
        }
    }

    private int getCurrentY(MinecraftClient client) {
        int startY = (int) (ModConfig.INSTANCE.potionHudY * client.getWindow().getScaledHeight());

        int currentHudHeight = getCurrentHudHeight();

        boolean atBottom = ModConfig.INSTANCE.potionHudY > 0.5f;
        if (atBottom) {
            startY = startY - currentHudHeight + 40;
        }

        return startY + 2;
    }

    private int getCurrentHudHeight() {
        int linesToRender;
        if (ModConfig.INSTANCE.compactHud) {
            if (ModConfig.INSTANCE.potionHudItemSize == 1) {
                linesToRender = this.cachedLowestEffects.size() + ((this.cachedEffectSize - this.cachedLowestEffects.size() > 0) ? 1 : 0);
            } else {
                int displayed = Math.min(ModConfig.INSTANCE.potionHudItemSize, this.cachedEffects.size());
                linesToRender = displayed + ((this.cachedEffectSize - ModConfig.INSTANCE.potionHudItemSize > 0) ? 1 : 0);
            }
        } else {
            linesToRender = this.cachedEffects.size();
        }

        return linesToRender * 20;
    }

    private void renderEffect(DrawContext drawContext, MinecraftClient client, StatusEffectInstance effect, int startX, int currentY) {
        String duration = effect.getDuration() == -1 ? "∞" : TimeUtil.formatDuration(effect.getDuration());
        String text = String.format("%s:%s %s",ModConfig.INSTANCE.showText ? effect.getEffectType().value().getName().getString() : "", formatAmplifier(effect.getAmplifier()), duration);

        if (ModConfig.INSTANCE.showIcons) drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(effect.getEffectType()), startX + 4, currentY, 18, 18);
        drawContext.drawText(client.textRenderer, text, startX + 26, currentY + 4, ModConfig.INSTANCE.textColor, true);
    }

    private void updateEffectCache(MinecraftClient client) {
        this.cachedEffects.clear();
        this.cachedLowestEffects.clear();
        this.cachedEffectSize = 0;

        if (client.player == null) return;
        Collection<StatusEffectInstance> activeEffects = client.player.getStatusEffects();
        if (activeEffects.isEmpty()) return;

        boolean filterHud = ModConfig.FilterType.isFilterHud();
        boolean isWhitelist = ModConfig.INSTANCE.listType.equals(ModConfig.ListType.WHITELIST);
        int minDuration = Integer.MAX_VALUE;

        for (StatusEffectInstance e : activeEffects) {
            if (!e.isInfinite() || ModConfig.INSTANCE.showInfEffects) {
                this.cachedEffectSize++;
            }
            if (!ModConfig.INSTANCE.showInfEffects && e.isInfinite()) continue;
            if (filterHud) {
                boolean inMap = ModConfig.INSTANCE.effectMap.getOrDefault(e.getTranslationKey(), false);
                if (isWhitelist != inMap) continue;
            }
            this.cachedEffects.add(e);
            if (!e.isInfinite() && e.getDuration() < minDuration) {
                minDuration = e.getDuration();
            }
        }

        if (this.cachedEffects.isEmpty()) return;

        this.cachedEffects.sort((e1, e2) -> {
            int d1 = e1.isInfinite() ? Integer.MAX_VALUE : e1.getDuration();
            int d2 = e2.isInfinite() ? Integer.MAX_VALUE : e2.getDuration();
            return Integer.compare(d1, d2);
        });

        if (ModConfig.INSTANCE.compactHud && ModConfig.INSTANCE.potionHudItemSize == 1) {
            if (minDuration == Integer.MAX_VALUE) minDuration = 0;
            for (StatusEffectInstance e : this.cachedEffects) {
                if (!e.isInfinite() && Math.abs(e.getDuration() - minDuration) <= 20) {
                    this.cachedLowestEffects.add(e);
                }
            }
        }
    }

    private String formatAmplifier(int i) {
        if (i < 0) return "";
        if (i < ROMAN_NUMERALS.length) {
            return ROMAN_NUMERALS[i];
        }
        return " " + (i + 1);
    }
}