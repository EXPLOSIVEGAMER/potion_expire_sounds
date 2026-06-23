package at.woodexplosive.potion_expire_sounds.config.custom;

import at.woodexplosive.potion_expire_sounds.PotionExpireSounds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ResetButtonWidget extends ImageButton {

    private static final WidgetSprites RESET_ICON = new WidgetSprites(
            PotionExpireSounds.id("icon/reset_btn"),
            PotionExpireSounds.id("icon/reset_btn_disabled"),
            PotionExpireSounds.id("icon/reset_btn_highligted")
    );

    private static final int SIZE = 20;

    private final ConfigWidget<?, ?> target;

    public ResetButtonWidget(int x, int y, ConfigWidget<?, ?> target) {
        super(x, y, SIZE, SIZE, RESET_ICON, _ -> target.reset(), Component.empty());
        this.target = target;
        this.setTooltip(Tooltip.create(Component.translatable("gui.potion_expire_sounds.reset")));
    }

    @Override
    public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        this.active = !this.target.get().equals(this.target.getDefaultValue());
        super.extractContents(graphics, mouseX, mouseY, deltaTicks);
    }
}
