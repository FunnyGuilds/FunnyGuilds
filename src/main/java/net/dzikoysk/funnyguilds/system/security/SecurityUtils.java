package net.dzikoysk.funnyguilds.system.security;

import net.dzikoysk.funnyguilds.util.StringUtils;

public class SecurityUtils {

    public static String getBustedMessage(String name, String cheat) {
        return StringUtils.colored(
                getSecurityTag() +
                        "Przylapano gracza &d" + name +
                        " &cna uzywaniu cheatu &d" + cheat +
                        " &club innemu o podobnym dzialaniu."
        );
    }

    public static String getNoteMessage(String note) {
        return StringUtils.colored(
                getSecurityTag() +
                        "Notatka: &7" + note
        );
    }

    public static String getSecurityTag() {
        return "&c!!!&4[FunnyGuilds]&c!!! ";
    }

}
