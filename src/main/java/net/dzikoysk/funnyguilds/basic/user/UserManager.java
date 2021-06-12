package net.dzikoysk.funnyguilds.basic.user;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import org.apache.commons.lang3.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private final Map<UUID, User> usersByUuid = new ConcurrentHashMap<>();
    private final Map<String, User> usersByName = new ConcurrentHashMap<>();

    @Deprecated
    private static UserManager instance;

    public UserManager() {
        instance = this;
    }

    public Set<User> getUsers() {
        return new HashSet<>(usersByUuid.values());
    }

    public Set<User> getUsers(Collection<String> names) {
        Set<User> users = new HashSet<>();

        for (String name : names) {
            Optional<User> optional = getUser(name);

            if (!optional.isPresent()) {
                FunnyGuilds.getPluginLogger().warning("Corrupted user: " + name);
                continue;
            }

            users.add(optional.get());
        }

        return users;
    }

    public Optional<User> getUser(String nickname) {
        return getUser(nickname, false);
    }

    public Optional<User> getUser(String nickname, boolean ignoreCase) {
        if (ignoreCase) {
            for (Map.Entry<String, User> entry : usersByName.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(nickname)) {
                    return Optional.of(entry.getValue());
                }
            }

            return Optional.empty();
        }

        return Optional.ofNullable(usersByName.get(nickname));
    }

    public Optional<User> getUser(UUID uuid) {
        return Optional.ofNullable(usersByUuid.get(uuid));
    }

    public Optional<User> getUser(Player player) {
        if (player.getUniqueId().version() == 2) {
            return Optional.of(new User(player));
        }

        return getUser(player.getUniqueId());
    }

    public Optional<User> getUser(OfflinePlayer offline) {
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

    /**
     * Gets the user manager.
     *
     * @return the user manager
     * @deprecated for removal in the future, in favour of {@link FunnyGuilds#getUserManager()}
     */
    @Deprecated
    public static UserManager getInstance() {
        return instance;
    }

}
