package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.custom.ConfigWidget;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.config.custom.RangedSliderWidget;
import at.woodexplosive.potion_expire_sounds.config.custom.ResetButtonWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class PotionHudEditScreen extends Screen {

    private static final String TRANSLATION_PATH = "gui." + MOD_ID + ".potion_hud.";

    private final Screen parent;
    private final int boxWidth = 120;
    private final int boxHeight = 40;

    private int hudX, hudY, dragOffsetX, dragOffsetY;
    private boolean dragging = false;

    public PotionHudEditScreen(Screen parent) {
        super(Component.translatable(TRANSLATION_PATH + "edit_screen"));
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
        this.fromConfig();

        Button closeButton = Button.builder(
                        Component.translatable(TRANSLATION_PATH + "close_button"),
                        _ -> this.onClose()
                )
                .bounds(this.width / 2 + 10, this.height - 30, 100, 20)
                .build();

        Button resetButton = Button.builder(
                        Component.translatable(TRANSLATION_PATH + "reset_button"),
                        _ -> {
                            this.hudX = (int) (0.6 * this.width);
                            this.hudY = (int) (0.9 * this.height);
                            this.toConfig();
                        }
                ).bounds(this.width / 2 - 110, this.height - 30, 100, 20)
                .build();

        ImageButton settingsButton = new ImageButton(
                2, 2, 20, 20,
                new WidgetSprites(
                        PotionExpireSounds.id("icon/settings"),
                        PotionExpireSounds.id("icon/settings_highlighted")
                ),
                _ -> {
                    if (!(this instanceof SettingsScreen)) {
                        SettingsScreen screen = new SettingsScreen(this,
                                10, 25, 284, 152);
                        this.minecraft.gui.setScreen(screen);
                    } else {
                        this.onClose();
                    }
                },
                Component.empty()
        );

        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(closeButton);
        this.addRenderableWidget(settingsButton);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTick) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTick);

        String textStrength = "";
        String textSpeed = "";

        if (ModConfig.INSTANCE.showText) {
            textStrength = MobEffects.STRENGTH.value().getDisplayName().getString();
            textSpeed = MobEffects.SPEED.value().getDisplayName().getString();
        }

        graphics.pose().pushMatrix();
        graphics.pose().translate((float) hudX, (float) hudY);

        float scale = ModConfig.INSTANCE.hudSize;
        graphics.pose().scale(scale, scale);

        drawBoarder(graphics, 0, 0, boxWidth, boxHeight, 0xFFAAAAAA);

        graphics.text(font, Component.literal(textStrength + ": 10s"), 26, 6, ModConfig.INSTANCE.textColor, true);
        graphics.text(font, Component.literal(textSpeed + ": 2m 20s"), 26, 24, ModConfig.INSTANCE.textColor, true);

        if (ModConfig.INSTANCE.showIcons) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.getMobEffectSprite(MobEffects.SPEED), 4, 20, 18, 18);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.getMobEffectSprite(MobEffects.STRENGTH), 4, 2, 18, 18);
        }

        graphics.pose().popMatrix();

        graphics.centeredText(this.font, Component.translatable(TRANSLATION_PATH + "drag_tooltip"), this.width / 2, this.height / 2, 0xAAAAAAAA);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        int scaledW = (int) (boxWidth * ModConfig.INSTANCE.hudSize);
        int scaledH = (int) (boxHeight * ModConfig.INSTANCE.hudSize);
        if (click.x() >= hudX && click.x() <= hudX + scaledW && click.y() >= hudY && click.y() <= hudY + scaledH) {
            dragging = true;
            dragOffsetX = (int) click.x() - hudX;
            dragOffsetY = (int) click.y() - hudY;
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
        if (dragging) {
            int scaledW = (int) (boxWidth * ModConfig.INSTANCE.hudSize);
            int scaledH = (int) (boxHeight * ModConfig.INSTANCE.hudSize);
            int targetX = (int) click.x() - dragOffsetX;
            int targetY = (int) click.y() - dragOffsetY;
            this.hudX = Math.clamp(targetX, 0, this.width - scaledW);
            this.hudY = Math.clamp(targetY, 0, this.height - scaledH);
            this.toConfig();
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent click) {
        dragging = false;
        return super.mouseReleased(click);
    }

    @Override
    public void onClose() {
        this.toConfig();
        ModConfig.save();
        if (parent instanceof PotionHudEditScreen && !(parent instanceof SettingsScreen)) super.onClose();
        this.minecraft.gui.setScreen(parent);
    }

    protected void drawBoarder(GuiGraphicsExtractor graphics, int x, int y, int x2, int y2, int color) {
        graphics.horizontalLine(x, x2, y, color);
        graphics.horizontalLine(x, x2, y2, color);
        graphics.verticalLine(x, y, y2, color);
        graphics.verticalLine(x2, y, y2, color);
    }

    private void fromConfig() {
        int scaledW = (int) (boxWidth * ModConfig.INSTANCE.hudSize);
        int scaledH = (int) (boxHeight * ModConfig.INSTANCE.hudSize);
        hudX = Math.clamp((int) (ModConfig.INSTANCE.potionHudX * this.width), 0, Math.max(0, this.width - scaledW));
        hudY = Math.clamp((int) (ModConfig.INSTANCE.potionHudY * this.height), 0, Math.max(0, this.height - scaledH));
    }

    private void toConfig() {
        ModConfig.INSTANCE.potionHudX = (float) this.hudX / this.width;
        ModConfig.INSTANCE.potionHudY = (float) this.hudY / this.height;
    }

    public static class SettingsScreen extends PotionHudEditScreen {
        private static final String TRANSLATION_PATH = PotionHudEditScreen.TRANSLATION_PATH + "settings_screen.";

        private final List<ConfigWidget<?, ?>> configWidgets = new ArrayList<>();

        private final Screen parent;
        private final int x, y, width, height;

        private SettingsScreen(Screen parent, int x, int y, int width, int height) {
            super(parent);
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void init() {
            super.init();
            configWidgets.clear();

            // --- text color input ---
            ConfigWidget<EditBox, Integer> textColorWidget = getIntegerConfigWidget(
                    this.x + 18, this.y + 5,
                    70, 12,
                    Component.translatable(TRANSLATION_PATH + "text_color_input"));

            configWidgets.add(textColorWidget);

            // Hud Size
            ConfigWidget<RangedSliderWidget<Float>, Float> hudSizeWidget = new ConfigWidget<>(
                    new RangedSliderWidget<>(
                            this.x + 2, this.y + 75, 100, 15,
                            Component.empty(),
                            0.5f, 2f, 0.1f, ModConfig.INSTANCE.hudSize,
                            value -> (float) Math.round(value * 10) / 10
                    ),
                    1f,
                    () -> ModConfig.INSTANCE.hudSize,
                    f -> ModConfig.INSTANCE.hudSize = f
            );

            hudSizeWidget.getWidget().setOnChange(v -> { hudSizeWidget.set(v); hudSizeWidget.save(); });
            hudSizeWidget.setOnValueChanged(v -> hudSizeWidget.getWidget().setValue(v));

            configWidgets.add(hudSizeWidget);

            // --- show icons checkbox ---
            @SuppressWarnings("unchecked")
            ConfigWidget<?, Boolean>[] booleanConfigWidgetsRef = new ConfigWidget[2];

            Checkbox showIconButton = Checkbox.builder(
                            Component.translatable(TRANSLATION_PATH + "show_icon_button"),
                            this.font
                    )
                    .pos(this.x + 2, this.y + 25)
                    .selected(ModConfig.INSTANCE.showIcons)
                    .onValueChange((_, checked) -> {
                        ModConfig.INSTANCE.showIcons = checked;
                        if (booleanConfigWidgetsRef[0] != null) booleanConfigWidgetsRef[0].set(checked);
                    })
                    .build();

            Checkbox showTextBtn = Checkbox.builder(
                            Component.translatable(TRANSLATION_PATH + "show_text_button"),
                            this.font
                    ).pos(this.x + 2, this.y + 50)
                    .selected(ModConfig.INSTANCE.showText)
                    .onValueChange((_, checked) -> {
                        ModConfig.INSTANCE.showText = checked;
                        if (booleanConfigWidgetsRef[1] != null) booleanConfigWidgetsRef[1].set(checked);
                    })
                    .build();

            booleanConfigWidgetsRef[0] = new ConfigWidget<>(
                    showIconButton,
                    true,
                    () -> ModConfig.INSTANCE.showIcons,
                    v -> ModConfig.INSTANCE.showIcons = v
            );

            booleanConfigWidgetsRef[1] = new ConfigWidget<>(
                    showTextBtn,
                    true,
                    () -> ModConfig.INSTANCE.showText,
                    v -> ModConfig.INSTANCE.showText = v
            );

            // Sync value → checkbox (called on reset/load)
            booleanConfigWidgetsRef[0].setOnValueChanged(v -> {
                if (showIconButton.selected() != v) {
                    showIconButton.onPress(new InputWithModifiers() {
                        public int input() { return 0; }
                        public int modifiers() { return 0; }
                    });
                }
            });

            booleanConfigWidgetsRef[1].setOnValueChanged(v -> {
                if (showTextBtn.selected() != v) {
                    showTextBtn.onPress(new InputWithModifiers() {
                        public int input() { return 0; }
                        public int modifiers() { return 0; }
                    });
                }
            });

            configWidgets.add(booleanConfigWidgetsRef[0]);
            configWidgets.add(booleanConfigWidgetsRef[1]);

            for (ConfigWidget<?, ?> w : configWidgets) {
                this.addRenderableWidget(w.getWidget());
                this.addRenderableWidget(new ResetButtonWidget(
                        this.width - 25,
                        w.getWidget().getY(),
                        w
                ));
            }
        }

        private @NonNull ConfigWidget<EditBox, Integer> getIntegerConfigWidget(int x, int y, int width, int height, Component text) {
            EditBox textColorField = new EditBox(
                    this.font,
                    x, y,
                    width, height,
                    text
            );

            ConfigWidget<EditBox, Integer> textColorWidget = new ConfigWidget<>(
                    textColorField,
                    0xFFFFFFFF,
                    () -> ModConfig.INSTANCE.textColor,
                    v -> ModConfig.INSTANCE.textColor = v
            );

            // Sync field → widget value
            textColorField.setValue(String.format("%08X", textColorWidget.get()));
            textColorField.setResponder(raw -> {
                try {
                    String cleaned = raw.replaceAll("[^0-9a-fA-F]", "");
                    if (!cleaned.matches("[0-9a-fA-F]{8}")) return;
                    textColorWidget.set(Long.decode("0x" + cleaned.toUpperCase()).intValue());
                    textColorWidget.save();
                } catch (NumberFormatException e) {
                    PotionExpireSounds.LOGGER.error("No valid Hex", e);
                }
            });

            // Sync widget value → field (called on reset/load)
            textColorWidget.setOnValueChanged(v ->
                    textColorField.setValue(String.format("%08X", v))
            );
            return textColorWidget;
        }

        @Override
        public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTick) {
            super.extractRenderState(graphics, mouseX, mouseY, deltaTick);
            enableScissor(graphics);
            this.drawBoarder(graphics, this.x, this.y, this.width - 1, this.height - 1, 0xFFFFFFFF);
            // Text Color Input
            graphics.text(this.font, Component.literal("0x"), this.x + 5, this.y + 7, 0xFFFFFFFF, true);
            graphics.text(this.font, Component.translatable(TRANSLATION_PATH + "hud_text_color"), this.x + 92, this.y + 7, 0xFFFFFFFF, true);
            // hud size
            graphics.text(this.font, Component.translatable(TRANSLATION_PATH + "hud_size"), x + 105, y + 78, 0xFFFFFFFF, true);
            graphics.disableScissor();
        }

        @Override
        public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTick) {
            super.extractBackground(graphics, mouseX, mouseY, deltaTick);
            enableScissor(graphics);
            graphics.fill(this.x, this.y, this.width, this.height, 0xC0101010);
            graphics.disableScissor();
        }

        @Override
        public void onClose() {
            configWidgets.forEach(ConfigWidget::save);
            configWidgets.clear();
            if (parent instanceof SettingsScreen) minecraft.gui.setScreen(null);
            minecraft.gui.setScreen(parent);
        }

        private void enableScissor(GuiGraphicsExtractor graphics) {
            graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        }
    }
}
