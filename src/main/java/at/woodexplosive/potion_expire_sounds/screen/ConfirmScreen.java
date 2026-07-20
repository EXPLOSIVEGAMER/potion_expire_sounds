package at.woodexplosive.potion_expire_sounds.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class ConfirmScreen extends Screen {
    private static final String TRANSLATION_KEY = "gui." + MOD_ID + ".confirm_screen.";
    public static final Minecraft client = Minecraft.getInstance();
    private final Screen parent;

    private final BiConsumer<Screen, Button> onConfirm;
    private final BiConsumer<Screen, Button> onDeny;
    private final Consumer<Screen> onClose;

    private ConfirmScreen(Builder builder) {
        super(builder.title);
        this.parent = builder.parent;
        this.onConfirm = builder.onConfirm != null ? builder.onConfirm : (s, _) -> s.onClose();
        this.onDeny = builder.onDeny != null ? builder.onDeny : (s, _) -> s.onClose();
        this.onClose = builder.onClose != null ? builder.onClose : _ -> {};
    }

    @Override
    protected void init() {
        super.init();

        Button confirm = Button.builder(
                Component.translatable(TRANSLATION_KEY + "btn.confirm").withColor(0xff00ff00),
                        btn -> this.onConfirm.accept(this, btn)
                )
                .bounds(this.width / 2 + 25, this.height / 2 - 25, 100, 25)
                .build();

        Button deny = Button.builder(
                        Component.translatable(TRANSLATION_KEY + "btn.deny").withColor(0xffff0000),
                        btn -> this.onDeny.accept(this, btn)
                )
                .bounds(this.width / 2 - 125, this.height / 2 - 25, 100, 25)
                .build();

        this.addRenderableWidget(confirm);
        this.addRenderableWidget(deny);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(client.font, this.title, this.width / 2, this.height / 2 - 50, 0xffffffff);
    }

    @Override
    public void onClose() {
        if (parent instanceof ConfirmScreen) super.onClose();
        this.onClose.accept(this);
        client.setScreen(this.parent);
    }

    public void open() {
        client.setScreen(this);
    }

    public static class Builder {
        private final Component title;
        private final Screen parent;
        private BiConsumer<Screen, Button> onConfirm;
        private BiConsumer<Screen, Button> onDeny;
        private Consumer<Screen> onClose;

        public Builder(Component title, Screen parent) {
            this.title = title;
            this.parent = parent;
        }

        public Builder onConfirm(BiConsumer<Screen, Button> onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public Builder onDeny(BiConsumer<Screen, Button> onDeny) {
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
