package at.woodexplosive.potion_expire_sounds.effect;

import at.woodexplosive.potion_expire_sounds.config.EffectSoundOverride;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.sound.ModSounds;
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
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class PotionExpires implements ClientTickEvents.StartTick {

    private final Map<RegistryEntry<StatusEffect>, Integer> lastDurations = new HashMap<>();

    private int lastWarningSoundTick = -100;
    private int lastExpireSoundTick = -100;

    @Override
    public void onStartTick(MinecraftClient client) {
        if (client.player == null || client.isPaused() || !ModConfig.INSTANCE.enableMod) return;

        int warningThreshold = ModConfig.INSTANCE.warningThreshold;
        ClientPlayerEntity player = client.player;
        int currentTick = player.age;

        int cooldownTicks = 20;

        if (currentTick < lastWarningSoundTick) lastWarningSoundTick = currentTick - cooldownTicks;
        if (currentTick < lastExpireSoundTick) lastExpireSoundTick = currentTick - cooldownTicks;

        for (StatusEffectInstance effectInstance : player.getStatusEffects()) {
            RegistryEntry<StatusEffect> type = effectInstance.getEffectType();
            int currentDuration = effectInstance.getDuration();

            int lastDuration = lastDurations.getOrDefault(type, currentDuration);
            lastDurations.put(type, currentDuration);

            boolean isInMap = ModConfig.INSTANCE.effectMap.getOrDefault(effectInstance.getTranslationKey(), false);
            boolean isWhiteList = ModConfig.INSTANCE.listType.equals(ModConfig.ListType.WHITELIST);
            if (isWhiteList != isInMap && ModConfig.FilterType.isFilterSound()) continue;

            if (currentDuration <= warningThreshold && lastDuration > warningThreshold && ModConfig.INSTANCE.playWarningSound) {
                if (currentTick - lastWarningSoundTick >= cooldownTicks) {
                    float[] volumePitch = getVolumeAndPitch(effectInstance, false);

                    player.playSound(
                            getWarningSound(effectInstance),
                            volumePitch[0],
                            volumePitch[1]
                    );
                    lastWarningSoundTick = currentTick;
                }
            }
            else if (currentDuration <= 20 && lastDuration > 20 && ModConfig.INSTANCE.playExpireSound) {
                if (currentTick - lastExpireSoundTick >= cooldownTicks) {
                    float[] volumePitch = getVolumeAndPitch(effectInstance, true);

                    player.playSound(
                            getExpireSound(effectInstance),
                            volumePitch[0],
                            volumePitch[1]
                    );
                    lastExpireSoundTick = currentTick;
                }
            }
            else if (currentDuration <= 90 && currentDuration > 20 && lastDuration > currentDuration && ModConfig.INSTANCE.playWarningSound2) {
                if (currentDuration % 20 == 0) {
                    if (currentTick - lastWarningSoundTick >= cooldownTicks) {
                        float[] volumePitch = getVolumeAndPitch(effectInstance, false);

                        player.playSound(
                                getWarningSound(effectInstance),
                                volumePitch[0],
                                volumePitch[1]
                        );
                        lastWarningSoundTick = currentTick;
                    }
                }
            }
        }

        lastDurations.keySet().removeIf(type -> player.getStatusEffect(type) == null);
    }

    private EffectSoundOverride findOverride(StatusEffectInstance effect) {
        String translationKey = effect.getTranslationKey();
        for (EffectSoundOverride override : ModConfig.INSTANCE.effectSoundOverrides) {
            if (translationKey.equals(override.effectId)) {
                return override;
            }
        }
        return null;
    }

    private SoundEvent getExpireSound(StatusEffectInstance effect) {
        EffectSoundOverride override = findOverride(effect);
        if (override != null && override.expireSound != null && !override.expireSound.isEmpty()) {
            Identifier id = Identifier.tryParse(override.expireSound);
            if (id != null) return SoundEvent.of(id);
        }

        if (effect.getEffectType().equals(StatusEffects.STRENGTH) && ModConfig.INSTANCE.soundStrengthExpire != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundStrengthExpire);
        if (effect.getEffectType().equals(StatusEffects.SPEED) && ModConfig.INSTANCE.soundSpeedExpire != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundSpeedExpire);
        if (effect.getEffectType().equals(StatusEffects.FIRE_RESISTANCE) && ModConfig.INSTANCE.soundFireResExpire != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundFireResExpire);

        return ModConfig.INSTANCE.soundPotionExpire != null ? SoundEvent.of(ModConfig.INSTANCE.soundPotionExpire) : ModSounds.POTION_EXPIRE;
    }

    private SoundEvent getWarningSound(StatusEffectInstance effect) {
        EffectSoundOverride override = findOverride(effect);
        if (override != null && override.warningSound != null && !override.warningSound.isEmpty()) {
            Identifier id = Identifier.tryParse(override.warningSound);
            if (id != null) return SoundEvent.of(id);
        }

        if (effect.getEffectType().equals(StatusEffects.STRENGTH) && ModConfig.INSTANCE.soundStrengthWarning != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundStrengthWarning);
        if (effect.getEffectType().equals(StatusEffects.SPEED) && ModConfig.INSTANCE.soundSpeedWarning != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundSpeedWarning);
        if (effect.getEffectType().equals(StatusEffects.FIRE_RESISTANCE) && ModConfig.INSTANCE.soundFireResWarning != null)
            return SoundEvent.of(ModConfig.INSTANCE.soundFireResWarning);

        return ModConfig.INSTANCE.soundPotionWarning != null ? SoundEvent.of(ModConfig.INSTANCE.soundPotionWarning) : ModSounds.POTION_WARNING;
    }

    private float[] getVolumeAndPitch(StatusEffectInstance effect, boolean isExpire) {
        float volume = isExpire ? ModConfig.INSTANCE.volumeExpire : ModConfig.INSTANCE.volumeWarning;
        float pitch = isExpire ? ModConfig.INSTANCE.pitchExpire : ModConfig.INSTANCE.pitchWarning;

        EffectSoundOverride override = findOverride(effect);
        if (override != null) {
            String relevantSound = isExpire ? override.expireSound : override.warningSound;
            if (relevantSound != null && !relevantSound.isEmpty()) {
                return new float[]{override.volume, override.pitch};
            }
        }

        RegistryEntry<StatusEffect> type = effect.getEffectType();

        if (type.equals(StatusEffects.STRENGTH)) {
            if (isExpire && ModConfig.INSTANCE.soundStrengthExpire != null) {
                volume = ModConfig.INSTANCE.soundStrengthExpireVolume;
                pitch = ModConfig.INSTANCE.soundStrengthExpirePitch;
            } else if (!isExpire && ModConfig.INSTANCE.soundStrengthWarning != null) {
                volume = ModConfig.INSTANCE.soundStrengthWarningVolume;
                pitch = ModConfig.INSTANCE.soundStrengthWarningPitch;
            }
        }
        else if (type.equals(StatusEffects.SPEED)) {
            if (isExpire && ModConfig.INSTANCE.soundSpeedExpire != null) {
                volume = ModConfig.INSTANCE.soundSpeedExpireVolume;
                pitch = ModConfig.INSTANCE.soundSpeedExpirePitch;
            } else if (!isExpire && ModConfig.INSTANCE.soundSpeedWarning != null) {
                volume = ModConfig.INSTANCE.soundSpeedWarningVolume;
                pitch = ModConfig.INSTANCE.soundSpeedWarningPitch;
            }
        }
        else if (type.equals(StatusEffects.FIRE_RESISTANCE)) {
            if (isExpire && ModConfig.INSTANCE.soundFireResExpire != null) {
                volume = ModConfig.INSTANCE.soundFireResExpireVolume;
                pitch = ModConfig.INSTANCE.soundFireResExpirePitch;
            } else if (!isExpire && ModConfig.INSTANCE.soundFireResWarning != null) {
                volume = ModConfig.INSTANCE.soundFireResWarningVolume;
                pitch = ModConfig.INSTANCE.soundFireResWarningPitch;
            }
        }
        return new float[]{volume, pitch};
    }
}