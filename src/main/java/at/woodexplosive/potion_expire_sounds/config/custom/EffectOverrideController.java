package at.woodexplosive.potion_expire_sounds.config.custom;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.EffectSoundOverride;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public record EffectOverrideController(Option<EffectSoundOverride> option) implements Controller<EffectSoundOverride> {

    @Override
    public Component formatValue() {
        EffectSoundOverride value = option.pendingValue();
        if (value.effectId.isEmpty()) {
            return Component.translatable("gui." + PotionExpireSounds.MOD_ID + ".effect_override.unset").withStyle(ChatFormatting.GRAY);
        }

        String expireName = soundDisplayName(value.expireSound);
        String warningName = soundDisplayName(value.warningSound);

        return Component.translatable(value.effectId)
                .copy()
                .append(Component.literal(" → E: " + expireName + ", W: " + warningName
                                + "  (vol " + value.volume + ", pitch " + value.pitch + ")")
                        .withStyle(ChatFormatting.GRAY));
    }

    private static String soundDisplayName(String sound) {
        if (sound == null || sound.isEmpty()) {
            return Component.translatable("yacl.control.text.blank").getString();
        }
        try {
            return Identifier.parse(sound).getPath();
        } catch (Exception e) {
            return sound;
        }
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new EffectOverrideControllerElement(this, screen, widgetDimension);
    }
}
