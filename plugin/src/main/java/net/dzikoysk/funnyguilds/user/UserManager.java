package net.dzikoysk.funnyguilds.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.apache.commons.lang3.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class UserManager {

    private final Map<UUID, User> usersByUuid = new ConcurrentHashMap<>();
    private final Map<String, User> usersByName = new ConcurrentHashMap<>();

    @Deprecated
    private static UserManager INSTANCE;

    public UserManager() {
        INSTANCE = this;
    }

    public int countUsers() {
        return this.usersByUuid.size();
    }

    /**
     * Gets the copied set of users.
     *
     * @return set of users
     */
    public Set<User> getUsers() {
        return new HashSet<>(this.usersByUuid.values());
    }

    /**
     * Gets the set of users from collection of strings (names).
     *
     * @param names collection of names
     * @return set of users
     */
    public Set<User> findByNames(Collection<String> names) {
        return PandaStream.of(names)
                .flatMap(this::findByName)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the user.
     *
     * @param uuid the universally unique identifier of user
     * @return the user
     */
    public Option<User> findByUuid(UUID uuid) {
        return Option.of(usersByUuid.get(uuid));
    }

    /**
     * Gets the user.
     *
     * @param nickname the name of user
     * @return the user
     */
    public Option<User> findByName(String nickname) {
        return findByName(nickname, false);
    }

    /**
     * Gets the user.
     *
     * @param nickname the name of user
     * @param ignoreCase ignore the case of the nickname
     * @return the user
     */
    public Option<User> findByName(String nickname, boolean ignoreCase) {
        if (ignoreCase) {
            return PandaStream.of(usersByName.entrySet())
                    .find(entry -> entry.getKey().equalsIgnoreCase(nickname))
                    .map(Map.Entry::getValue);
        }

        return Option.of(usersByName.get(nickname));
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public Option<User> findByPlayer(@NotNull Player player) {
        if (player.getUniqueId().version() == 2) {
            return Option.of(new User(player));
        }

        return findByUuid(player.getUniqueId());
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public Option<User> findByPlayer(OfflinePlayer offlinePlayer) {
        return findByUuid(offlinePlayer.getUniqueId());
    }

    /**
     * Create the user and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link UserManager#findByUuid(UUID)}, {@link UserManager#findByName(String)} etc.
     *
     * @param uuid the universally unique identifier which will be assigned to user
     * @param name the nickname which will be assigned to User
     * @return the user
     */
    public User create(UUID uuid, String name) {
        Validate.notNull(uuid, "uuid can't be null!");
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(UserUtils.validateUsername(name), "name is not valid!");

        User user = new User(uuid, name);
        addUser(user);

        return user;
    }

    /**
     * Create the user and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link UserManager#findByPlayer(Player)}.
     *
     * @return the user
     */
    public User create(Player player) {
        Validate.notNull(player, "player can't be null!");

        User user = new User(player);
        addUser(user);

        return user;
    }

    /**
     * Add user to storage. If you think you should use this method you probably shouldn't.
     *
     * @param user the user to addition
     */
    public void addUser(User user) {
        Validate.notNull(user, "user can't be null!");

        usersByUuid.put(user.getUUID(), user);
        usersByName.put(user.getName(), user);
    }

    /**
     * Remove user from storage. If you think you should use this method you probably shouldn't.
     *
     * @param user the user to removal
     */
    public void removeUser(User user) {
        Validate.notNull(user, "user can't be null!");

        usersByUuid.remove(user.getUUID());
        usersByName.remove(user.getName());
    }

    /**
     * Update username for user.
     *
     * @param user the user for which the nickname will be changed
     * @param newUsername the new nickname for user
     */
    public void updateUsername(User user, String newUsername) {
        Validate.notNull(user, "user can't be null!");

        usersByName.remove(user.getName());
        usersByName.put(newUsername, user);

        user.setName(newUsername);
    }

    /**
     * Checks if user with given nickname have ever played on a server.
     *
     * @param nickname the nickname of user to check if ever played on
     * @return if user with given name have ever played on a server
     */
    public boolean playedBefore(String nickname) {
        return playedBefore(nickname, false);
    }

    /**
     * Checks if user with given nickname have ever played on a server.
     *
     * @param nickname the nickname of user to check if ever played on
     * @param ignoreCase ignore the case of the nickname
     * @return if user with given name have ever played on a server
     */
    public boolean playedBefore(String nickname, boolean ignoreCase) {
        return findByName(nickname, ignoreCase).isPresent();
    }

    /**
     * Gets the user manager.
     *
     * @return the user manager
     * @deprecated for removal in the future, in favour of {@link FunnyGuilds#getUserManager()}
     */
    @Deprecated
    public static UserManager getInstance() {
        return INSTANCE;
    }

}
