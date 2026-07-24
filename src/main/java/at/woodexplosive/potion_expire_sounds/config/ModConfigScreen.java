package at.woodexplosive.potion_expire_sounds.config;

import at.woodexplosive.potion_expire_sounds.config.custom.EffectOverrideControllerBuilder;
import at.woodexplosive.potion_expire_sounds.config.custom.SoundControllerBuilder;
import at.woodexplosive.potion_expire_sounds.screen.ImportScreen;
import at.woodexplosive.potion_expire_sounds.screen.PotionHudEditScreen;
import at.woodexplosive.potion_expire_sounds.screen.PresetScreen;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

@Environment(EnvType.CLIENT)
public class ModConfigScreen {

    private static final String CP = "config." + MOD_ID + ".";
    private static final List<String> allSoundEventIds = Stream.concat(
            Stream.of(""),
            BuiltInRegistries.SOUND_EVENT.keySet().stream().map(Identifier::toString)
                    .sorted()).toList();

    public static class Refs {
        // General
        public Option<Boolean> enableMod, playWarningSound, playWarningSound2, playExpireSound;
        public Option<Integer> warningThreshold;
        // Sound
        public Option<Float> volumeExpire, pitchExpire, volumeWarning, pitchWarning;
        public Option<String> soundPotionExpire, soundPotionWarning;
        // HUD
        public Option<Boolean> displayPotionHud, showInfEffects, compactHud;
        public Option<Integer> potionHudItemSize;
        // Filter
        public Option<ModConfig.ListType> listType;
        public Option<ModConfig.FilterType> filterType;
        public final Map<String, Option<Boolean>> effectOptions = new LinkedHashMap<>();
        // Combat – Strength
        public Option<String> soundStrengthExpire, soundStrengthWarning;
        public Option<Float> soundStrengthExpireVolume, soundStrengthExpirePitch,
                soundStrengthWarningVolume, soundStrengthWarningPitch;
        // Combat – Speed
        public Option<String> soundSpeedExpire, soundSpeedWarning;
        public Option<Float> soundSpeedExpireVolume, soundSpeedExpirePitch,
                soundSpeedWarningVolume, soundSpeedWarningPitch;
        // Combat – Fire Resistance
        public Option<String> soundFireResExpire, soundFireResWarning;
        public Option<Float> soundFireResExpireVolume, soundFireResExpirePitch,
                soundFireResWarningVolume, soundFireResWarningPitch;
        // Effect Overrides
        public Option<List<EffectSoundOverride>> effectSoundOverrides;
    }

    public static Screen createScreen(Screen parent) {
        Refs refs = new Refs();
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable(CP + "title"))
                .category(buildGeneralCategory(refs))
                .category(buildAdvancedCategory(refs))
                .category(buildEffectsCategory(refs))
                .category(buildCombatCategory(refs))
                .category(buildEffectOverridesCategory(refs))
                .category(buildPresetCategory(refs, parent))
                .save(ModConfig::save)
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory(Refs refs) {
        refs.enableMod = Option.<Boolean>createBuilder()
                .name(Component.translatable(CP + "enable_mod"))
                .description(OptionDescription.of(Component.translatable(CP + "enable_mod.desc")))
                .binding(true, () -> ModConfig.INSTANCE.enableMod, v -> ModConfig.INSTANCE.enableMod = v)
                .controller(TickBoxControllerBuilder::create)
                .build();
        refs.playWarningSound = Option.<Boolean>createBuilder()
                .name(Component.translatable(CP + "play_warning_sound"))
                .description(OptionDescription.of(Component.translatable(CP + "play_warning_sound.desc")))
                .binding(true, () -> ModConfig.INSTANCE.playWarningSound, v -> ModConfig.INSTANCE.playWarningSound = v)
                .controller(TickBoxControllerBuilder::create)
                .build();
        refs.playWarningSound2 = Option.<Boolean>createBuilder()
                .name(Component.translatable(CP + "play_warning_sound2"))
                .description(OptionDescription.of(Component.translatable(CP + "play_warning_sound2.desc")))
                .binding(true, () -> ModConfig.INSTANCE.playWarningSound2, v -> ModConfig.INSTANCE.playWarningSound2 = v)
                .controller(TickBoxControllerBuilder::create)
                .build();
        refs.playExpireSound = Option.<Boolean>createBuilder()
                .name(Component.translatable(CP + "play_expire_sound"))
                .description(OptionDescription.of(Component.translatable(CP + "play_expire_sound.desc")))
                .binding(true, () -> ModConfig.INSTANCE.playExpireSound, v -> ModConfig.INSTANCE.playExpireSound = v)
                .controller(TickBoxControllerBuilder::create)
                .build();
        refs.warningThreshold = Option.<Integer>createBuilder()
                .name(Component.translatable(CP + "warning_threshold"))
                .description(OptionDescription.of(Component.translatable(CP + "warning_threshold.desc")))
                .binding(30, () -> ModConfig.INSTANCE.warningThreshold / 20, v -> ModConfig.INSTANCE.warningThreshold = Math.max(0, v) * 20)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0))
                .build();
        return ConfigCategory.createBuilder()
                .name(Component.translatable(CP + "category.general"))
                .option(refs.enableMod)
                .option(refs.playWarningSound)
                .option(refs.playWarningSound2)
                .option(refs.playExpireSound)
                .option(refs.warningThreshold)
                .build();
    }

    private static ConfigCategory buildAdvancedCategory(Refs refs) {
        refs.volumeExpire = Option.<Float>createBuilder()
                .name(Component.translatable(CP + "volume_expire"))
                .description(OptionDescription.of(Component.translatable(CP + "volume_expire.desc")))
                .binding(1.0f, () -> ModConfig.INSTANCE.volumeExpire, v -> ModConfig.INSTANCE.volumeExpire = v)
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.01f))
                .build();
        refs.pitchExpire = Option.<Float>createBuilder()
                .name(Component.translatable(CP + "pitch_expire"))
                .description(OptionDescription.of(Component.translatable(CP + "pitch_expire.desc")))
                .binding(1.0f, () -> ModConfig.INSTANCE.pitchExpire, v -> ModConfig.INSTANCE.pitchExpire = v)
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 2f).step(0.01f))
                .build();
        refs.volumeWarning = Option.<Float>createBuilder()
                .name(Component.translatable(CP + "volume_warning"))
                .description(OptionDescription.of(Component.translatable(CP + "volume_warning.desc")))
                .binding(1.0f, () -> ModConfig.INSTANCE.volumeWarning, v -> ModConfig.INSTANCE.volumeWarning = v)
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.01f))
                .build();
        refs.pitchWarning = Option.<Float>createBuilder()
                .name(Component.translatable(CP + "pitch_warning"))
                .description(OptionDescription.of(Component.translatable(CP + "pitch_warning.desc")))
                .binding(1.0f, () -> ModConfig.INSTANCE.pitchWarning, v -> ModConfig.INSTANCE.pitchWarning = v)
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 2f).step(0.01f))
                .build();
        refs.soundPotionExpire = buildSoundOption(CP + "sound.potion_expire",
                () -> ModConfig.INSTANCE.soundPotionExpire, v -> ModConfig.INSTANCE.soundPotionExpire = v,
                "minecraft:entity.elder_guardian.curse");
        refs.soundPotionWarning = buildSoundOption(CP + "sound.potion_warning",
                () -> ModConfig.INSTANCE.soundPotionWarning, v -> ModConfig.INSTANCE.soundPotionWarning = v,
                "minecraft:entity.experience_orb.pickup");

        refs.displayPotionHud = Option.<Boolean>createBuilder()
                .name(Component.translatable(CP + "potion_hud.toggle"))
                .description(OptionDescription.of(Component.translatable(CP + "potion_hud.toggle.desc")))
                .binding(false, () -> ModConfig.INSTANCE.displayPotionHud, v -> ModConfig.INSTANCE.displayPotionHud = v)
                .controller(TickBoxControllerBuilder::create)
                .build();
        refs.showInfEffects = Option.<Boolean>createBuilder()
                .name(Component.translatable(CP + "potion_hud.show_inf.toggle"))
                .description(OptionDescription.of(Component.translatable(CP + "potion_hud.show_inf.toggle.desc")))
                .binding(true, () -> ModConfig.INSTANCE.showInfEffects, v -> ModConfig.INSTANCE.showInfEffects = v)
                .controller(TickBoxControllerBuilder::create)
                .build();
        refs.compactHud = Option.<Boolean>createBuilder()
                .name(Component.translatable(CP + "potion_hud.compact_hud.toggle"))
                .description(OptionDescription.of(Component.translatable(CP + "potion_hud.compact_hud.toggle.desc")))
                .binding(false, () -> ModConfig.INSTANCE.compactHud, v -> ModConfig.INSTANCE.compactHud = v)
                .controller(TickBoxControllerBuilder::create)
                .build();
        refs.potionHudItemSize = Option.<Integer>createBuilder()
                .name(Component.translatable(CP + "potion_hud.compact_hud.item_size"))
                .description(OptionDescription.of(Component.translatable(CP + "potion_hud.compact_hud.item_size.desc")))
                .binding(1, () -> ModConfig.INSTANCE.potionHudItemSize, v -> ModConfig.INSTANCE.potionHudItemSize = v)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1))
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable(CP + "category.advanced"))
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable(CP + "sound.desc"))
                        .option(refs.volumeExpire).option(refs.pitchExpire)
                        .option(refs.volumeWarning).option(refs.pitchWarning)
                        .option(refs.soundPotionExpire).option(refs.soundPotionWarning)
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable(CP + "potion_hud.desc"))
                        .option(refs.displayPotionHud)
                        .option(refs.showInfEffects)
                        .option(ButtonOption.createBuilder()
                                .name(Component.translatable(CP + "potion_hud.open_editor"))
                                .description(OptionDescription.of(Component.translatable(CP + "potion_hud.open_editor.desc")))
                                .text(Component.translatable(CP + "potion_hud.open_editor.open"))
                                .action((_, _) -> Minecraft.getInstance().gui.setScreen(
                                        new PotionHudEditScreen(Minecraft.getInstance().gui.screen())))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable(CP + "potion_hud.compact_hud.desc"))
                        .option(refs.compactHud).option(refs.potionHudItemSize)
                        .build())
                .build();
    }

    private static ConfigCategory buildEffectsCategory(Refs refs) {
        refs.listType = Option.<ModConfig.ListType>createBuilder()
                .name(Component.translatable(CP + "list_type"))
                .description(OptionDescription.of(Component.translatable(CP + "list_type.desc")))
                .binding(ModConfig.ListType.BLACKLIST, () -> ModConfig.INSTANCE.listType, v -> ModConfig.INSTANCE.listType = v)
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ModConfig.ListType.class))
                .build();
        refs.filterType = Option.<ModConfig.FilterType>createBuilder()
                .name(Component.translatable(CP + "filter.type"))
                .description(OptionDescription.of(Component.translatable(CP + "filter.type.desc")))
                .binding(ModConfig.FilterType.BOTH, () -> ModConfig.INSTANCE.filterType, v -> ModConfig.INSTANCE.filterType = v)
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ModConfig.FilterType.class))
                .build();

        OptionGroup.Builder effectGroup = OptionGroup.createBuilder()
                .name(Component.translatable(CP + "effect_list.desc"));
        for (Identifier effect : BuiltInRegistries.MOB_EFFECT.keySet()) {
            String key = "effect." + effect.getNamespace() + "." + effect.getPath();
            Option<Boolean> opt = Option.<Boolean>createBuilder()
                    .name(Component.translatable(key))
                    .binding(false,
                            () -> ModConfig.INSTANCE.effectMap.getOrDefault(key, false),
                            v -> ModConfig.INSTANCE.effectMap.put(key, v))
                    .controller(TickBoxControllerBuilder::create)
                    .build();
            refs.effectOptions.put(key, opt);
            effectGroup.option(opt);
        }

        return ConfigCategory.createBuilder()
                .name(Component.translatable(CP + "category.effects"))
                .option(refs.listType)
                .option(refs.filterType)
                .group(effectGroup.build())
                .build();
    }

    private static ConfigCategory buildCombatCategory(Refs refs) {
        refs.soundStrengthExpire = buildSoundOption(CP + "combat.sound.strength.expire",
                () -> ModConfig.INSTANCE.soundStrengthExpire, v -> ModConfig.INSTANCE.soundStrengthExpire = v, "");
        refs.soundStrengthWarning = buildSoundOption(CP + "combat.sound.strength.warning",
                () -> ModConfig.INSTANCE.soundStrengthWarning, v -> ModConfig.INSTANCE.soundStrengthWarning = v, "");
        refs.soundStrengthExpireVolume = floatOption(CP + "combat.sound.strength.expire.volume", 1f, 0f, 1f,
                () -> ModConfig.INSTANCE.soundStrengthExpireVolume, v -> ModConfig.INSTANCE.soundStrengthExpireVolume = v);
        refs.soundStrengthExpirePitch = floatOption(CP + "combat.sound.strength.expire.pitch", 1f, 0f, 2f,
                () -> ModConfig.INSTANCE.soundStrengthExpirePitch, v -> ModConfig.INSTANCE.soundStrengthExpirePitch = v);
        refs.soundStrengthWarningVolume = floatOption(CP + "combat.sound.strength.warning.volume", 1f, 0f, 1f,
                () -> ModConfig.INSTANCE.soundStrengthWarningVolume, v -> ModConfig.INSTANCE.soundStrengthWarningVolume = v);
        refs.soundStrengthWarningPitch = floatOption(CP + "combat.sound.strength.warning.pitch", 1f, 0f, 2f,
                () -> ModConfig.INSTANCE.soundStrengthWarningPitch, v -> ModConfig.INSTANCE.soundStrengthWarningPitch = v);

        refs.soundSpeedExpire = buildSoundOption(CP + "combat.sound.speed.expire",
                () -> ModConfig.INSTANCE.soundSpeedExpire, v -> ModConfig.INSTANCE.soundSpeedExpire = v, "");
        refs.soundSpeedWarning = buildSoundOption(CP + "combat.sound.speed.warning",
                () -> ModConfig.INSTANCE.soundSpeedWarning, v -> ModConfig.INSTANCE.soundSpeedWarning = v, "");
        refs.soundSpeedExpireVolume = floatOption(CP + "combat.sound.speed.expire.volume", 1f, 0f, 1f,
                () -> ModConfig.INSTANCE.soundSpeedExpireVolume, v -> ModConfig.INSTANCE.soundSpeedExpireVolume = v);
        refs.soundSpeedExpirePitch = floatOption(CP + "combat.sound.speed.expire.pitch", 1f, 0f, 2f,
                () -> ModConfig.INSTANCE.soundSpeedExpirePitch, v -> ModConfig.INSTANCE.soundSpeedExpirePitch = v);
        refs.soundSpeedWarningVolume = floatOption(CP + "combat.sound.speed.warning.volume", 1f, 0f, 1f,
                () -> ModConfig.INSTANCE.soundSpeedWarningVolume, v -> ModConfig.INSTANCE.soundSpeedWarningVolume = v);
        refs.soundSpeedWarningPitch = floatOption(CP + "combat.sound.speed.warning.pitch", 1f, 0f, 2f,
                () -> ModConfig.INSTANCE.soundSpeedWarningPitch, v -> ModConfig.INSTANCE.soundSpeedWarningPitch = v);

        refs.soundFireResExpire = buildSoundOption(CP + "combat.sound.fire_res.expire",
                () -> ModConfig.INSTANCE.soundFireResExpire, v -> ModConfig.INSTANCE.soundFireResExpire = v, "");
        refs.soundFireResWarning = buildSoundOption(CP + "combat.sound.fire_res.warning",
                () -> ModConfig.INSTANCE.soundFireResWarning, v -> ModConfig.INSTANCE.soundFireResWarning = v, "");
        refs.soundFireResExpireVolume = floatOption(CP + "combat.sound.fire_res.expire.volume", 1f, 0f, 1f,
                () -> ModConfig.INSTANCE.soundFireResExpireVolume, v -> ModConfig.INSTANCE.soundFireResExpireVolume = v);
        refs.soundFireResExpirePitch = floatOption(CP + "combat.sound.fire_res.expire.pitch", 1f, 0f, 2f,
                () -> ModConfig.INSTANCE.soundFireResExpirePitch, v -> ModConfig.INSTANCE.soundFireResExpirePitch = v);
        refs.soundFireResWarningVolume = floatOption(CP + "combat.sound.fire_res.warning.volume", 1f, 0f, 1f,
                () -> ModConfig.INSTANCE.soundFireResWarningVolume, v -> ModConfig.INSTANCE.soundFireResWarningVolume = v);
        refs.soundFireResWarningPitch = floatOption(CP + "combat.sound.fire_res.warning.pitch", 1f, 0f, 2f,
                () -> ModConfig.INSTANCE.soundFireResWarningPitch, v -> ModConfig.INSTANCE.soundFireResWarningPitch = v);

        return ConfigCategory.createBuilder()
                .name(Component.translatable(CP + "category.combat"))
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable(CP + "combat.sounds.strength"))
                        .option(refs.soundStrengthExpire).option(refs.soundStrengthWarning)
                        .option(refs.soundStrengthExpireVolume).option(refs.soundStrengthExpirePitch)
                        .option(refs.soundStrengthWarningVolume).option(refs.soundStrengthWarningPitch)
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable(CP + "combat.sounds.speed"))
                        .option(refs.soundSpeedExpire).option(refs.soundSpeedWarning)
                        .option(refs.soundSpeedExpireVolume).option(refs.soundSpeedExpirePitch)
                        .option(refs.soundSpeedWarningVolume).option(refs.soundSpeedWarningPitch)
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable(CP + "combat.sounds.fire_res"))
                        .option(refs.soundFireResExpire).option(refs.soundFireResWarning)
                        .option(refs.soundFireResExpireVolume).option(refs.soundFireResExpirePitch)
                        .option(refs.soundFireResWarningVolume).option(refs.soundFireResWarningPitch)
                        .build())
                .build();
    }

    private static ConfigCategory buildEffectOverridesCategory(Refs refs) {
        ListOption<EffectSoundOverride> listOption = ListOption.<EffectSoundOverride>createBuilder()
                .name(Component.translatable(CP + "effect_overrides.list"))
                .description(OptionDescription.of(Component.translatable(CP + "effect_overrides.list.desc")))
                .binding(new ArrayList<>(),
                        () -> ModConfig.INSTANCE.effectSoundOverrides,
                        v -> ModConfig.INSTANCE.effectSoundOverrides = v)
                .initial(EffectSoundOverride::new)
                .controller(EffectOverrideControllerBuilder::create)
                .build();
        refs.effectSoundOverrides = listOption;

        return ConfigCategory.createBuilder()
                .name(Component.translatable(CP + "category.effect_overrides"))
                .group(listOption)
                .build();
    }

    private static ConfigCategory buildPresetCategory(Refs refs, Screen grandParent) {
        Runnable refresh = () -> Minecraft.getInstance().gui.setScreen(createScreen(grandParent));

        ButtonOption saveButton = ButtonOption.createBuilder()
                .name(Component.translatable(CP + "preset.save"))
                .description(OptionDescription.of(Component.translatable(CP + "preset.save.desc")))
                .text(Component.empty())
                .action((screen, _) -> PresetScreen.openCreateScreen(screen, refresh))
                .build();

        ButtonOption openFolderButton = ButtonOption.createBuilder()
                .name(Component.translatable(CP + "preset.open_folder"))
                .description(OptionDescription.of(Component.translatable(CP + "preset.open_folder.desc")))
                .text(Component.empty())
                .action((_, _) -> Util.getPlatform().openFile(ModConfig.getPresetsDir()))
                .build();

        ButtonOption importButton = ButtonOption.createBuilder()
                .name(Component.translatable(CP + "preset.import"))
                .description(OptionDescription.of(Component.translatable(CP + "preset.import.desc")))
                .text(Component.empty())
                .action((screen, _) -> Minecraft.getInstance().gui.setScreen(new ImportScreen(screen, refresh)))
                .build();

        OptionGroup.Builder builtInGroup = OptionGroup.createBuilder()
                .name(Component.translatable(CP + "group.builtin_presets"));
        BuiltInPresets.ALL.forEach((name, config) ->
                builtInGroup.option(ButtonOption.createBuilder()
                        .name(Component.literal(name))
                        .description(OptionDescription.of(Component.translatable(CP + "presets.builtin.desc")))
                        .text(Component.translatable(CP + "presets.select"))
                        .action((screen, _) -> new PresetScreen(name, refs, config, screen, null).open())
                        .build()));

        ConfigCategory.Builder category = ConfigCategory.createBuilder()
                .name(Component.translatable(CP + "category.presets"))
                .group(builtInGroup.build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable(CP + "group.save_preset"))
                        .option(saveButton)
                        .option(importButton)
                        .option(openFolderButton)
                        .build());

        List<String> presets = ModConfig.getPresets();
        if (!presets.isEmpty()) {
            OptionGroup.Builder loadGroup = OptionGroup.createBuilder()
                    .name(Component.translatable(CP + "group.presets"));
            for (String fileName : presets) {
                String displayName = fileName.replace(".json", "");
                loadGroup.option(ButtonOption.createBuilder()
                        .name(Component.literal(displayName))
                        .description(OptionDescription.of(Component.translatable(CP + "presets.user.desc")))
                        .text(Component.translatable(CP + "presets.select"))
                        .action((screen, _) -> new PresetScreen(fileName, refs, screen, refresh).open())
                        .build());
            }
            category.group(loadGroup.build());
        }

        return category.build();
    }

    private static Option<String> buildSoundOption(String translationKey, Supplier<Identifier> getter,
                                                    Consumer<Identifier> setter, String defSound) {
        return Option.<String>createBuilder()
                .name(Component.translatable(translationKey))
                .description(OptionDescription.of(Component.translatable(translationKey + ".desc")))
                .binding(defSound,
                        () -> getter.get() != null ? getter.get().toString() : "",
                        v -> setter.accept(v.isEmpty() ? null : Identifier.parse(v)))
                .customController(opt -> SoundControllerBuilder.create(opt)
                        .values(allSoundEventIds).build())
                .build();
    }

    private static Option<Float> floatOption(String key, float def, float min, float max,
                                              Supplier<Float> getter, Consumer<Float> setter) {
        return Option.<Float>createBuilder()
                .name(Component.translatable(key))
                .description(OptionDescription.of(Component.translatable(key + ".desc")))
                .binding(def, getter, setter)
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(min, max).step(0.01f))
                .build();
    }
}
