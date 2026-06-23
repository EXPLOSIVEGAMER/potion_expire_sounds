package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.util.TimeUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Environment(EnvType.CLIENT)
public class PotionHud implements HudElement {

    private static final String[] ROMAN_NUMERALS = {"", " II", " III", " IV", " V", " VI", " VII", " IIX", " IX", " X"};

    private final List<MobEffectInstance> cachedEffects = new ArrayList<>();
    private final List<MobEffectInstance> cachedLowestEffects = new ArrayList<>();
    private int lastPlayerAge = -1;
    private int cachedEffectSize = 0;

    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !ModConfig.INSTANCE.displayPotionHud || !ModConfig.INSTANCE.enableMod) return;

        int currentPlayerAge = client.player.tickCount;
        if (this.lastPlayerAge != currentPlayerAge) {
            this.lastPlayerAge = currentPlayerAge;
            this.updateEffectCache(client);
        }

        if (this.cachedEffects.isEmpty()) return;

        float scale = ModConfig.INSTANCE.hudSize;
        int startX = (int) (ModConfig.INSTANCE.potionHudX * graphics.guiWidth());
        int startY = getStartY(graphics, scale);

        graphics.pose().pushMatrix();
        graphics.pose().translate(startX, startY);
        graphics.pose().scale(scale, scale);

        int currentY = 40;

        if (ModConfig.INSTANCE.compactHud) {
            if (ModConfig.INSTANCE.potionHudItemSize == 1) {
                for (MobEffectInstance cachedLowestEffect : this.cachedLowestEffects) {
                    renderEffect(graphics, client, cachedLowestEffect, currentY);
                    currentY += 20;
                }

                int remaining = this.cachedEffectSize - this.cachedLowestEffects.size();
                if (remaining > 0) {
                    Component tooltip = Component.translatable("hud." + PotionExpireSounds.MOD_ID + ".potion_hud.compact_hud.tooltip", remaining);
                    graphics.text(client.font, tooltip, 26, currentY + 4, 0xFFFFFFFF, true);
                }
            } else {
                int toRender = Math.min(ModConfig.INSTANCE.potionHudItemSize, this.cachedEffects.size());
                for (int i = 0; i < toRender; i++) {
                    renderEffect(graphics, client, this.cachedEffects.get(i), currentY);
                    currentY += 20;
                }

                int remaining = this.cachedEffectSize - ModConfig.INSTANCE.potionHudItemSize;
                if (remaining > 0) {
                    Component tooltip = Component.translatable("hud." + PotionExpireSounds.MOD_ID + ".potion_hud.compact_hud.tooltip", remaining);
                    graphics.text(client.font, tooltip, 26, currentY + 4, 0xFFFFFFFF, true);
                }
            }
        } else {
            for (MobEffectInstance cachedEffect : this.cachedEffects) {
                renderEffect(graphics, client, cachedEffect, currentY);
                currentY += 20;
            }
        }

        graphics.pose().popMatrix();
    }

    private int getStartY(GuiGraphicsExtractor graphics, float scale) {
        int anchorY = (int) (ModConfig.INSTANCE.potionHudY * graphics.guiHeight());
        boolean atBottom = ModConfig.INSTANCE.potionHudY > 0.5f;
        if (atBottom) {
            return anchorY - (int) (getCurrentHudHeight() * scale);
        }
        return anchorY + 2;
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

    private void renderEffect(GuiGraphicsExtractor graphics, Minecraft client, MobEffectInstance effect, int y) {
        String duration = effect.isInfiniteDuration() ? "∞" : TimeUtil.formatDuration(effect.getDuration());
        String text = String.format("%s:%s %s", ModConfig.INSTANCE.showText ? effect.getEffect().value().getDisplayName().getString() : "", formatAmplifier(effect.getAmplifier()), duration);

        if (ModConfig.INSTANCE.showIcons) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.getMobEffectSprite(effect.getEffect()), 4, y, 18, 18);
        graphics.text(client.font, Component.literal(text), 26, y + 4, ModConfig.INSTANCE.textColor, true);
    }

    private void updateEffectCache(Minecraft client) {
        this.cachedEffects.clear();
        this.cachedLowestEffects.clear();
        this.cachedEffectSize = 0;

        if (client.player == null) return;
        Collection<MobEffectInstance> activeEffects = client.player.getActiveEffects();
        if (activeEffects.isEmpty()) return;

        boolean filterHud = ModConfig.FilterType.isFilterHud();
        boolean isWhitelist = ModConfig.INSTANCE.listType.equals(ModConfig.ListType.WHITELIST);
        int minDuration = Integer.MAX_VALUE;

        for (MobEffectInstance e : activeEffects) {
            if (!e.isInfiniteDuration() || ModConfig.INSTANCE.showInfEffects) {
                this.cachedEffectSize++;
            }
            if (!ModConfig.INSTANCE.showInfEffects && e.isInfiniteDuration()) continue;
            if (filterHud) {
                boolean inMap = ModConfig.INSTANCE.effectMap.getOrDefault(e.getEffect().value().getDescriptionId(), false);
                if (isWhitelist != inMap) continue;
            }
            this.cachedEffects.add(e);
            if (!e.isInfiniteDuration() && e.getDuration() < minDuration) {
                minDuration = e.getDuration();
            }
        }

        if (this.cachedEffects.isEmpty()) return;

        this.cachedEffects.sort((e1, e2) -> {
            int d1 = e1.isInfiniteDuration() ? Integer.MAX_VALUE : e1.getDuration();
            int d2 = e2.isInfiniteDuration() ? Integer.MAX_VALUE : e2.getDuration();
            return Integer.compare(d1, d2);
        });

        if (ModConfig.INSTANCE.compactHud && ModConfig.INSTANCE.potionHudItemSize == 1) {
            if (minDuration == Integer.MAX_VALUE) minDuration = 0;
            for (MobEffectInstance e : this.cachedEffects) {
                if (!e.isInfiniteDuration() && Math.abs(e.getDuration() - minDuration) <= 20) {
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
