package at.woodexplosive.potion_expire_sounds.config.custom;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
public class SoundController extends AbstractDropdownController<String> {
    private final List<String> allowedValues;

    public SoundController(Option<String> option, List<String> allowedValues) {
        super(option);
        this.allowedValues = allowedValues;
    }

    @Override
    public List<String> getAllowedValues(String input) { return allowedValues; }

    @Override
    public boolean isValueValid(String value) {
        return value.isEmpty() || allowedValues.contains(value);
    }

    @Override
    public String getValidValue(String value, int index) {
        List<String> matching = allowedValues.stream()
                .filter(v -> v.toLowerCase().contains(value.toLowerCase()))
                .toList();
        if (matching.isEmpty()) return "";
        return matching.get(Math.min(index, matching.size() - 1));
    }

    @Override
    public String getString() {
        return option.pendingValue();
    }

    @Override
    public void setFromString(String value) {
        option.requestSet(value);
    }

    @Override
    public Component formatValue() {
        String val = option().pendingValue();
        if (val.isEmpty()) return Component.literal("None").withStyle(ChatFormatting.GRAY);
        try {
            return Component.literal(Identifier.parse(val).getPath());
        } catch (Exception e) {
            return Component.literal(val);
        }
    }

    @Override
    public SoundControllerElement provideWidget(YACLScreen screen, Dimension<Integer> dim) {
        return new SoundControllerElement(this, screen, dim);
    }
}