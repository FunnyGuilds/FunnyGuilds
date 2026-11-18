package net.dzikoysk.funnyguilds.feature.items.gui;

import java.util.List;
import java.util.regex.Pattern;

public final class GuiPattern {

    private static final Pattern TOKEN = Pattern.compile("\\{([^}]+)}");

    private GuiPattern() {
    }

    public static List<String> slots(String row) {
        return TOKEN.matcher(row).results().map(result -> result.group(1)).toList();
    }
}
