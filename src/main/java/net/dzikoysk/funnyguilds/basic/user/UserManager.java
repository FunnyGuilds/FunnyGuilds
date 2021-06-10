package net.dzikoysk.funnyguilds.basic.user;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import org.apache.commons.lang3.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private final Map<UUID, User> usersByUuid = new ConcurrentHashMap<>();
    private final Map<String, User> usersByName = new ConcurrentHashMap<>();
    private final FunnyGuilds plugin;

    public UserManager(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public Set<User> getUsers() {
        return new HashSet<>(usersByUuid.values());
    }

    public Set<User> getUsers(Collection<String> names) {
        Set<User> users = new HashSet<>();

        for (String name : names) {
            User user = getUser(name);

            if (user == null) {
                FunnyGuilds.getPluginLogger().warning("Corrupted user: " + name);
                continue;
            }

            users.add(user);
        }

        return users;
    }

    public User getUser(String nickname) {
        return getUser(nickname, false);
    }

    public User getUser(String nickname, boolean ignoreCase) {
        if (ignoreCase) {
            for (Map.Entry<String, User> entry : usersByName.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(nickname)) {
                    return entry.getValue();
                }
            }

            return null;
        }

        return usersByName.get(nickname);
    }

    public User getUser(UUID uuid) {
        return usersByUuid.get(uuid);
    }

    public User getUser(Player player) {
        if (player.getUniqueId().version() == 2) {
            return new User(player);
        }

        return getUser(player.getUniqueId());
    }

    public User getUser(OfflinePlayer offline) {
        return getUser(offline.getName());
    }

    public User create(UUID uuid, String name) {
        Validate.notNull(uuid, "uuid can't be null!");
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(UserUtils.validateUsername(name), "name is not valid!");

        User user = new User(uuid, name);

        addUser(user);
        RankManager.getInstance().update(user);

        return user;
    }

    public User create(Player player) {
        Validate.notNull(player, "player can't be null!");

        User user = new User(player);
        addUser(user);
        RankManager.getInstance().update(user);

        return user;
    }

    public void addUser(User user) {
        Validate.notNull(user, "user can't be null!");

        usersByUuid.put(user.getUUID(), user);
        usersByName.put(user.getName(), user);
    }

    public void removeUser(User user) {
        Validate.notNull(user, "user can't be null!");

        usersByUuid.remove(user.getUUID());
        usersByName.remove(user.getName());
    }

    public void updateUsername(User user, String newUsername) {
        Validate.notNull(user, "user can't be null!");

        usersByName.remove(user.getName());
        usersByName.put(newUsername, user);

        user.setName(newUsername);
    }

    public boolean playedBefore(String nickname) {
        return playedBefore(nickname, false);
    }

    public boolean playedBefore(String nickname, boolean ignoreCase) {
        if (nickname == null) {
            return false;
        }

        if (ignoreCase) {
            for (String userNickname : usersByName.keySet()) {
                if (userNickname.equalsIgnoreCase(nickname)) {
                    return true;
                }
            }

            return false;
        }

        return usersByName.containsKey(nickname);
    }

    public int usersSize() {
        return usersByUuid.size();
    }

}
