package at.woodexplosive.potion_expire_sounds.effect;

import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class PotionExpires implements ClientTickEvents.StartTick {

    private final Map<RegistryEntry<StatusEffect>, Integer> lastDurations = new HashMap<>();

    @Override
    public void onStartTick(MinecraftClient client) {
        if (client.player == null) return;

        int warningThreshold = ModConfig.INSTANCE.warningThreshold;

        ClientPlayerEntity player = client.player;

        for (StatusEffectInstance effectInstance : player.getStatusEffects()) {

            boolean isInMap = ModConfig.INSTANCE.effectMap.getOrDefault(effectInstance.getTranslationKey(), false);
            boolean isWhiteList = ModConfig.INSTANCE.listType.equals(ModConfig.ListType.WHITELIST);

            if (isWhiteList != isInMap) continue;

            RegistryEntry<StatusEffect> type = effectInstance.getEffectType();
            int currentDuration = effectInstance.getDuration();
            int lastDuration = lastDurations.getOrDefault(type, 0);

            if (currentDuration <= warningThreshold && lastDuration > warningThreshold && ModConfig.INSTANCE.playWarningSound) {

                player.playSound(
                        getWarningSound(effectInstance),
                        ModConfig.INSTANCE.volume_warning,
                        ModConfig.INSTANCE.pitch_warning
                );

            } else if (currentDuration <= 20 && lastDuration > 20 && ModConfig.INSTANCE.playExpireSound) {

                player.playSound(
                        getExpireSound(effectInstance),
                        ModConfig.INSTANCE.volume_expire,
                        ModConfig.INSTANCE.pitch_expire
                );

            } else if (currentDuration <= 90 && lastDuration > 20 && ModConfig.INSTANCE.playWarningSound2) {

                if (client.isPaused()) return;

                if (currentDuration % 20 == 0) {
                    player.playSound(
                            getWarningSound(effectInstance),
                            ModConfig.INSTANCE.volume_warning,
                            ModConfig.INSTANCE.pitch_warning
                    );
                }

            }

            lastDurations.put(type, currentDuration);
        }

        lastDurations.keySet().removeIf(type ->
                player.getStatusEffect(type) == null);
    }

    private SoundEvent getExpireSound(StatusEffectInstance effect) {

        if (effect.getEffectType().equals(StatusEffects.STRENGTH) && ModConfig.INSTANCE.soundStrengthExpire != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundStrengthExpire);

        if (effect.getEffectType().equals(StatusEffects.SPEED) && ModConfig.INSTANCE.soundSpeedExpire != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundSpeedExpire);

        if (effect.getEffectType().equals(StatusEffects.FIRE_RESISTANCE) && ModConfig.INSTANCE.soundFireResExpire != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundFireResExpire);

        return SoundEvent.of(ModConfig.INSTANCE.soundPotionExpire);
    }

    private SoundEvent getWarningSound(StatusEffectInstance effect) {

        if (effect.getEffectType().equals(StatusEffects.STRENGTH) && ModConfig.INSTANCE.soundStrengthWarning != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundStrengthWarning);

        if (effect.getEffectType().equals(StatusEffects.SPEED) && ModConfig.INSTANCE.soundSpeedWarning != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundSpeedWarning);

        if (effect.getEffectType().equals(StatusEffects.FIRE_RESISTANCE) && ModConfig.INSTANCE.soundFireResWarning != null)
                return SoundEvent.of(ModConfig.INSTANCE.soundFireResWarning);

        return SoundEvent.of(ModConfig.INSTANCE.soundPotionWarning);
    }
}
