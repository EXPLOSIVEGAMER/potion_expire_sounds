package at.woodexplosive.potion_expire_sounds.config.custom;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;

/**
 * A small reset button that sits next to a config widget.
 * It renders your 16×16 reset icon and calls {@link ConfigWidget#reset()}
 * when clicked. The button dims itself when the current value already
 * equals the default so the player gets visual feedback.
 */
public class ResetButtonWidget extends TexturedButtonWidget {

    private static final ButtonTextures RESET_ICON = new ButtonTextures(
            PotionExpireSounds.id("icon/reset_btn"),
            PotionExpireSounds.id("icon/reset_btn_disabled"),
            PotionExpireSounds.id("icon/reset_btn_highligted")
    );

    /** Total button size – a little padding around the icon. */
    private static final int SIZE = 20;

    private final ConfigWidget<?, ?> target;

    // -----------------------------------------------------------------
    // Construction
    // -----------------------------------------------------------------

    /**
     * @param x      left edge
     * @param y      top edge
     * @param target the {@link ConfigWidget} this button resets
     */
    public ResetButtonWidget(int x, int y, ConfigWidget<?, ?> target) {
        super(x, y, SIZE, SIZE, RESET_ICON,btn -> target.reset(), net.minecraft.text.Text.empty());
        this.target = target;
    }

    // -----------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------


    @Override
    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.active = !this.target.get().equals(this.target.getDefaultValue());
        super.drawIcon(context, mouseX, mouseY, deltaTicks);

        if (isHovered()) {
            context.drawTooltip(
                    MinecraftClient.getInstance().textRenderer,
                    net.minecraft.text.Text.translatable("gui.potion_expire_sounds.reset"),
                    mouseX, mouseY
            );
        }
    }
}