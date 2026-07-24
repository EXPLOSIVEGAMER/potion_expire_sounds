package at.woodexplosive.potion_expire_sounds.config.custom;

import at.woodexplosive.potion_expire_sounds.screen.EffectOverrideEditScreen;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class EffectOverrideControllerElement extends ControllerWidget<EffectOverrideController> {

    public EffectOverrideControllerElement(EffectOverrideController control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim);
    }

    private void openEditor() {
        playDownSound();
        MinecraftClient.getInstance().setScreen(new EffectOverrideEditScreen(screen, control.option()));
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && isAvailable()) {
            openEditor();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) {
            return false;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
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
