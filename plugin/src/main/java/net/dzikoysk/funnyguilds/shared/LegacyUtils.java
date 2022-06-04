package net.dzikoysk.funnyguilds.shared;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import panda.std.Option;

public final class LegacyUtils {

    private LegacyUtils() {
    }

    /**
     * Used to get x from placeholders like {PLACEHOLDER-x}
     *
     * @param text text to search for placeholder
     * @return x value
     */
    public static int getIndex(String text) {
        StringBuilder indexBuilder = new StringBuilder();

        boolean open = false;
        boolean start = false;

        for (char c : text.toCharArray()) {
            boolean end = false;

            switch (c) {
                case '{':
                    open = true;
                    break;
                case '-':
                    start = true;
                    break;
                case '}':
                    end = true;
                    break;
                default:
                    if (open && start) {
                        indexBuilder.append(c);
                    }
            }

            if (end) {
                break;
            }
        }

        Option<Integer> result = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(indexBuilder.toString()));
        if (result.isEmpty()) {
            FunnyGuilds.getPluginLogger().parser(text + " contains an invalid number: " + indexBuilder);
            return -1;
        }

        return result.get();
    }

}
