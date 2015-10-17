package net.dzikoysk.funnyguilds.util;

import org.bukkit.Note.Tone;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum NotePitch {

    NOTE_1C(1, Tone.C, 0.5F),
    NOTE_1D(1, Tone.D, 0.53F),
    NOTE_1E(1, Tone.E, 0.56F),
    NOTE_1F(1, Tone.F, 0.6F),
    NOTE_1G(1, Tone.G, 0.63F),
    NOTE_1A(1, Tone.A, 0.67F),
    NOTE_1B(1, Tone.B, 0.7F),

    NOTE_2C(2, Tone.C, 0.76F),
    NOTE_2D(2, Tone.D, 0.8F),
    NOTE_2E(2, Tone.E, 0.84F),
    NOTE_2F(2, Tone.F, 0.9F),
    NOTE_2G(2, Tone.G, 0.94F),
    NOTE_2A(2, Tone.A, 1.0F),
    NOTE_2B(2, Tone.B, 1.06F),

    NOTE_C(3, Tone.C, 1.12F),
    NOTE_D(3, Tone.D, 1.18F),
    NOTE_E(3, Tone.E, 1.26F),
    NOTE_F(3, Tone.F, 1.34F),
    NOTE_G(3, Tone.G, 1.42F),
    NOTE_A(3, Tone.A, 1.5F),
    NOTE_B(3, Tone.B, 1.6F),

    NOTE_4C(4, Tone.C, 1.68F),
    NOTE_4D(4, Tone.D, 1.78F),
    NOTE_4E(4, Tone.E, 1.88F),
    NOTE_4F(4, Tone.F, 2.0F);

    private final int octave;
    private final Tone tone;
    private final float pitch;

    private NotePitch(int octave, Tone tone, float pitch) {
        this.octave = octave;
        this.tone = tone;
        this.pitch = pitch;
    }

    public static NotePitch getNote(int octave, Tone tone) {
        for (NotePitch note : values())
            if (note.octave == octave && note.tone == tone)
                return note;
        return getNote(3, Tone.C);
    }

    public static void play(Player player, NotePitch note) {
        player.playSound(player.getEyeLocation(), Sound.NOTE_PIANO, 1, note.getPitch());
    }

    public static void play(Player player, int octave, Tone tone) {
        play(player, getNote(octave, tone));
    }

    public int getOctave() {
        return this.octave;
    }

    public Tone getTone() {
        return this.tone;
    }

    public float getPitch() {
        return this.pitch;
    }

}
