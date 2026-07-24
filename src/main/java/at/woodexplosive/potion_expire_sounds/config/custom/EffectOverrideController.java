package at.woodexplosive.potion_expire_sounds.config.custom;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.EffectSoundOverride;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public record EffectOverrideController(Option<EffectSoundOverride> option) implements Controller<EffectSoundOverride> {

    @Override
    public Text formatValue() {
        EffectSoundOverride value = option.pendingValue();
        if (value.effectId.isEmpty()) {
            return Text.translatable("gui." + PotionExpireSounds.MOD_ID + ".effect_override.unset").formatted(Formatting.GRAY);
        }

        String expireName = soundDisplayName(value.expireSound);
        String warningName = soundDisplayName(value.warningSound);

        return Text.translatable(value.effectId)
                .copy()
                .append(Text.literal(" → E: " + expireName + ", W: " + warningName
                                + "  (vol " + value.volume + ", pitch " + value.pitch + ")")
                        .formatted(Formatting.GRAY));
    }

    private static String soundDisplayName(String sound) {
        if (sound == null || sound.isEmpty()) {
            return Text.translatable("yacl.control.text.blank").getString();
        }
        Identifier id = Identifier.tryParse(sound);
        return id != null ? id.getPath() : sound;
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new EffectOverrideControllerElement(this, screen, widgetDimension);
    }
}
