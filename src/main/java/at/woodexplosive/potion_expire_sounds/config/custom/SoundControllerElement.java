package at.woodexplosive.potion_expire_sounds.config.custom;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class SoundControllerElement extends AbstractDropdownControllerElement<String, String> {
    private final SoundController controller;


    public SoundControllerElement(SoundController controller, YACLScreen screen, Dimension<Integer> dim) {
        super(controller, screen, dim);
        this.controller = controller;
    }

    @Override
    public List<String> computeMatchingValues() {
        return controller.getAllowedValues().stream()
                .filter(this::matchingValue)
                .sorted((s1, s2) -> {
                    if (s1.startsWith(inputField) && !s2.startsWith(inputField)) return -1;
                    if (!s1.startsWith(inputField) && s2.startsWith(inputField)) return 1;
                    return s1.compareTo(s2);
                })
                .toList();
    }

    @Override
    public String getString(String object) { return object; }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTick) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTick);
    }

    @Override
    protected void extractDropdownEntry(GuiGraphicsExtractor graphics, Dimension<Integer> entryDimension, String value) {
        String display = value.isEmpty() ? "" : Identifier.parse(value).getPath();
        Component text = value.isEmpty()
                ? Component.translatable("yacl.control.text.blank").withStyle(ChatFormatting.GRAY)
                : Component.literal(display);

        graphics.text(
                textRenderer,
                text,
                entryDimension.xLimit() - textRenderer.width(text) - getDropdownEntryPadding(),
                getTextY(entryDimension),
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public Component shortenString(String value) {
        String display = value.isEmpty() ? "" : Identifier.parse(value).getPath();
        return Component.literal(GuiUtils.shortenString(display, textRenderer, getDimension().width() - 20, "..."));
    }

    @Override
    public void createDropdownWidget() {
        this.dropdownVisible = true;
        this.dropdownWidget = new SoundDropdownWidget(controller, screen, getDimension(), this);
        screen.addPopupControllerWidget(dropdownWidget);
    }
}
