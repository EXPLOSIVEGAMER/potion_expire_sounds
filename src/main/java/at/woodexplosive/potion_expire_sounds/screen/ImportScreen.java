package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.List;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class ImportScreen extends Screen {
    private static final String TK = "gui." + MOD_ID + ".import.";
    private static final Minecraft client = Minecraft.getInstance();
    private final Screen parent;
    private final Runnable onRefresh;
    private Component feedback = Component.empty();

    public ImportScreen(Screen parent, Runnable onRefresh) {
        super(Component.translatable(TK + "title"));
        this.parent = parent;
        this.onRefresh = onRefresh;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(
                Component.translatable(TK + "btn.back"),
                _ -> this.onClose()
        ).size(100, 25).pos(this.width / 2 - 50, this.height / 2 + 40).build());
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        long imported = paths.stream()
                .filter(p -> p.toString().endsWith(".json"))
                .filter(ModConfig::importPreset)
                .count();

        if (imported > 0) {
            feedback = Component.translatable(TK + "success", imported);
            if (onRefresh != null) onRefresh.run();
        } else {
            feedback = Component.translatable(TK + "fail");
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(client.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFFFF);
        graphics.centeredText(client.font, Component.translatable(TK + "hint"), this.width / 2, this.height / 2 - 10, 0xFFAAAAAA);
        if (!feedback.getString().isEmpty()) {
            graphics.centeredText(client.font, feedback, this.width / 2, this.height / 2 + 15, 0x55FF55);
        }
    }

    @Override
    public void onClose() {
        client.gui.setScreen(parent);
    }
}
