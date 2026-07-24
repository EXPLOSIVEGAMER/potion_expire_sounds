package at.woodexplosive.potion_expire_sounds.config;

import java.util.Objects;

public class EffectSoundOverride {
    public String effectId;
    public String expireSound;
    public String warningSound;
    public float volume;
    public float pitch;

    public EffectSoundOverride() {
        this("", "", "", 1.0f, 1.0f);
    }

    public EffectSoundOverride(String effectId, String expireSound, String warningSound, float volume, float pitch) {
        this.effectId = effectId;
        this.expireSound = expireSound;
        this.warningSound = warningSound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public EffectSoundOverride copy() {
        return new EffectSoundOverride(effectId, expireSound, warningSound, volume, pitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EffectSoundOverride other)) return false;
        return Float.compare(volume, other.volume) == 0
                && Float.compare(pitch, other.pitch) == 0
                && Objects.equals(effectId, other.effectId)
                && Objects.equals(expireSound, other.expireSound)
                && Objects.equals(warningSound, other.warningSound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effectId, expireSound, warningSound, volume, pitch);
    }
}
