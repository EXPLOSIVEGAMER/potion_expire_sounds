package at.woodexplosive.potion_expire_sounds.config.custom;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Wraps a single config entry: holds current value, default, getter/setter,
 * and a UI-sync callback that is called whenever the value changes programmatically.
 *
 * @param <W> the widget type (e.g. TextFieldWidget, CyclingButtonWidget)
 * @param <T> the config value type (e.g. Boolean, Integer, String)
 */
public class ConfigWidget<W extends AbstractWidget, T> {

    private final W widget;
    private final T defaultValue;
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    /** Called whenever the value is changed by reset() or load() so the UI stays in sync. */
    private Consumer<T> onValueChanged;

    private T currentValue;
    private boolean updatingUi = false;

    // -----------------------------------------------------------------
    // Construction
    // -----------------------------------------------------------------

    public ConfigWidget(W widget, T defaultValue, Supplier<T> getter, Consumer<T> setter) {
        this.widget       = widget;
        this.defaultValue = defaultValue;
        this.getter       = getter;
        this.setter       = setter;
        load();
    }

    // -----------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------

    /** Persists {@link #currentValue} back to the config backend. */
    public void save() {
        setter.accept(currentValue);
    }

    /**
     * Reads the value from the config backend and updates the UI.
     * Falls back to {@link #defaultValue} when the backend returns {@code null}.
     */
    public void load() {
        T loaded = (getter != null) ? getter.get() : null;
        setValue(loaded != null ? loaded : defaultValue);
    }

    /**
     * Resets {@link #currentValue} to {@link #defaultValue} and updates the UI.
     * Does NOT persist – call {@link #save()} afterwards if needed.
     */
    public void reset() {
        setValue(defaultValue);
        save();
    }

    /** Returns the current (possibly unsaved) value. */
    public T get() {
        return currentValue;
    }

    /**
     * Updates the current value and notifies the UI consumer.
     * Use this instead of mutating {@link #currentValue} directly.
     */
    public void set(T value) {
        setValue(value);
    }

    public W getWidget() {
        return widget;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Registers the callback that keeps the widget's visual state in sync.
     * The consumer receives the new value every time it changes via
     * {@link #set}, {@link #reset}, or {@link #load}.
     */
    public void setOnValueChanged(Consumer<T> onValueChanged) {
        this.onValueChanged = onValueChanged;
    }

    // -----------------------------------------------------------------
    // Internal
    // -----------------------------------------------------------------

    private void setValue(T value) {
        this.currentValue = value;
        if (onValueChanged != null && !updatingUi) {
            this.updatingUi = true;
            try {
                onValueChanged.accept(value);
            } finally {
                this.updatingUi = false;
            }
        }
    }
}