package at.woodexplosive.potion_expire_sounds.effect;

import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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

            boolean isInList = ModConfig.INSTANCE.effectList.contains(effectInstance.getTranslationKey());
            boolean isWhiteList = ModConfig.INSTANCE.listType.equals(ModConfig.ListType.WHITELIST);

            if (isWhiteList != isInList) continue;

            RegistryEntry<StatusEffect> type = effectInstance.getEffectType();
            int currentDuration = effectInstance.getDuration();
            int lastDuration = lastDurations.getOrDefault(type, 0);

            if (currentDuration <= warningThreshold && lastDuration > warningThreshold && ModConfig.INSTANCE.playWarningSound) {

                player.playSound(
                        SoundEvent.of(ModConfig.INSTANCE.soundPotionWarning),
                        ModConfig.INSTANCE.volume_warning,
                        ModConfig.INSTANCE.pitch_warning
                );

            } else if (currentDuration <= 20 && lastDuration > 20 && ModConfig.INSTANCE.playExpireSound) {

                player.playSound(
                        SoundEvent.of(ModConfig.INSTANCE.soundPotionExpire),
                        ModConfig.INSTANCE.volume_expire,
                        ModConfig.INSTANCE.pitch_expire
                );

            } else if (currentDuration <= 90 && lastDuration > 20 && ModConfig.INSTANCE.playWarningSound2) {

                if (client.isPaused()) return;

                if (currentDuration % 20 == 0){
                    player.playSound(
                            SoundEvent.of(ModConfig.INSTANCE.soundPotionWarning),
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
}
