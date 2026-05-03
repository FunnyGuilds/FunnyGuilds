package net.dzikoysk.funnyguilds.shared;

import java.util.List;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.RangeFormatting;

public final class RankFormatter {

    private RankFormatter() {
    }

    /**
     * Formats a signed point change against a range-formatting list (e.g. {@code killPointsChangeFormat}).
     * The range chooses the visual style (color and sign prefix); the {@code {CHANGE}} token in the chosen
     * format is replaced with the absolute value.
     */
    public static String formatPointsChange(int change, List<RangeFormatting> rangeFormat) {
        return NumberRange.inRangeToString(change, rangeFormat, true)
                .replace("{CHANGE}", String.valueOf(Math.abs(change)));
    }
}
