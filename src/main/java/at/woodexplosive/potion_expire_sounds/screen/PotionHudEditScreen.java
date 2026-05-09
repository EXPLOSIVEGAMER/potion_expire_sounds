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

    private int dragOffsetX, dragOffsetY;
    private boolean dragging = false;
    private final Screen parent;

    private int maxTextLength = 10;

    public PotionHudEditScreen(Screen parent) {
        super(Text.translatable(TRANSLATION_PATH + "edit_screen"));
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();

        ButtonWidget closeButton = ButtonWidget.builder(
                        Text.translatable(TRANSLATION_PATH + "close_button"),
                        btn -> this.close()
                )
                .dimensions(this.width / 2 - 100, this.height - 30, 200, 20)
                .build();

        this.addDrawableChild(closeButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int x = ModConfig.INSTANCE.potionHudX;
        int y = ModConfig.INSTANCE.potionHudY;

        String textStrength = StatusEffects.STRENGTH.value().getName().getString() + " 10s";
        String textSpeed = StatusEffects.SPEED.value().getName().getString() + " 120s";
        maxTextLength = Math.max(client.textRenderer.getWidth(textStrength), client.textRenderer.getWidth(textSpeed));
        int boarderY;
        if (!isAtBottom(y)) {
            boarderY = y;
        } else {
            boarderY = correctedY(y);
        }

        drawBoarder(context, x - 24, boarderY - 6, x + maxTextLength + 4, boarderY + 34, 0xFFAAAAAA);

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(StatusEffects.STRENGTH), x - 20, y - 4, 18, 18);
        context.drawText(client.textRenderer, textStrength, x, y, 0xFFFFFFFF, true);
        y = correctedY(y);
        context.drawText(client.textRenderer, textSpeed, x, y, 0xFFFFFFFF, true);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture(StatusEffects.SPEED), x - 20, y - 4, 18, 18);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable(TRANSLATION_PATH + "drag_tooltip"),
                this.width / 2,
                this.height / 2,
                0xAAAAAAAA
        );
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int x = ModConfig.INSTANCE.potionHudX;
        int y = ModConfig.INSTANCE.potionHudY;

        int boarderY;
        if (!isAtBottom(y)) {
            boarderY = y;
        } else {
            boarderY = correctedY(y);
        }

        if (click.x() >= x - 24 && click.x() <= x + maxTextLength + 4 && click.y() >= boarderY - 6 && click.y() <= boarderY + 34) {
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
            ModConfig.INSTANCE.potionHudX = (int) click.x() - dragOffsetX;
            ModConfig.INSTANCE.potionHudY = (int) click.y() - dragOffsetY;
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
        ModConfig.save();
        if (parent instanceof PotionHudEditScreen) super.close();
        this.client.setScreen(parent);
    }

    private boolean isAtBottom(int y) {
        return y > client.getWindow().getScaledHeight() / 2;
    }

    private int correctedY(int y) {
        return isAtBottom(y) ? y - 18 : y + 18;
    }

    private void drawBoarder(DrawContext context, int x, int y, int x2, int y2, int color) {
        context.drawHorizontalLine(x, x2, y, color);
        context.drawHorizontalLine(x, x2, y2, color);
        context.drawVerticalLine(x, y, y2, color);
        context.drawVerticalLine(x2, y, y2, color);
    }
}
