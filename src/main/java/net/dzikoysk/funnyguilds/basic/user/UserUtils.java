package net.dzikoysk.funnyguilds.basic.user;

import net.dzikoysk.funnyguilds.basic.guild.Guild;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");

    public static Set<String> getNames(Collection<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toSet());
    }

    public static Set<String> getOnlineNames(Collection<User> users) {
        Set<String> set = new HashSet<>();

        for (User user : users) {
            set.add(user.isOnline() ? "<online>" + user.getName() + "</online>" : user.getName());
        }

        return set;
    }

    public static void removeGuild(Collection<User> users) {
        for (User user : users) {
            user.removeGuild();
        }
    }

    public static void setGuild(Collection<User> users, Guild guild) {
        for (User user : users) {
            user.setGuild(guild);
        }
    }

    public static boolean validateUsername(String name) {
        return USERNAME_PATTERN.matcher(name).matches();
    }

}
