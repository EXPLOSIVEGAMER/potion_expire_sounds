package at.woodexplosive.potion_expire_sounds.config.custom;

import at.woodexplosive.potion_expire_sounds.config.EffectSoundOverride;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;

public class EffectOverrideControllerBuilder implements ControllerBuilder<EffectSoundOverride> {
    private final Option<EffectSoundOverride> option;

    private EffectOverrideControllerBuilder(Option<EffectSoundOverride> option) {
        this.option = option;
    }

    public static EffectOverrideControllerBuilder create(Option<EffectSoundOverride> option) {
        return new EffectOverrideControllerBuilder(option);
    }

    @Override
    public Controller<EffectSoundOverride> build() {
        return new EffectOverrideController(option);
    }
}
