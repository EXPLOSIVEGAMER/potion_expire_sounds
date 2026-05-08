package at.woodexplosive.potion_expire_sounds.config;

import at.woodexplosive.potion_expire_sounds.config.config_elements.ButtonEntry;
import at.woodexplosive.potion_expire_sounds.screen.PotionHudEditScreen;
import at.woodexplosive.potion_expire_sounds.sound.ModSounds;
import me.shedaniel.clothconfig2.api.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

@Environment(EnvType.CLIENT)
public class ModConfigScreen {

    private static final String CONFIG_PATH = "config."+MOD_ID+".";
    private static final List<String> allEffectIds = Registries.STATUS_EFFECT.getIds()
            .stream().map(Identifier::toString).sorted().toList();
    private static final List<String> allSoundEventIds = Registries.SOUND_EVENT.getIds()
            .stream().map(Identifier::toString).sorted().toList();

    public static Screen createScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config."+ MOD_ID+".title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(
                Text.translatable(CONFIG_PATH+"category.general")
        );

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable(CONFIG_PATH+"play_warning_sound"),
                        ModConfig.INSTANCE.playWarningSound)
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.playWarningSound = b)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable(CONFIG_PATH+"play_warning_sound2"),
                        ModConfig.INSTANCE.playWarningSound2)
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.playWarningSound2 = b)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable(CONFIG_PATH+"play_expire_sound"),
                        ModConfig.INSTANCE.playExpireSound)
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.playExpireSound = b)
                .build());

        general.addEntry(entryBuilder
                .startIntField(
                        Text.translatable(CONFIG_PATH+"warning_threshold"),
                        ModConfig.INSTANCE.warningThreshold / 20
                )
                .setDefaultValue(10)
                .setSaveConsumer(i -> ModConfig.INSTANCE.warningThreshold = Math.max(0, i) * 20)
                .build());

        general.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH+"volume_expire"),
                        (int) ModConfig.INSTANCE.volume_expire * 100, 0, 100
                )
                .setDefaultValue(100)
                .setSaveConsumer(i -> ModConfig.INSTANCE.volume_expire = (float) i / 100)
                .build());

        general.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH+"pitch_expire"),
                        (int) ModConfig.INSTANCE.pitch_expire * 100, 0, 200
                )
                .setDefaultValue(100)
                .setSaveConsumer(i -> ModConfig.INSTANCE.pitch_expire = (float) i / 100)
                .build());

        general.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH+"volume_warning"),
                        (int) ModConfig.INSTANCE.volume_warning * 100, 0, 100
                )
                .setDefaultValue(100)
                .setSaveConsumer(i -> ModConfig.INSTANCE.volume_warning = (float) i / 100)
                .build());

        general.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH+"pitch_warning"),
                        (int) ModConfig.INSTANCE.pitch_warning * 100, 0, 200
                )
                .setDefaultValue(100)
                .setSaveConsumer(i -> ModConfig.INSTANCE.pitch_warning = (float) i / 100)
                .build());

        general.addEntry(entryBuilder
                .startEnumSelector(
                        Text.translatable(CONFIG_PATH+"list_type"),
                        ModConfig.ListType.class,
                        ModConfig.INSTANCE.listType
                )
                .setDefaultValue(ModConfig.ListType.BLACKLIST)
                .setSaveConsumer(e -> ModConfig.INSTANCE.listType = e)
                .build());

        general.addEntry(entryBuilder
                .startStrList(
                        Text.translatable(CONFIG_PATH+"effect_list"),
                        ModConfig.INSTANCE.effectList
                )
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(s -> ModConfig.INSTANCE.effectList = s)
                .build());

        general.addEntry(entryBuilder
                .startStringDropdownMenu(
                        Text.translatable(CONFIG_PATH+"sound_potion_expire"),
                        ModConfig.INSTANCE.soundPotionExpire.toString(),
                        Text::of
                )
                .setDefaultValue(ModSounds.POTION_EXPIRE.id().toString())
                .setSelections(allSoundEventIds)
                .setSaveConsumer(s -> ModConfig.INSTANCE.soundPotionExpire = Identifier.of(s))
                .build());

        general.addEntry(entryBuilder
                .startStringDropdownMenu(
                        Text.translatable(CONFIG_PATH+"sound_potion_warning"),
                        ModConfig.INSTANCE.soundPotionWarning.toString(),
                        Text::of
                )
                .setDefaultValue(ModSounds.POTION_WARNING.id().toString())
                .setSelections(allSoundEventIds)
                .setSaveConsumer(s -> ModConfig.INSTANCE.soundPotionWarning = Identifier.of(s))
                .build());


        general.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable(CONFIG_PATH+"toggle_potion_hud"),
                        ModConfig.INSTANCE.displayPotionHud
                )
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.displayPotionHud = b)
                .build());

        general.addEntry(new ButtonEntry(
                Text.translatable(CONFIG_PATH+"open_potion_hud_editor"),
                () -> MinecraftClient.getInstance().setScreen(new PotionHudEditScreen(MinecraftClient.getInstance().currentScreen))
        ));

        return builder.build();
    }
}
