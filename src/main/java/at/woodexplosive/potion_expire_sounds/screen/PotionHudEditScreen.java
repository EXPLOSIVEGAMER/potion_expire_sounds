package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class PotionHudEditScreen extends Screen {

    private static final String TRANSLATION_PATH = "gui."+MOD_ID+".potion_hud.";

    private int dragOffsetX, dragOffsetY;
    private boolean dragging = false;
    private final Screen parent;

    public PotionHudEditScreen(Screen parent) {
        super(Text.translatable(TRANSLATION_PATH+"edit_screen"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawText(
                this.textRenderer,
                Text.translatable(TRANSLATION_PATH+"element"),
                ModConfig.INSTANCE.potionHudX,
                ModConfig.INSTANCE.potionHudY,
                0xFFFFFFFF,
                true
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable(TRANSLATION_PATH+"drag_tooltip"),
                this.width / 2,
                this.height / 2,
                0xAAAAAAAA
        );
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int x = ModConfig.INSTANCE.potionHudX;
        int y = ModConfig.INSTANCE.potionHudY;

        if (click.x() > this.width || click.y() > this.height) return false;

        if (click.x() >= x && click.x() <= x + 100 && click.y() >= y && click.y() <= y + 20) {
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
}
