package net.dzikoysk.funnyguilds.basic.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

import java.util.*;
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

    public static User get(UUID uuid) {
        User user = uuidUserCache.getIfPresent(uuid);
        if (user == null) {
            user = uuidUserMap.get(uuid);
            if (user != null) {
                uuidUserMap.put(uuid, user);
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

    public static boolean playedBefore(String s) {
        return s != null && get(s) != null;
    }

    public static List<String> getNames(List<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toList());
    }

    public static List<User> getUsers(List<String> names) {
        return names.stream().map(User::get).collect(Collectors.toList());
    }

    public static List<String> getOnlineNames(List<User> users) {
        List<String> list = new ArrayList<>();
        for (User u : users) {
            if (u.isOnline()) {
                list.add("<online>" + u.getName() + "</online>");
            } else {
                list.add(u.getName());
            }
        }
        return list;
    }

    public static void removeGuild(List<User> users) {
        for (User u : users) {
            u.removeGuild();
        }
    }

    public static void setGuild(List<User> users, Guild guild) {
        for (User u : users) {
            u.setGuild(guild);
        }
    }

    public static int usersSize() {
        return uuidUserMap.size();
    }

}
