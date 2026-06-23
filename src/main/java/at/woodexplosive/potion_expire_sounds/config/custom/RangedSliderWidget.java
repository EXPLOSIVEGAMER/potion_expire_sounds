package at.woodexplosive.potion_expire_sounds.config.custom;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;

public class RangedSliderWidget<T extends Number> extends AbstractSliderButton {

    private final double min;
    private final double max;
    private final double step;
    private final Function<Double, T> toT;
    private Consumer<T> onChange;

    public RangedSliderWidget(int x, int y, int width, int height,
                              Component text,
                              T min, T max, T step, T value,
                              Function<Double, T> toT) {
        super(x, y, width, height, text,
              (value.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue()));
        this.min = min.doubleValue();
        this.max = max.doubleValue();
        this.step = step.doubleValue();
        this.toT = toT;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.literal(String.valueOf(getValue())));
    }

    public void setOnChange(Consumer<T> onChange) {
        this.onChange = onChange;
    }

    @Override
    protected void applyValue() {
        double raw = min + this.value * (max - min);
        double stepped = Math.round(raw / step) * step;
        stepped = Math.clamp(stepped, min, max);
        this.value = (stepped - min) / (max - min);
        if (onChange != null) onChange.accept(getValue());
    }

    public T getValue() {
        return toT.apply(Math.clamp(min + this.value * (max - min), min, max));
    }

    public void setValue(T value) {
        this.value = Math.clamp(
                (value.doubleValue() - min) / (max - min), 0.0, 1.0
        );
        updateMessage();
    }
}
