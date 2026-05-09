package at.woodexplosive.potion_expire_sounds.config;

import at.woodexplosive.potion_expire_sounds.config.config_elements.ButtonEntry;
import at.woodexplosive.potion_expire_sounds.screen.PotionHudEditScreen;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

@Environment(EnvType.CLIENT)
public class ModConfigScreen {

    private static final String CONFIG_PATH = "config." + MOD_ID + ".";
    private static final List<String> allSoundEventIds = Registries.SOUND_EVENT.getIds()
            .stream().map(Identifier::toString).toList();

    public static Screen createScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config." + MOD_ID + ".title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(
                Text.translatable(CONFIG_PATH + "category.general")
        );

        ConfigCategory effects = builder.getOrCreateCategory(
                Text.translatable(CONFIG_PATH + "category.effects")
        );

        ConfigCategory advanced = builder.getOrCreateCategory(
                Text.translatable(CONFIG_PATH + "category.advanced")
        );

        ConfigCategory combat = builder.getOrCreateCategory(
                Text.translatable(CONFIG_PATH + "category.combat")
        );

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable(CONFIG_PATH + "play_warning_sound"),
                        ModConfig.INSTANCE.playWarningSound)
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.playWarningSound = b)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable(CONFIG_PATH + "play_warning_sound2"),
                        ModConfig.INSTANCE.playWarningSound2)
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.playWarningSound2 = b)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable(CONFIG_PATH + "play_expire_sound"),
                        ModConfig.INSTANCE.playExpireSound)
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.playExpireSound = b)
                .build());

        general.addEntry(entryBuilder
                .startIntField(
                        Text.translatable(CONFIG_PATH + "warning_threshold"),
                        ModConfig.INSTANCE.warningThreshold / 20
                )
                .setDefaultValue(10)
                .setSaveConsumer(i -> ModConfig.INSTANCE.warningThreshold = Math.max(0, i) * 20)
                .build());

        advanced.addEntry(entryBuilder
                .startTextDescription(Text.translatable(CONFIG_PATH + "sound.desc"))
                .build());

        advanced.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH + "volume_expire"),
                        (int) ModConfig.INSTANCE.volume_expire * 100, 0, 100
                )
                .setDefaultValue(100)
                .setSaveConsumer(i -> ModConfig.INSTANCE.volume_expire = (float) i / 100)
                .build());

        advanced.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH + "pitch_expire"),
                        (int) ModConfig.INSTANCE.pitch_expire * 100, 0, 200
                )
                .setDefaultValue(100)
                .setSaveConsumer(i -> ModConfig.INSTANCE.pitch_expire = (float) i / 100)
                .build());

        advanced.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH + "volume_warning"),
                        (int) ModConfig.INSTANCE.volume_warning * 100, 0, 100
                )
                .setDefaultValue(100)
                .setSaveConsumer(i -> ModConfig.INSTANCE.volume_warning = (float) i / 100)
                .build());

        advanced.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH + "pitch_warning"),
                        (int) ModConfig.INSTANCE.pitch_warning * 100, 0, 200
                )
                .setDefaultValue(100)
                .setSaveConsumer(i -> ModConfig.INSTANCE.pitch_warning = (float) i / 100)
                .build());

        addSoundDropdown(advanced, entryBuilder, CONFIG_PATH + "sound.potion_expire",
                ModConfig.INSTANCE.soundPotionExpire, id -> ModConfig.INSTANCE.soundPotionExpire = id);

        addSoundDropdown(
                advanced, entryBuilder, CONFIG_PATH + "sound.potion_warning",
                ModConfig.INSTANCE.soundPotionWarning, id -> ModConfig.INSTANCE.soundPotionWarning = id);

        advanced.addEntry(entryBuilder
                .startTextDescription(Text.translatable(CONFIG_PATH + "potion_hud.desc"))
                .build());

        advanced.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable(CONFIG_PATH + "potion_hud.toggle"),
                        ModConfig.INSTANCE.displayPotionHud
                )
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.displayPotionHud = b)
                .build());

        advanced.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable(CONFIG_PATH + "potion_hud.show_inf.toggle"),
                        ModConfig.INSTANCE.showInfEffects
                )
                .setDefaultValue(true)
                .setSaveConsumer(b -> ModConfig.INSTANCE.showInfEffects = b)
                .build());

        advanced.addEntry(new ButtonEntry(
                Text.translatable(CONFIG_PATH + "potion_hud.open_editor"),
                () -> MinecraftClient.getInstance().setScreen(new PotionHudEditScreen(MinecraftClient.getInstance().currentScreen))
        ));

        advanced.addEntry(entryBuilder
                .startTextDescription(Text.translatable(CONFIG_PATH + "potion_hud.compact_hud.desc"))
                .build());

        advanced.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable(CONFIG_PATH + "potion_hud.compact_hud.toggle"),
                        ModConfig.INSTANCE.compactHud
                )
                .setDefaultValue(false)
                .setSaveConsumer(b -> ModConfig.INSTANCE.compactHud = b)
                .build());

        advanced.addEntry(entryBuilder
                .startIntSlider(
                        Text.translatable(CONFIG_PATH + "potion_hud.compact_hud.item_size"),
                        ModConfig.INSTANCE.potionHudItemSize,
                        1, 10
                )
                .setDefaultValue(1)
                .setSaveConsumer(i -> ModConfig.INSTANCE.potionHudItemSize = i)
                .build());

        combat.addEntry(entryBuilder
                .startTextDescription(Text.translatable(CONFIG_PATH + "combat.sounds"))
                .build());

        combat.addEntry(entryBuilder
                .startTextDescription(Text.translatable(CONFIG_PATH + "combat.sounds.strength"))
                .build());

        addSoundDropdown(combat, entryBuilder, CONFIG_PATH + "combat.sound.strength.expire",
                ModConfig.INSTANCE.soundStrengthExpire, id -> ModConfig.INSTANCE.soundStrengthExpire = id);

        addSoundDropdown(combat, entryBuilder, CONFIG_PATH + "combat.sound.strength.warning",
                ModConfig.INSTANCE.soundStrengthWarning, id -> ModConfig.INSTANCE.soundStrengthWarning = id);

        combat.addEntry(entryBuilder
                .startTextDescription(Text.translatable(CONFIG_PATH + "combat.sounds.speed"))
                .build());

        addSoundDropdown(combat, entryBuilder, CONFIG_PATH + "combat.sound.speed.expire",
                ModConfig.INSTANCE.soundSpeedExpire, id -> ModConfig.INSTANCE.soundSpeedExpire = id);

        addSoundDropdown(combat, entryBuilder, CONFIG_PATH + "combat.sound.speed.warning",
                ModConfig.INSTANCE.soundSpeedWarning, id -> ModConfig.INSTANCE.soundSpeedWarning = id);

        combat.addEntry(entryBuilder
                .startTextDescription(Text.translatable(CONFIG_PATH + "combat.sounds.fireRes"))
                .build());

        addSoundDropdown(combat, entryBuilder, CONFIG_PATH + "combat.sound.fireRes.expire",
                ModConfig.INSTANCE.soundFireResExpire, id -> ModConfig.INSTANCE.soundFireResExpire = id);

        addSoundDropdown(combat, entryBuilder, CONFIG_PATH + "combat.sound.fireRes.warning",
                ModConfig.INSTANCE.soundFireResWarning, id -> ModConfig.INSTANCE.soundFireResWarning = id);

        effects.addEntry(entryBuilder
                .startTextDescription(Text.translatable(CONFIG_PATH+"filter.desc"))
                .build());

        effects.addEntry(entryBuilder
                .startEnumSelector(
                        Text.translatable(CONFIG_PATH + "list_type"),
                        ModConfig.ListType.class,
                        ModConfig.INSTANCE.listType
                )
                .setDefaultValue(ModConfig.ListType.BLACKLIST)
                .setSaveConsumer(e -> ModConfig.INSTANCE.listType = e)
                .build());

        effects.addEntry(entryBuilder
                .startEnumSelector(
                        Text.translatable(CONFIG_PATH+"filter.type"),
                        ModConfig.FilterType.class,
                        ModConfig.INSTANCE.filterType
                )
                .setDefaultValue(ModConfig.FilterType.BOTH)
                .setSaveConsumer(v -> ModConfig.INSTANCE.filterType = v)
                .build());

        effects.addEntry(entryBuilder
                .startTextDescription(
                        Text.translatable(CONFIG_PATH + "effect_list.desc")
                )
                .build());

        for (Identifier effect : Registries.STATUS_EFFECT.getIds()) {
            effects.addEntry(entryBuilder
                    .startBooleanToggle(
                            Text.translatable(effect.toTranslationKey("effect")),
                            ModConfig.INSTANCE.effectMap.getOrDefault(effect.toTranslationKey("effect"), false)
                    )
                    .setDefaultValue(false)
                    .setSaveConsumer(b -> ModConfig.INSTANCE.effectMap.put(effect.toTranslationKey("effect"), b))
                    .build());
        }

        builder.setSavingRunnable(ModConfig::save);

        return builder.build();
    }

    private static DropdownBoxEntry.DefaultSelectionCellCreator<String> soundEventDropDownBox() {
        return new DropdownBoxEntry.DefaultSelectionCellCreator<>() {
            @Override
            public DropdownBoxEntry.SelectionCellElement<String> create(String selection) {
                return new DropdownBoxEntry.DefaultSelectionCellElement<>(selection, this.toTextFunction) {
                    @Override
                    public void render(DrawContext graphics, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
                        this.rendering = true;
                        this.x = x;
                        this.y = y;
                        this.width = width;
                        this.height = height;
                        boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
                        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

                        if (b) {
                            graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
                            graphics.drawTooltip(textRenderer, Text.literal(this.r), mouseX, mouseY);
                        }

                        graphics.drawTextWithShadow(textRenderer, this.toTextFunction.apply(this.r).asOrderedText(), x + 6, y + 4, b ? -1 : -7829368);
                    }
                };
            }
        };
    }

    private static void addSoundDropdown(ConfigCategory category, ConfigEntryBuilder entryBuilder, String translationKey, Identifier currentValue, Consumer<Identifier> saveConsumer) {
        category.addEntry(entryBuilder
                .startDropdownMenu(
                        Text.translatable(translationKey),
                        DropdownMenuBuilder.TopCellElementBuilder.of(
                                currentValue != null ? currentValue.toString() : "",
                                s -> s
                        ),
                        soundEventDropDownBox()
                )
                .setDefaultValue("")
                .setSelections(allSoundEventIds)
                .setSaveConsumer(s -> saveConsumer.accept(s.isEmpty() ? null : Identifier.of(s)))
                .build()
        );
    }
}
