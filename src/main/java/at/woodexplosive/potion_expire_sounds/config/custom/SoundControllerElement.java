package at.woodexplosive.potion_expire_sounds.config.custom;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    protected void renderDropdownEntry(DrawContext graphics, Dimension<Integer> entryDimension, String value) {
        // Nur Path anzeigen, voller Identifier wird gespeichert
        String display = value.isEmpty() ? "" : Identifier.of(value).getPath();
        Text text = value.isEmpty()
                ? Text.translatable("yacl.control.text.blank").formatted(Formatting.GRAY)
                : Text.literal(display);

        graphics.drawText(
                textRenderer,
                text,
                entryDimension.xLimit() - textRenderer.getWidth(text) - getDropdownEntryPadding(),
                getTextY(entryDimension),
                0xFFFFFFFF, // hellere Farbe
                true
        );
    }

    @Override
    public Text shortenString(String value) {
        // Zeige Path statt vollem Identifier im Eingabefeld
        String display = value.isEmpty() ? "" : Identifier.of(value).getPath();
        return Text.literal(GuiUtils.shortenString(display, textRenderer, getDimension().width() - 20, "..."));
    }

    @Override
    public void createDropdownWidget() {
        this.dropdownVisible = true;
        this.dropdownWidget = new SoundDropdownWidget(controller, screen, getDimension(), this);
        screen.addPopupControllerWidget(dropdownWidget);
    }
}
