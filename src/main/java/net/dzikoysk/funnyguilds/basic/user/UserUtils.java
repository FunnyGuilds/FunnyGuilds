package net.dzikoysk.funnyguilds.basic.user;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");

    private final static Map<UUID, User> BY_UUID_USER_COLLECTION = new ConcurrentHashMap<>();
    private final static Map<String, User> BY_NAME_USER_COLLECTION = new ConcurrentHashMap<>();

    public static Set<User> getUsers() {
        return new HashSet<>(BY_UUID_USER_COLLECTION.values());
    }

    public static User get(String nickname) {
        return get(nickname, false);
    }

    public static User get(String nickname, boolean ignoreCase) {
        if (ignoreCase) {
            for (Map.Entry<String, User> entry : BY_NAME_USER_COLLECTION.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(nickname)) {
                    return entry.getValue();
                }
            }

            return null;
        }
        else {
            return BY_NAME_USER_COLLECTION.get(nickname);
        }
    }

    public static User get(UUID uuid) {
        return BY_UUID_USER_COLLECTION.get(uuid);
    }

    public static void addUser(User user) {
        Validate.notNull(user, "user can't be null!");

        BY_UUID_USER_COLLECTION.put(user.getUUID(), user);
        BY_NAME_USER_COLLECTION.put(user.getName(), user);
    }

    public static void removeUser(User user) {
        Validate.notNull(user, "user can't be null!");

        BY_UUID_USER_COLLECTION.remove(user.getUUID());
        BY_NAME_USER_COLLECTION.remove(user.getName());
    }

    public static void updateUsername(User user, String newUsername) {
        Validate.notNull(user, "user can't be null!");

        BY_NAME_USER_COLLECTION.remove(user.getName());
        BY_NAME_USER_COLLECTION.put(newUsername, user);

        user.setName(newUsername);
    }

    public static boolean playedBefore(String nickname) {
        return playedBefore(nickname, false);
    }

    public static boolean playedBefore(String nickname, boolean ignoreCase) {
        if (ignoreCase) {
            if (nickname != null) {
                for (String userNickname : BY_NAME_USER_COLLECTION.keySet()) {
                    if (userNickname.equalsIgnoreCase(nickname)) {
                        return true;
                    }
                }
            }

            return false;
        }
        else {
            return nickname != null && BY_NAME_USER_COLLECTION.containsKey(nickname);
        }
    }

    public static Set<String> getNames(Collection<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toSet());
    }

    public static Set<User> getUsers(Collection<String> names) {
        Set<User> users = new HashSet<>();

        for (String name : names) {
            User user = User.get(name);

            if (user == null) {
                FunnyGuilds.getInstance().getLogger().warning("Corrupted user: " + name);
                continue;
            }

            users.add(user);
        }
        return users;
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

    public static int usersSize() {
        return BY_UUID_USER_COLLECTION.size();
    }

    public static boolean validateUsername(String name) {
        return USERNAME_PATTERN.matcher(name).matches();
    }
}
