package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.List;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class ImportScreen extends Screen {
    private static final String TK = "gui." + MOD_ID + ".import.";
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private final Screen parent;
    private final Runnable onRefresh;
    private Text feedback = Text.empty();

    public ImportScreen(Screen parent, Runnable onRefresh) {
        super(Text.translatable(TK + "title"));
        this.parent = parent;
        this.onRefresh = onRefresh;
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawable(ButtonWidget.builder(
                Text.translatable(TK + "btn.back"),
                btn -> this.close()
        ).size(100, 25).position(this.width / 2 - 50, this.height / 2 + 40).build());
    }

    @Override
    public void onFilesDropped(List<Path> paths) {
        long imported = paths.stream()
                .filter(p -> p.toString().endsWith(".json"))
                .filter(ModConfig::importPreset)
                .count();

        if (imported > 0) {
            feedback = Text.translatable(TK + "success", imported);
            if (onRefresh != null) onRefresh.run();
        } else {
            feedback = Text.translatable(TK + "fail");
        }
    }

    @Override
    public void render(@NonNull DrawContext graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawText(client.textRenderer, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFFFF, true);
        graphics.drawText(client.textRenderer, Text.translatable(TK + "hint"), this.width / 2, this.height / 2 - 10, 0xAAAAAA, true);
        if (!feedback.getString().isEmpty()) {
            graphics.drawText(client.textRenderer, feedback, this.width / 2, this.height / 2 + 15, 0x55FF55, true);
        }
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
