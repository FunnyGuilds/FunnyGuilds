package net.dzikoysk.funnyguilds.shared;

import net.dzikoysk.funnyguilds.FunnyGuilds;

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
        StringBuilder sb = new StringBuilder();
        boolean open = false;
        boolean start = false;
        int result = -1;

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
                        sb.append(c);
                    }
            }

            if (end) {
                break;
            }
        }

        try {
            result = Integer.parseInt(sb.toString());
        }
        catch (NumberFormatException e) {
            FunnyGuilds.getPluginLogger().parser(text + " contains an invalid number: " + sb.toString());
        }

        return result;
    }

}
