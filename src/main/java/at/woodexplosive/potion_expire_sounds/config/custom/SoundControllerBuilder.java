package at.woodexplosive.potion_expire_sounds.config.custom;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SoundControllerBuilder implements ControllerBuilder<String> {
    private final Option<String> option;
    private List<String> values = List.of();

    private SoundControllerBuilder(Option<String> option) {
        this.option = option;
    }

    public static SoundControllerBuilder create(Option<String> option) {
        return new SoundControllerBuilder(option);
    }

    public SoundControllerBuilder values(List<String> values) {
        this.values = values;
        return this;
    }

    @Override
    public Controller<String> build() {
        return new SoundController(option, values);
    }
}