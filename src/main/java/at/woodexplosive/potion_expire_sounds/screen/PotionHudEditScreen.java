package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class PotionHudEditScreen extends Screen {

    private static final String TRANSLATION_PATH = "gui." + MOD_ID + ".potion_hud.";

    private final Screen parent;
    private final int boxWidth = 120;
    private final int boxHeight = 40;

    private int x, y, dragOffsetX, dragOffsetY;
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
                            this.x = 10;
                            this.y = 10;
                            this.toConfig();
                        }
                ).dimensions(this.width / 2 - 110, this.height - 30, 100, 20)
                .build();

        this.addDrawableChild(resetButton);
        this.addDrawableChild(closeButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String textStrength = StatusEffects.STRENGTH.value().getName().getString() + " 10s";
        String textSpeed = StatusEffects.SPEED.value().getName().getString() + ":  2m 20s";

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(x, y);

        drawBoarder(context, 0, 0, boxWidth, boxHeight, 0xFFAAAAAA);

        // Strength display
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(StatusEffects.STRENGTH), 4, 2, 18, 18);
        context.drawText(client.textRenderer, textStrength, 26, 6, 0xFFFFFFFF, true);

        // Speed display
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(StatusEffects.SPEED), 4, 20, 18, 18);
        context.drawText(client.textRenderer, textSpeed, 26, 24, 0xFFFFFFFF, true);

        context.getMatrices().popMatrix();

        // Drag Tooltip
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable(TRANSLATION_PATH + "drag_tooltip"), this.width / 2, this.height / 2, 0xAAAAAAAA);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.x() >= x && click.x() <= x + boxWidth && click.y() >= y && click.y() <= y + boxHeight) {
            dragging = true;
            dragOffsetX = (int) click.x() - x;
            dragOffsetY = (int) click.y() - y;
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging) {
            
            int targetX = (int) click.x() - dragOffsetX;
            int targetY = (int) click.y() - dragOffsetY;

            this.x = Math.clamp(targetX, 0, client.getWindow().getScaledWidth() - boxWidth);
            this.y = Math.clamp(targetY, 0, client.getWindow().getScaledHeight() - boxHeight);

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
        if (parent instanceof PotionHudEditScreen) super.close();
        this.client.setScreen(parent);
    }

    private void drawBoarder(DrawContext context, int x, int y, int x2, int y2, int color) {
        context.drawHorizontalLine(x, x2, y, color);
        context.drawHorizontalLine(x, x2, y2, color);
        context.drawVerticalLine(x, y, y2, color);
        context.drawVerticalLine(x2, y, y2, color);
    }

    private void fromConfig() {
        x = (int) (ModConfig.INSTANCE.potionHudX * client.getWindow().getScaledWidth());
        y = (int) (ModConfig.INSTANCE.potionHudY * client.getWindow().getScaledHeight());
    }

    private void toConfig() {
        ModConfig.INSTANCE.potionHudX = (float) this.x / client.getWindow().getScaledWidth();
        ModConfig.INSTANCE.potionHudY = (float) this.y / client.getWindow().getScaledHeight();
    }
}
