package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.config.EffectSoundOverride;
import dev.isxander.yacl3.api.Option;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class EffectOverrideEditScreen extends Screen {
    private static final String TK = "gui." + MOD_ID + ".effect_override.";
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static final List<String> ALL_SOUND_IDS = Registries.SOUND_EVENT.getIds().stream()
            .map(Identifier::toString)
            .sorted()
            .toList();
    private static final int VISIBLE_SUGGESTIONS = 6;
    private static final int SUGGESTION_ROW_HEIGHT = 12;
    private static final int SCROLLBAR_WIDTH = 3;

    private final Screen parent;
    private final Option<EffectSoundOverride> option;
    private final List<String> effectIds;

    private String selectedEffectId;
    private TextFieldWidget expireBox;
    private TextFieldWidget warningBox;
    private TextFieldWidget volumeBox;
    private TextFieldWidget pitchBox;

    private List<String> soundSuggestions = List.of();
    private int highlightedSuggestion = -1;
    private int suggestionScroll = 0;

    public EffectOverrideEditScreen(Screen parent, Option<EffectSoundOverride> option) {
        super(Text.translatable(TK + "title"));
        this.parent = parent;
        this.option = option;
        this.effectIds = Registries.STATUS_EFFECT.getIds().stream()
                .map(id -> id.toTranslationKey("effect"))
                .sorted()
                .toList();

        EffectSoundOverride current = option.pendingValue();
        this.selectedEffectId = !current.effectId.isEmpty() && effectIds.contains(current.effectId)
                ? current.effectId
                : effectIds.isEmpty() ? "" : effectIds.getFirst();
    }

    private void setupSoundBox(TextFieldWidget box, String initialValue) {
        box.setMaxLength(256);
        box.setText(initialValue);
        box.setChangedListener(this::updateSoundSuggestions);
    }

    @Override
    protected void init() {
        super.init();

        EffectSoundOverride current = option.pendingValue();
        int centerX = this.width / 2;
        int top = this.height / 2 - 65;

        CyclingButtonWidget<String> effectButton = CyclingButtonWidget.<String>builder(Text::translatable, selectedEffectId)
                .values(effectIds)
                .tooltip(v -> Tooltip.of(Text.translatable(TK + "effect.tooltip")))
                .build(centerX - 100, top, 200, 20,
                        Text.translatable(TK + "effect"),
                        (button, value) -> selectedEffectId = value);

        expireBox = new TextFieldWidget(client.textRenderer, centerX - 100, top + 30, 200, 20, Text.translatable(TK + "expire"));
        expireBox.setTooltip(Tooltip.of(Text.translatable(TK + "expire.tooltip")));
        expireBox.setPlaceholder(Text.translatable(TK + "expire.hint"));
        setupSoundBox(expireBox, current.expireSound != null ? current.expireSound : "");

        warningBox = new TextFieldWidget(client.textRenderer, centerX - 100, top + 60, 200, 20, Text.translatable(TK + "warning"));
        warningBox.setTooltip(Tooltip.of(Text.translatable(TK + "warning.tooltip")));
        warningBox.setPlaceholder(Text.translatable(TK + "warning.hint"));
        setupSoundBox(warningBox, current.warningSound != null ? current.warningSound : "");

        volumeBox = new TextFieldWidget(client.textRenderer, centerX - 100, top + 90, 95, 20, Text.translatable(TK + "volume"));
        volumeBox.setTooltip(Tooltip.of(Text.translatable(TK + "volume.tooltip")));
        volumeBox.setText(String.valueOf(current.volume));

        pitchBox = new TextFieldWidget(client.textRenderer, centerX + 5, top + 90, 95, 20, Text.translatable(TK + "pitch"));
        pitchBox.setTooltip(Tooltip.of(Text.translatable(TK + "pitch.tooltip")));
        pitchBox.setText(String.valueOf(current.pitch));

        ButtonWidget saveBtn = ButtonWidget.builder(Text.translatable(TK + "save"), btn -> {
                    save();
                    this.close();
                })
                .position(centerX - 100, top + 125)
                .size(95, 20)
                .build();

        ButtonWidget cancelBtn = ButtonWidget.builder(Text.translatable(TK + "cancel"), btn -> this.close())
                .position(centerX + 5, top + 125)
                .size(95, 20)
                .build();

        this.addDrawableChild(effectButton);
        this.addDrawableChild(expireBox);
        this.addDrawableChild(warningBox);
        this.addDrawableChild(volumeBox);
        this.addDrawableChild(pitchBox);
        this.addDrawableChild(saveBtn);
        this.addDrawableChild(cancelBtn);
    }

    private void updateSoundSuggestions(String text) {
        String query = text.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            soundSuggestions = List.of();
        } else {
            soundSuggestions = ALL_SOUND_IDS.stream()
                    .filter(id -> id.toLowerCase(Locale.ROOT).contains(query))
                    .sorted((a, b) -> {
                        boolean aStarts = a.startsWith(query);
                        boolean bStarts = b.startsWith(query);
                        if (aStarts != bStarts) return aStarts ? -1 : 1;
                        return a.compareTo(b);
                    })
                    .toList();
        }
        highlightedSuggestion = soundSuggestions.isEmpty() ? -1 : 0;
        suggestionScroll = 0;
    }

    private void applySuggestion(TextFieldWidget box, String suggestion) {
        box.setText(suggestion);
        box.setCursor(suggestion.length(), false);
        hideSuggestions();
    }

    private void hideSuggestions() {
        soundSuggestions = List.of();
        highlightedSuggestion = -1;
        suggestionScroll = 0;
    }

    private void scrollToShow(int index) {
        if (index < suggestionScroll) {
            suggestionScroll = index;
        } else if (index >= suggestionScroll + VISIBLE_SUGGESTIONS) {
            suggestionScroll = index - VISIBLE_SUGGESTIONS + 1;
        }
    }

    private int maxSuggestionScroll() {
        return Math.max(0, soundSuggestions.size() - VISIBLE_SUGGESTIONS);
    }

    private TextFieldWidget focusedSoundBox() {
        if (expireBox.isFocused()) return expireBox;
        if (warningBox.isFocused()) return warningBox;
        return null;
    }

    @Override
    public boolean mouseClicked(@NonNull Click click, boolean doubleClick) {
        TextFieldWidget box = focusedSoundBox();
        if (box != null && !soundSuggestions.isEmpty()) {
            int x = box.getX();
            int y = box.getY() + box.getHeight();
            int width = box.getWidth();
            int last = Math.min(soundSuggestions.size(), suggestionScroll + VISIBLE_SUGGESTIONS);
            for (int i = suggestionScroll; i < last; i++) {
                int rowY = y + (i - suggestionScroll) * SUGGESTION_ROW_HEIGHT;
                if (click.x() >= x && click.x() < x + width && click.y() >= rowY && click.y() < rowY + SUGGESTION_ROW_HEIGHT) {
                    applySuggestion(box, soundSuggestions.get(i));
                    return true;
                }
            }
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        TextFieldWidget box = focusedSoundBox();
        if (box != null && !soundSuggestions.isEmpty()) {
            suggestionScroll = MathHelper.clamp(suggestionScroll - (int) Math.signum(scrollY), 0, maxSuggestionScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(@NonNull KeyInput input) {
        TextFieldWidget box = focusedSoundBox();
        if (box != null && !soundSuggestions.isEmpty()) {
            if (input.key() == GLFW.GLFW_KEY_TAB) {
                applySuggestion(box, soundSuggestions.get(Math.max(highlightedSuggestion, 0)));
                return true;
            }
            if (input.key() == GLFW.GLFW_KEY_DOWN) {
                highlightedSuggestion = (highlightedSuggestion + 1) % soundSuggestions.size();
                scrollToShow(highlightedSuggestion);
                return true;
            }
            if (input.key() == GLFW.GLFW_KEY_UP) {
                highlightedSuggestion = (highlightedSuggestion - 1 + soundSuggestions.size()) % soundSuggestions.size();
                scrollToShow(highlightedSuggestion);
                return true;
            }
            if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
                hideSuggestions();
                return true;
            }
        }
        return super.keyPressed(input);
    }

    private void save() {
        float volume = parseFloatOr(volumeBox.getText(), 1.0f);
        float pitch = parseFloatOr(pitchBox.getText(), 1.0f);
        String expire = parseSoundOrEmpty(expireBox.getText());
        String warning = parseSoundOrEmpty(warningBox.getText());

        option.requestSet(new EffectSoundOverride(selectedEffectId, expire, warning, volume, pitch));
    }

    private static String parseSoundOrEmpty(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return "";
        return Identifier.tryParse(trimmed) != null ? trimmed : "";
    }

    private static float parseFloatOr(String s, float def) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public void render(@NonNull DrawContext graphics, int mouseX, int mouseY, float a) {
        super.render(graphics, mouseX, mouseY, a);
        graphics.drawCenteredTextWithShadow(client.textRenderer, this.title, this.width / 2, this.height / 2 - 95, 0xFFFFFFFF);

        TextFieldWidget focused = focusedSoundBox();
        if (focused != null && !soundSuggestions.isEmpty()) {
            renderSuggestions(graphics, focused);
        }
    }

    private void renderSuggestions(DrawContext graphics, TextFieldWidget box) {
        int x = box.getX() + box.getWidth();
        int y = box.getY();
        int width = box.getWidth();
        boolean scrollable = soundSuggestions.size() > VISIBLE_SUGGESTIONS;
        int listWidth = scrollable ? width - SCROLLBAR_WIDTH : width;

        int last = Math.min(soundSuggestions.size(), suggestionScroll + VISIBLE_SUGGESTIONS);
        for (int i = suggestionScroll; i < last; i++) {
            int rowY = y + (i - suggestionScroll) * SUGGESTION_ROW_HEIGHT;
            int background = i == highlightedSuggestion ? 0xCC335577 : 0xCC000000;
            graphics.fill(x, rowY, x + listWidth, rowY + SUGGESTION_ROW_HEIGHT, background);
            graphics.drawText(client.textRenderer, soundSuggestions.get(i), x + 2, rowY + 2, 0xFFFFFFFF, false);
        }

        if (scrollable) {
            int trackHeight = VISIBLE_SUGGESTIONS * SUGGESTION_ROW_HEIGHT;
            int thumbHeight = Math.max(SUGGESTION_ROW_HEIGHT, trackHeight * VISIBLE_SUGGESTIONS / soundSuggestions.size());
            int thumbY = y + (trackHeight - thumbHeight) * suggestionScroll / maxSuggestionScroll();
            int barX = x + listWidth;
            graphics.fill(barX, y, barX + SCROLLBAR_WIDTH, y + trackHeight, 0xCC000000);
            graphics.fill(barX, thumbY, barX + SCROLLBAR_WIDTH, thumbY + thumbHeight, 0xFFAAAAAA);
        }
    }

    @Override
    public void close() {
        client.setScreen(this.parent);
    }
}
