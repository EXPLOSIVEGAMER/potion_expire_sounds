package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.config.BuiltInPresets;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.config.ModConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class PresetScreen extends Screen {
    private static final String TRANSLATION_KEY = "gui." + MOD_ID + ".presets.";
    private static final Minecraft client = Minecraft.getInstance();
    private final ModConfigScreen.Refs refs;
    private final ModConfig cfg;
    private final Screen parent;
    private final String fileName;
    private final String displayName;
    private final Runnable onRefresh;

    public PresetScreen(String fileName, ModConfigScreen.Refs refs, ModConfig cfg, Screen parent, Runnable onRefresh) {
        super(Component.translatable(TRANSLATION_KEY + "title", fileName.replace(".json", "")));

        this.fileName = fileName;
        this.displayName = fileName.replace(".json", "");
        this.refs = refs;
        this.cfg = cfg;
        this.parent = parent;
        this.onRefresh = onRefresh;
    }

    public PresetScreen(String fileName, ModConfigScreen.Refs refs, Screen parent, Runnable onRefresh) {
        this(fileName, refs, ModConfig.loadPresetAsConfig(fileName), parent, onRefresh);
    }

    @Override
    protected void init() {
        super.init();

        Button.Builder loadBtn = Button.builder(
                        Component.translatable(TRANSLATION_KEY + "btn.load").withColor(0xff00ff00),
                        _ -> new ConfirmScreen.Builder(Component.translatable(TRANSLATION_KEY + "load.title", this.displayName), this.parent)
                                .onConfirm((_, _) -> {
                                    applyPreset();
                                    this.onClose();
                                })
                                .build()
                                .open())
                .size(100, 25);

        Button.Builder back = Button.builder(
                        Component.translatable(TRANSLATION_KEY + "btn.back"),
                        _ -> this.onClose()
                ).size(100, 25);

        if (!BuiltInPresets.ALL.containsKey(fileName)) {
            Button delete = Button.builder(
                            Component.translatable(TRANSLATION_KEY + "btn.delete").withColor(0xffff0000),
                            _ -> new ConfirmScreen.Builder(Component.translatable(TRANSLATION_KEY + "delete.title", this.displayName), this.parent)
                                    .onConfirm((_, _) -> {
                                        ModConfig.deletePreset(this.fileName);
                                        if (this.onRefresh != null) this.onRefresh.run();
                                        else this.onClose();
                                    })
                                    .build()
                                    .open()
                    )
                    .bounds(this.width / 2 - 50, this.height / 2 + 12, 100, 25)
                    .build();

            loadBtn.pos(this.width / 2 - 50, this.height / 2 - 25);

            back.pos(this.width / 2 - 50, this.height / 2 + 49);

            this.addRenderableWidget(delete);

        } else {

            loadBtn.pos(this.width / 2 - 50, this.height / 2 - 25);
            back.pos(this.width / 2 - 50, this.height / 2 + 15);
        }

        this.addRenderableWidget(loadBtn.build());
        this.addRenderableWidget(back.build());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(client.font, this.title, this.width / 2, this.height / 2 - 50, 0xffffffff);
    }

    public void open() {
        client.setScreen(this);
    }

    public static void openCreateScreen(Screen parent, Runnable onRefresh) {
        client.setScreen(new CreateScreen(parent, onRefresh));
    }

    public void applyPreset() {
        if (this.cfg == null) return;

        this.refs.enableMod.requestSet(this.cfg.enableMod);
        this.refs.playWarningSound.requestSet(this.cfg.playWarningSound);
        this.refs.playWarningSound2.requestSet(this.cfg.playWarningSound2);
        this.refs.playExpireSound.requestSet(this.cfg.playExpireSound);
        this.refs.warningThreshold.requestSet(this.cfg.warningThreshold / 20);

        this.refs.volumeExpire.requestSet(this.cfg.volumeExpire);
        this.refs.pitchExpire.requestSet(this.cfg.pitchExpire);
        this.refs.volumeWarning.requestSet(this.cfg.volumeWarning);
        this.refs.pitchWarning.requestSet(this.cfg.pitchWarning);
        this.refs.soundPotionExpire.requestSet(this.cfg.soundPotionExpire != null ? this.cfg.soundPotionExpire.toString() : "");
        this.refs.soundPotionWarning.requestSet(this.cfg.soundPotionWarning != null ? this.cfg.soundPotionWarning.toString() : "");

        this.refs.displayPotionHud.requestSet(this.cfg.displayPotionHud);
        this.refs.showInfEffects.requestSet(this.cfg.showInfEffects);
        this.refs.compactHud.requestSet(this.cfg.compactHud);
        this.refs.potionHudItemSize.requestSet(this.cfg.potionHudItemSize);

        this.refs.listType.requestSet(this.cfg.listType != null ? this.cfg.listType : ModConfig.ListType.BLACKLIST);
        this.refs.filterType.requestSet(this.cfg.filterType != null ? this.cfg.filterType : ModConfig.FilterType.BOTH);

        this.refs.effectOptions.forEach((key, opt) ->
                opt.requestSet(this.cfg.effectMap.getOrDefault(key, false)));

        this.refs.soundStrengthExpire.requestSet(this.cfg.soundStrengthExpire != null ? this.cfg.soundStrengthExpire.toString() : "");
        this.refs.soundStrengthWarning.requestSet(this.cfg.soundStrengthWarning != null ? this.cfg.soundStrengthWarning.toString() : "");
        this.refs.soundStrengthExpireVolume.requestSet(this.cfg.soundStrengthExpireVolume);
        this.refs.soundStrengthExpirePitch.requestSet(this.cfg.soundStrengthExpirePitch);
        this.refs.soundStrengthWarningVolume.requestSet(this.cfg.soundStrengthWarningVolume);
        this.refs.soundStrengthWarningPitch.requestSet(this.cfg.soundStrengthWarningPitch);

        this.refs.soundSpeedExpire.requestSet(this.cfg.soundSpeedExpire != null ? this.cfg.soundSpeedExpire.toString() : "");
        this.refs.soundSpeedWarning.requestSet(this.cfg.soundSpeedWarning != null ? this.cfg.soundSpeedWarning.toString() : "");
        this.refs.soundSpeedExpireVolume.requestSet(this.cfg.soundSpeedExpireVolume);
        this.refs.soundSpeedExpirePitch.requestSet(this.cfg.soundSpeedExpirePitch);
        this.refs.soundSpeedWarningVolume.requestSet(this.cfg.soundSpeedWarningVolume);
        this.refs.soundSpeedWarningPitch.requestSet(this.cfg.soundSpeedWarningPitch);

        this.refs.soundFireResExpire.requestSet(this.cfg.soundFireResExpire != null ? this.cfg.soundFireResExpire.toString() : "");
        this.refs.soundFireResWarning.requestSet(this.cfg.soundFireResWarning != null ? this.cfg.soundFireResWarning.toString() : "");
        this.refs.soundFireResExpireVolume.requestSet(this.cfg.soundFireResExpireVolume);
        this.refs.soundFireResExpirePitch.requestSet(this.cfg.soundFireResExpirePitch);
        this.refs.soundFireResWarningVolume.requestSet(this.cfg.soundFireResWarningVolume);
        this.refs.soundFireResWarningPitch.requestSet(this.cfg.soundFireResWarningPitch);
    }

    @Override
    public void onClose() {
        if (this.parent instanceof PresetScreen) super.onClose();
        client.setScreen(this.parent);
    }

    private static class CreateScreen extends Screen {
        private final Screen parent;
        private final Runnable onRefresh;

        protected CreateScreen(Screen parent, Runnable onRefresh) {
            super(Component.translatable(TRANSLATION_KEY + "create.title"));
            this.parent = parent;
            this.onRefresh = onRefresh;
        }

        @Override
        protected void init() {
            super.init();

            EditBox textBox = new EditBox(Minecraft.getInstance().font,
                    this.width / 2 - 100, this.height / 2 - 50,
                    200, 25,
                    Component.empty());
            textBox.setMaxLength(32);
            textBox.setHint(Component.translatable(TRANSLATION_KEY + "create.hint"));

            Button createBtn = Button.builder(Component.translatable(TRANSLATION_KEY + "create"), _ -> {
                        String name = textBox.getValue().trim();
                        if (name.isEmpty()) return;
                        if (ModConfig.getPresets().contains(name + ".json")) {
                            new ConfirmScreen.Builder(Component.translatable(TRANSLATION_KEY + "overwrite.title", name), this.parent)
                                    .onConfirm((_, _) -> {
                                        ModConfig.savePreset(name);
                                        if (this.onRefresh != null) this.onRefresh.run();
                                        else this.onClose();
                                    })
                                    .build()
                                    .open();
                        } else {
                            ModConfig.savePreset(name);
                            if (this.onRefresh != null) this.onRefresh.run();
                            else this.onClose();
                        }
                    })
                    .bounds(this.width / 2 - 50, this.height / 2 - 15, 100, 25)
                    .build();

            Button back = Button.builder(Component.translatable(TRANSLATION_KEY + "btn.back"),
                            _ -> this.onClose())
                    .size(50, 25)
                    .pos(this.width / 2 - 25, this.height / 2 + 20)
                    .build();

            this.addRenderableWidget(back);
            this.addRenderableWidget(textBox);
            this.addRenderableWidget(createBtn);
        }

        @Override
        public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            super.extractRenderState(graphics, mouseX, mouseY, a);

            graphics.centeredText(client.font, this.title, this.width / 2, this.height / 2 - 75, 0xFFFFFFFF);
        }

        @Override
        public void onClose() {
            if (this.parent instanceof CreateScreen) super.onClose();
            client.setScreen(this.parent);
        }
    }
}
