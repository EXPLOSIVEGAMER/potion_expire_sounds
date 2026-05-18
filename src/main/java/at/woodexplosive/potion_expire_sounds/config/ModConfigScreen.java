package at.woodexplosive.potion_expire_sounds.config;

import at.woodexplosive.potion_expire_sounds.screen.PotionHudEditScreen;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

@Environment(EnvType.CLIENT)
public class ModConfigScreen {

    private static final String CONFIG_PATH = "config." + MOD_ID + ".";

    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable(CONFIG_PATH + "title"))
                .category(buildGeneralCategory())
                .category(buildAdvancedCategory())
                .category(buildEffectsCategory())
                .category(buildCombatCategory())
                .save(ModConfig::save)
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable(CONFIG_PATH + "category.general"))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "play_warning_sound"))
                        .binding(true, () -> ModConfig.INSTANCE.playWarningSound, v -> ModConfig.INSTANCE.playWarningSound = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "play_warning_sound2"))
                        .binding(true, () -> ModConfig.INSTANCE.playWarningSound2, v -> ModConfig.INSTANCE.playWarningSound2 = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "play_expire_sound"))
                        .binding(true, () -> ModConfig.INSTANCE.playExpireSound, v -> ModConfig.INSTANCE.playExpireSound = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "warning_threshold"))
                        .binding(10, () -> ModConfig.INSTANCE.warningThreshold / 20, v -> ModConfig.INSTANCE.warningThreshold = Math.max(0, v) * 20)
                        .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0))
                        .build())
                .build();
    }

    private static ConfigCategory buildAdvancedCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable(CONFIG_PATH + "category.advanced"))
                // Sound Settings
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "sound.desc"))
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "volume_expire"))
                                .binding(1.0f, () -> ModConfig.INSTANCE.volume_expire, v -> ModConfig.INSTANCE.volume_expire = v)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.01f))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "pitch_expire"))
                                .binding(1.0f, () -> ModConfig.INSTANCE.pitch_expire, v -> ModConfig.INSTANCE.pitch_expire = v)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 2f).step(0.01f))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "volume_warning"))
                                .binding(1.0f, () -> ModConfig.INSTANCE.volume_warning, v -> ModConfig.INSTANCE.volume_warning = v)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.01f))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "pitch_warning"))
                                .binding(1.0f, () -> ModConfig.INSTANCE.pitch_warning, v -> ModConfig.INSTANCE.pitch_warning = v)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 2f).step(0.01f))
                                .build())
                        .option(buildSoundOption(CONFIG_PATH + "sound.potion_expire",
                                () -> ModConfig.INSTANCE.soundPotionExpire, v -> ModConfig.INSTANCE.soundPotionExpire = v))
                        .option(buildSoundOption(CONFIG_PATH + "sound.potion_warning",
                                () -> ModConfig.INSTANCE.soundPotionWarning, v -> ModConfig.INSTANCE.soundPotionWarning = v))
                        .build())
                // Potion HUD Settings
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "potion_hud.desc"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "potion_hud.toggle"))
                                .binding(true, () -> ModConfig.INSTANCE.displayPotionHud, v -> ModConfig.INSTANCE.displayPotionHud = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "potion_hud.show_inf.toggle"))
                                .binding(true, () -> ModConfig.INSTANCE.showInfEffects, v -> ModConfig.INSTANCE.showInfEffects = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "potion_hud.open_editor"))
                                .action((screen, opt) -> MinecraftClient.getInstance().setScreen(new PotionHudEditScreen(MinecraftClient.getInstance().currentScreen)))
                                .build())
                        .build())
                // Compact HUD Settings
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "potion_hud.compact_hud.desc"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "potion_hud.compact_hud.toggle"))
                                .binding(false, () -> ModConfig.INSTANCE.compactHud, v -> ModConfig.INSTANCE.compactHud = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable(CONFIG_PATH + "potion_hud.compact_hud.item_size"))
                                .binding(1, () -> ModConfig.INSTANCE.potionHudItemSize, v -> ModConfig.INSTANCE.potionHudItemSize = v)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1))
                                .build())
                        .build())
                .build();
    }

    private static ConfigCategory buildEffectsCategory() {
        var effectsCategory = ConfigCategory.createBuilder()
                .name(Text.translatable(CONFIG_PATH + "category.effects"))
                .option(Option.<ModConfig.ListType>createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "list_type"))
                        .binding(ModConfig.ListType.BLACKLIST, () -> ModConfig.INSTANCE.listType, v -> ModConfig.INSTANCE.listType = v)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ModConfig.ListType.class))
                        .build())
                .option(Option.<ModConfig.FilterType>createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "filter.type"))
                        .binding(ModConfig.FilterType.BOTH, () -> ModConfig.INSTANCE.filterType, v -> ModConfig.INSTANCE.filterType = v)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ModConfig.FilterType.class))
                        .build());

        var effectGroup = OptionGroup.createBuilder()
                .name(Text.translatable(CONFIG_PATH + "effect_list.desc"));

        for (var effect : Registries.STATUS_EFFECT.getIds()) {
            String key = effect.toTranslationKey("effect");
            effectGroup.option(Option.<Boolean>createBuilder()
                    .name(Text.translatable(key))
                    .binding(false,
                            () -> ModConfig.INSTANCE.effectMap.getOrDefault(key, false),
                            v -> ModConfig.INSTANCE.effectMap.put(key, v))
                    .controller(TickBoxControllerBuilder::create)
                    .build());
        }

        return effectsCategory.group(effectGroup.build()).build();
    }

    private static ConfigCategory buildCombatCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable(CONFIG_PATH + "category.combat"))
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "combat.sounds.strength"))
                        .option(buildSoundOption(CONFIG_PATH + "combat.sound.strength.expire",
                                () -> ModConfig.INSTANCE.soundStrengthExpire, v -> ModConfig.INSTANCE.soundStrengthExpire = v))
                        .option(buildSoundOption(CONFIG_PATH + "combat.sound.strength.warning",
                                () -> ModConfig.INSTANCE.soundStrengthWarning, v -> ModConfig.INSTANCE.soundStrengthWarning = v))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "combat.sounds.speed"))
                        .option(buildSoundOption(CONFIG_PATH + "combat.sound.speed.expire",
                                () -> ModConfig.INSTANCE.soundSpeedExpire, v -> ModConfig.INSTANCE.soundSpeedExpire = v))
                        .option(buildSoundOption(CONFIG_PATH + "combat.sound.speed.warning",
                                () -> ModConfig.INSTANCE.soundSpeedWarning, v -> ModConfig.INSTANCE.soundSpeedWarning = v))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable(CONFIG_PATH + "combat.sounds.fireRes"))
                        .option(buildSoundOption(CONFIG_PATH + "combat.sound.fireRes.expire",
                                () -> ModConfig.INSTANCE.soundFireResExpire, v -> ModConfig.INSTANCE.soundFireResExpire = v))
                        .option(buildSoundOption(CONFIG_PATH + "combat.sound.fireRes.warning",
                                () -> ModConfig.INSTANCE.soundFireResWarning, v -> ModConfig.INSTANCE.soundFireResWarning = v))
                        .build())
                .build();
    }

    private static Option<String> buildSoundOption(String translationKey, java.util.function.Supplier<Identifier> getter, java.util.function.Consumer<Identifier> setter) {
        return Option.<String>createBuilder()
                .name(Text.translatable(translationKey))
                .binding("",
                        () -> getter.get() != null ? getter.get().toString() : "",
                        v -> setter.accept(v.isEmpty() ? null : Identifier.of(v)))
                .controller(StringControllerBuilder::create)
                .build();
    }
}