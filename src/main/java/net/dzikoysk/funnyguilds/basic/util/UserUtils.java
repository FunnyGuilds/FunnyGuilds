package net.dzikoysk.funnyguilds.basic.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UserUtils {

    private final static Map<UUID, User> uuidUserMap = new HashMap<>();
    private final static Map<String, User> nameUserMap = new HashMap<>();

    private final static Cache<UUID, User> uuidUserCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private final static Cache<String, User> nameUserCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public static List<User> getUsers() {
        return new ArrayList<>(uuidUserMap.values());
    }

    public static User get(String nickname) {
        return get(nickname, false);
    }

    public static User get(String nickname, boolean ignoreCase) {
        if (ignoreCase) {

            for (Map.Entry<String, User> cacheEntry : nameUserCache.asMap().entrySet()) {
                if (cacheEntry.getKey().equalsIgnoreCase(nickname)) {
                    return cacheEntry.getValue();
                }
            }

            for (Map.Entry<String, User> entry : nameUserMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(nickname)) {
                    return entry.getValue();
                }
            }

            return null;
        }
        else {
            User user = nameUserCache.getIfPresent(nickname);

            if (user == null) {
                user = nameUserMap.get(nickname);

                if (user != null) {
                    nameUserCache.put(nickname, user);
                    return user;
                }

                return null;
            }

            return user;
        }
    }

    public static User get(UUID uuid) {
        User user = uuidUserCache.getIfPresent(uuid);

        if (user == null) {
            user = uuidUserMap.get(uuid);

            if (user != null) {
                uuidUserCache.put(uuid, user);
                return user;
            }

            return null;
        }

        return user;
    }

    public static void addUser(User user) {
        uuidUserMap.put(user.getUUID(), user);
        uuidUserCache.put(user.getUUID(), user);

        if (user.getName() != null) {
            nameUserMap.put(user.getName(), user);
            nameUserCache.put(user.getName(), user);
        }
    }

    public static void removeUser(User user) {
        uuidUserCache.invalidate(user.getUUID());
        nameUserCache.invalidate(user.getName());

        uuidUserMap.remove(user.getUUID());
        nameUserMap.remove(user.getName());
    }

    public static boolean playedBefore(String nickname) {
        return playedBefore(nickname, false);
    }

    public static boolean playedBefore(String nickname, boolean ignoreCase) {
        if (ignoreCase) {
            if (nickname != null) {
                for (String userNickname : nameUserMap.keySet()) {
                    if (userNickname.equalsIgnoreCase(nickname)) {
                        return true;
                    }
                }
            }

            return false;
        }
        else {
            return nickname != null && nameUserMap.containsKey(nickname);
        }
    }

    public static List<String> getNames(List<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toList());
    }

    public static List<User> getUsers(List<String> names) {
        return names.stream().map(User::get).collect(Collectors.toList());
    }

    public static List<String> getOnlineNames(List<User> users) {
        List<String> list = new ArrayList<>();

        for (User user : users) {
            list.add(user.isOnline() ? "<online>" + user.getName() + "</online>" : user.getName());
        }

        return list;
    }

    public static void removeGuild(List<User> users) {
        for (User user : users) {
            user.removeGuild();
        }
    }

    public static void setGuild(List<User> users, Guild guild) {
        for (User user : users) {
            user.setGuild(guild);
        }
    }

    public static int usersSize() {
        return uuidUserMap.size();
    }

}
