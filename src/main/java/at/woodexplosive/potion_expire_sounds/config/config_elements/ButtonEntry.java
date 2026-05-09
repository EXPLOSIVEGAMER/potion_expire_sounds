package at.woodexplosive.potion_expire_sounds.config.config_elements;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class ButtonEntry extends AbstractConfigListEntry<Object> {

    private final ButtonWidget button;

    public ButtonEntry(Text fieldName, Runnable onClick) {
        super(fieldName, false);
        this.button = ButtonWidget.builder(fieldName, btn -> onClick.run())
                .size(150, 20)
                .build();
    }

    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        button.setX(x);
        button.setY(y);
        button.setWidth(entryWidth);
        button.render(context, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Selectable> narratables() {
        return List.of(button);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(button);
    }
}
