package at.woodexplosive.potion_expire_sounds.config.custom;

import at.woodexplosive.potion_expire_sounds.screen.EffectOverrideEditScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

public class EffectOverrideControllerElement extends ControllerWidget<EffectOverrideController> {

    public EffectOverrideControllerElement(EffectOverrideController control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim);
    }

    private void openEditor() {
        playDownSound();
        Minecraft.getInstance().gui.setScreen(new EffectOverrideEditScreen(screen, control.option()));
    }

    @Override
    protected void extractValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractValueText(graphics, mouseX, mouseY, a);
        if (hovered) {
            graphics.requestCursor(isAvailable() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (isMouseOver(event.x(), event.y()) && isAvailable()) {
            openEditor();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (!focused) {
            return false;
        }

        if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_SPACE || event.key() == InputConstants.KEY_NUMPADENTER) {
            openEditor();
            return true;
        }

        return false;
    }

    @Override
    protected int getHoveredControlWidth() {
        return getUnhoveredControlWidth();
    }
}
