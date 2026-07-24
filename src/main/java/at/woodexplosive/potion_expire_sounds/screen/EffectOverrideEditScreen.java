package at.woodexplosive.potion_expire_sounds.screen;

import at.woodexplosive.potion_expire_sounds.config.EffectSoundOverride;
import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;

import static at.woodexplosive.potion_expire_sounds.PotionExpireSounds.MOD_ID;

public class EffectOverrideEditScreen extends Screen {
    private static final String TK = "gui." + MOD_ID + ".effect_override.";
    private static final Minecraft client = Minecraft.getInstance();

    private static final List<String> ALL_SOUND_IDS = BuiltInRegistries.SOUND_EVENT.keySet().stream()
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
    private EditBox expireBox;
    private EditBox warningBox;
    private EditBox volumeBox;
    private EditBox pitchBox;

    private List<String> soundSuggestions = List.of();
    private int highlightedSuggestion = -1;
    private int suggestionScroll = 0;

    public EffectOverrideEditScreen(Screen parent, Option<EffectSoundOverride> option) {
        super(Component.translatable(TK + "title"));
        this.parent = parent;
        this.option = option;
        this.effectIds = BuiltInRegistries.MOB_EFFECT.keySet().stream()
                .map(id -> "effect." + id.getNamespace() + "." + id.getPath())
                .sorted()
                .toList();

        EffectSoundOverride current = option.pendingValue();
        this.selectedEffectId = !current.effectId.isEmpty() && effectIds.contains(current.effectId)
                ? current.effectId
                : effectIds.isEmpty() ? "" : effectIds.getFirst();
    }

    private void setupSoundBox(EditBox box, String initialValue) {
        box.setMaxLength(256);
        box.setValue(initialValue);
        box.setResponder(this::updateSoundSuggestions);
    }

    @Override
    protected void init() {
        super.init();

        EffectSoundOverride current = option.pendingValue();
        int centerX = this.width / 2;
        int top = this.height / 2 - 65;

        CycleButton<String> effectButton = CycleButton.<String>builder(Component::translatable, selectedEffectId)
                .withValues(effectIds)
                .withTooltip(_ -> Tooltip.create(Component.translatable(TK + "effect.tooltip")))
                .create(centerX - 100, top, 200, 20,
                        Component.translatable(TK + "effect"),
                        (button, value) -> selectedEffectId = value);

        expireBox = new EditBox(client.font, centerX - 100, top + 30, 200, 20, Component.translatable(TK + "expire"));
        expireBox.setTooltip(Tooltip.create(Component.translatable(TK + "expire.tooltip")));
        expireBox.setHint(Component.translatable(TK + "expire.hint"));
        setupSoundBox(expireBox, current.expireSound != null ? current.expireSound : "");

        warningBox = new EditBox(client.font, centerX - 100, top + 60, 200, 20, Component.translatable(TK + "warning"));
        warningBox.setTooltip(Tooltip.create(Component.translatable(TK + "warning.tooltip")));
        warningBox.setHint(Component.translatable(TK + "warning.hint"));
        setupSoundBox(warningBox, current.warningSound != null ? current.warningSound : "");

        volumeBox = new EditBox(client.font, centerX - 100, top + 90, 95, 20, Component.translatable(TK + "volume"));
        volumeBox.setTooltip(Tooltip.create(Component.translatable(TK + "volume.tooltip")));
        volumeBox.setValue(String.valueOf(current.volume));

        pitchBox = new EditBox(client.font, centerX + 5, top + 90, 95, 20, Component.translatable(TK + "pitch"));
        pitchBox.setTooltip(Tooltip.create(Component.translatable(TK + "pitch.tooltip")));
        pitchBox.setValue(String.valueOf(current.pitch));

        Button saveBtn = Button.builder(Component.translatable(TK + "save"), _ -> {
                    save();
                    this.onClose();
                })
                .bounds(centerX - 100, top + 125, 95, 20)
                .build();

        Button cancelBtn = Button.builder(Component.translatable(TK + "cancel"), _ -> this.onClose())
                .bounds(centerX + 5, top + 125, 95, 20)
                .build();

        this.addRenderableWidget(effectButton);
        this.addRenderableWidget(expireBox);
        this.addRenderableWidget(warningBox);
        this.addRenderableWidget(volumeBox);
        this.addRenderableWidget(pitchBox);
        this.addRenderableWidget(saveBtn);
        this.addRenderableWidget(cancelBtn);
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

    private void applySuggestion(EditBox box, String suggestion) {
        box.setValue(suggestion);
        box.setCursorPosition(suggestion.length());
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

    private EditBox focusedSoundBox() {
        if (expireBox.isFocused()) return expireBox;
        if (warningBox.isFocused()) return warningBox;
        return null;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        EditBox box = focusedSoundBox();
        if (box != null && !soundSuggestions.isEmpty()) {
            int x = box.getX();
            int y = box.getY() + box.getHeight();
            int width = box.getWidth();
            int last = Math.min(soundSuggestions.size(), suggestionScroll + VISIBLE_SUGGESTIONS);
            for (int i = suggestionScroll; i < last; i++) {
                int rowY = y + (i - suggestionScroll) * SUGGESTION_ROW_HEIGHT;
                if (event.x() >= x && event.x() < x + width && event.y() >= rowY && event.y() < rowY + SUGGESTION_ROW_HEIGHT) {
                    applySuggestion(box, soundSuggestions.get(i));
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        EditBox box = focusedSoundBox();
        if (box != null && !soundSuggestions.isEmpty()) {
            suggestionScroll = Mth.clamp(suggestionScroll - (int) Math.signum(scrollY), 0, maxSuggestionScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        EditBox box = focusedSoundBox();
        if (box != null && !soundSuggestions.isEmpty()) {
            if (event.key() == InputConstants.KEY_TAB) {
                applySuggestion(box, soundSuggestions.get(Math.max(highlightedSuggestion, 0)));
                return true;
            }
            if (event.key() == InputConstants.KEY_DOWN) {
                highlightedSuggestion = (highlightedSuggestion + 1) % soundSuggestions.size();
                scrollToShow(highlightedSuggestion);
                return true;
            }
            if (event.key() == InputConstants.KEY_UP) {
                highlightedSuggestion = (highlightedSuggestion - 1 + soundSuggestions.size()) % soundSuggestions.size();
                scrollToShow(highlightedSuggestion);
                return true;
            }
            if (event.key() == InputConstants.KEY_ESCAPE) {
                hideSuggestions();
                return true;
            }
        }
        return super.keyPressed(event);
    }

    private void save() {
        float volume = parseFloatOr(volumeBox.getValue(), 1.0f);
        float pitch = parseFloatOr(pitchBox.getValue(), 1.0f);
        String expire = parseSoundOrEmpty(expireBox.getValue());
        String warning = parseSoundOrEmpty(warningBox.getValue());

        option.requestSet(new EffectSoundOverride(selectedEffectId, expire, warning, volume, pitch));
    }

    private static String parseSoundOrEmpty(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return "";
        try {
            Identifier.parse(trimmed);
            return trimmed;
        } catch (Exception e) {
            return "";
        }
    }

    private static float parseFloatOr(String s, float def) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(client.font, this.title, this.width / 2, this.height / 2 - 95, 0xFFFFFFFF);

        EditBox focused = focusedSoundBox();
        if (focused != null && !soundSuggestions.isEmpty()) {
            renderSuggestions(graphics, focused);
        }
    }

    private void renderSuggestions(GuiGraphicsExtractor graphics, EditBox box) {
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
            graphics.text(client.font, Component.literal(soundSuggestions.get(i)), x + 2, rowY + 2, 0xFFFFFFFF, false);
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
    public void onClose() {
        client.gui.setScreen(this.parent);
    }
}
