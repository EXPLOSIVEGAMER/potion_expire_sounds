package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.custom.ConfigWidget;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import at.woodexplosive.potion_expire_sounds.config.custom.RangedSliderWidget;
import at.woodexplosive.potion_expire_sounds.config.custom.ResetButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
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
        super(Text.translatable(TRANSLATION_PATH + "edit_screen"));
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
        this.fromConfig();

        ButtonWidget closeButton = ButtonWidget.builder(
                        Text.translatable(TRANSLATION_PATH + "close_button"),
                        btn -> this.close()
                )
                .dimensions(this.width / 2 + 10, this.height - 30, 100, 20)
                .build();

        ButtonWidget resetButton = ButtonWidget.builder(
                        Text.translatable(TRANSLATION_PATH + "reset_button"),
                        btn -> {
                            this.hudX = (int) (0.6 * this.width);
                            this.hudY = (int) (0.9 * this.height);
                            this.toConfig();
                        }
                ).dimensions(this.width / 2 - 110, this.height - 30, 100, 20)
                .build();

        TexturedButtonWidget settingsButton = new TexturedButtonWidget(
                2, 2, 20, 20,
                new ButtonTextures(
                        PotionExpireSounds.id("icon/settings"),
                        PotionExpireSounds.id("icon/settings_highlighted")
                ),
                btn -> {
                    if (!(this instanceof SettingsScreen)) {
                        SettingsScreen screen = new SettingsScreen(this,
                                10, 25, 284, 152);
                        this.client.setScreen(screen);
                    } else {
                        this.close();
                    }
                }
        );

        this.addDrawableChild(resetButton);
        this.addDrawableChild(closeButton);
        this.addDrawableChild(settingsButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String textStrength = "";
        String textSpeed = "";

        if (ModConfig.INSTANCE.showText) {
            textStrength = StatusEffects.STRENGTH.value().getName().getString();
            textSpeed = StatusEffects.SPEED.value().getName().getString();
        }

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(hudX, hudY);

        float scale = ModConfig.INSTANCE.hudSize;
        context.getMatrices().scale(scale, scale);

        drawBoarder(context, 0, 0, boxWidth, boxHeight, 0xFFAAAAAA);

        context.drawText(client.textRenderer, textStrength + ": 10s", 26, 6, ModConfig.INSTANCE.textColor, true);
        context.drawText(client.textRenderer, textSpeed + ": 2m 20s", 26, 24, ModConfig.INSTANCE.textColor, true);

        if (ModConfig.INSTANCE.showIcons) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(StatusEffects.SPEED), 4, 20, 18, 18);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(StatusEffects.STRENGTH), 4, 2, 18, 18);
        }

        context.getMatrices().popMatrix();

        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable(TRANSLATION_PATH + "drag_tooltip"), this.width / 2, this.height / 2, 0xAAAAAAAA);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
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
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
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
    public boolean mouseReleased(Click click) {
        dragging = false;
        return super.mouseReleased(click);
    }

    @Override
    public void close() {
        this.toConfig();
        ModConfig.save();
        if (parent instanceof PotionHudEditScreen && !(parent instanceof SettingsScreen)) super.close();
        this.client.setScreen(parent);
    }

    protected void drawBoarder(DrawContext context, int x, int y, int x2, int y2, int color) {
        context.drawHorizontalLine(x, x2, y, color);
        context.drawHorizontalLine(x, x2, y2, color);
        context.drawVerticalLine(x, y, y2, color);
        context.drawVerticalLine(x2, y, y2, color);
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

        // Local list – no more static global state in ConfigWidget
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
            ConfigWidget<TextFieldWidget, Integer> textColorWidget = getIntegerConfigWidget(
                    this.x + 18, this.y + 5,
                    70, 12,
                    Text.translatable(TRANSLATION_PATH + "text_color_input"));

            configWidgets.add(textColorWidget);

            // Hud Size
            ConfigWidget<RangedSliderWidget<Float>, Float> hudSizeWidget = new ConfigWidget<>(
                    new RangedSliderWidget<>(
                            this.x + 2, this.y + 75, 100, 15,
                            Text.empty(),
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

            CheckboxWidget showIconButton = CheckboxWidget.builder(
                            Text.translatable(TRANSLATION_PATH + "show_icon_button"),
                            this.textRenderer
                    )
                    .pos(this.x + 2, this.y + 25)
                    .checked(ModConfig.INSTANCE.showIcons)
                    .callback((checkbox, checked) -> {
                        ModConfig.INSTANCE.showIcons = checked;
                        if (booleanConfigWidgetsRef[0] != null) booleanConfigWidgetsRef[0].set(checked);
                    })
                    .build();

            CheckboxWidget showTextBtn = CheckboxWidget.builder(
                            Text.translatable(TRANSLATION_PATH + "show_text_button"),
                            this.textRenderer
                    ).pos(this.x + 2, this.y + 50)
                    .checked(ModConfig.INSTANCE.showText)
                    .callback(((checkbox, checked) -> {
                        ModConfig.INSTANCE.showText = checked;
                        if (booleanConfigWidgetsRef[1] != null) booleanConfigWidgetsRef[1].set(checked);
                    }))
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
                if (showIconButton.isChecked() != v) showIconButton.onPress(null);
            });

            booleanConfigWidgetsRef[1].setOnValueChanged(v -> {
                if (showTextBtn.isChecked() != v) showTextBtn.onPress(null);
            });

            configWidgets.add(booleanConfigWidgetsRef[0]);
            configWidgets.add(booleanConfigWidgetsRef[1]);

            for (ConfigWidget<?, ?> w : configWidgets) {
                this.addDrawableChild(w.getWidget());
                this.addDrawableChild(new ResetButtonWidget(
                        this.width - 25,
                        w.getWidget().getY(),
                        w
                ));
            }
        }

        private @NonNull ConfigWidget<TextFieldWidget, Integer> getIntegerConfigWidget(int x, int y, int width, int height, Text text) {
            TextFieldWidget textColorField = new TextFieldWidget(
                    this.textRenderer,
                    x, y,
                    width, height,
                    text
            );

            ConfigWidget<TextFieldWidget, Integer> textColorWidget = new ConfigWidget<>(
                    textColorField,
                    0xFFFFFFFF,
                    () -> ModConfig.INSTANCE.textColor,
                    v -> ModConfig.INSTANCE.textColor = v
            );

            // Sync field → widget value
            textColorField.setText(String.format("%08X", textColorWidget.get()));
            textColorField.setChangedListener(raw -> {
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
                    textColorField.setText(String.format("%08X", v))
            );
            return textColorWidget;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.render(context, mouseX, mouseY, deltaTicks);
            enableScissor(context);
            this.drawBoarder(context, this.x, this.y, this.width - 1, this.height - 1, 0xFFFFFFFF);
            // Text Color Input
            context.drawText(this.textRenderer, "0x", this.x + 5, this.y + 7, 0xFFFFFFFF, true);
            context.drawText(this.textRenderer, Text.translatable(TRANSLATION_PATH + "hud_text_color"), this.x + 92, this.y + 7, 0xFFFFFFFF, true);
            // hud size
            context.drawText(this.textRenderer, Text.translatable(TRANSLATION_PATH + "hud_size"), x + 105, y + 78, 0xFFFFFFFF, true);
            context.disableScissor();
        }

        @Override
        public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.renderBackground(context, mouseX, mouseY, deltaTicks);
            enableScissor(context);
            this.renderDarkening(context, this.x, this.y, this.width, this.height);
            context.disableScissor();
        }

        @Override
        public void close() {
            configWidgets.forEach(ConfigWidget::save);
            configWidgets.clear();
            if (parent instanceof SettingsScreen) client.setScreen(null);
            client.setScreen(parent);
        }

        private void enableScissor(DrawContext context) {
            context.enableScissor(this.x, this.y, this.width, this.height);
        }
    }
}