package at.woodexplosive.potion_expire_sounds.config.custom;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import dev.isxander.yacl3.gui.controllers.dropdown.DropdownWidget;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class SoundDropdownWidget  extends DropdownWidget<String> {

    public SoundDropdownWidget(AbstractDropdownController<String> control, YACLScreen screen, Dimension<Integer> dim, AbstractDropdownControllerElement<String, ?> dropdownElement) {
        super(control, screen, dim, dropdownElement);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        GuiUtils.blitGuiTexColor(
                context,
                Screen.MENU_BACKGROUND_TEXTURE,
                dropdownDim.x(), dropdownDim.y(),
                0.0f, 0.0f,
                dropdownDim.width(), dropdownDim.height(),
                32, 32,
                0xFFFFFFFF
        );
    }

    @Override
    public void renderBackground(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        GuiUtils.blitGuiTexColor(
                guiGraphics,
                Screen.MENU_BACKGROUND_TEXTURE,
                dropdownDim.x(), dropdownDim.y(),
                0.0f, 0.0f,
                dropdownDim.width(), dropdownDim.height(),
                32, 32,
                0xFFFFFFFF
        );
    }
}
