package at.woodexplosive.potion_expire_sounds.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class ConfirmScreen extends Screen {
    private static final String TRANSLATION_KEY = "gui." + MOD_ID + ".confirm_screen.";
    public static final MinecraftClient client = MinecraftClient.getInstance();
    private final Screen parent;

    private final BiConsumer<Screen, ButtonWidget> onConfirm;
    private final BiConsumer<Screen, ButtonWidget> onDeny;
    private final Consumer<Screen> onClose;

    private ConfirmScreen(Builder builder) {
        super(builder.title);
        this.parent = builder.parent;
        this.onConfirm = builder.onConfirm != null ? builder.onConfirm : (s, btn) -> s.close();
        this.onDeny = builder.onDeny != null ? builder.onDeny : (s, btn) -> s.close();
        this.onClose = builder.onClose != null ? builder.onClose : s -> {};
    }

    @Override
    protected void init() {
        super.init();

        ButtonWidget confirm = ButtonWidget.builder(
                Text.translatable(TRANSLATION_KEY + "btn.confirm").formatted(Formatting.GREEN),
                btn -> this.onConfirm.accept(this, btn)
        ).size(50, 25).position(this.width / 2 + 25, this.height / 2 - 25).build();

        ButtonWidget deny = ButtonWidget.builder(
                Text.translatable(TRANSLATION_KEY + "btn.deny").formatted(Formatting.RED),
                btn -> this.onDeny.accept(this, btn)
        ).size(50, 25).position(this.width / 2 - 75, this.height / 2 - 25).build();

        this.addDrawableChild(confirm);
        this.addDrawableChild(deny);
    }

    @Override
    public void render(@NonNull DrawContext graphics, int mouseX, int mouseY, float a) {
        super.render(graphics, mouseX, mouseY, a);
        graphics.drawCenteredTextWithShadow(client.textRenderer, this.title, this.width / 2, this.height / 2 - 50, 0xffffffff);
    }

    @Override
    public void close() {
        if (parent instanceof ConfirmScreen) super.close();
        this.onClose.accept(this);
        client.setScreen(this.parent);
    }

    public void open() {
        client.setScreen(this);
    }

    public static class Builder {
        private final Text title;
        private final Screen parent;
        private BiConsumer<Screen, ButtonWidget> onConfirm;
        private BiConsumer<Screen, ButtonWidget> onDeny;
        private Consumer<Screen> onClose;

        public Builder(Text title, Screen parent) {
            this.title = title;
            this.parent = parent;
        }

        public Builder onConfirm(BiConsumer<Screen, ButtonWidget> onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public Builder onDeny(BiConsumer<Screen, ButtonWidget> onDeny) {
            this.onDeny = onDeny;
            return this;
        }

        public Builder onClose(Consumer<Screen> onClose) {
            this.onClose = onClose;
            return this;
        }

        public ConfirmScreen build() {
            return new ConfirmScreen(this);
        }
    }
}
