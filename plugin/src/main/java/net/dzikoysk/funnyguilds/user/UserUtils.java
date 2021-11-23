package net.dzikoysk.funnyguilds.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.jetbrains.annotations.ApiStatus;

public final class UserUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    /**
     * Gets the copied set of users.
     *
     * @return set of users
     * @deprecated for removal in the future, in favour of {@link UserManager#getUsers()}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Set<User> getUsers() {
        return UserManager.getInstance().getUsers();
    }

    /**
     * Gets the set of users from collection of strings (names).
     *
     * @param names collection of names
     * @return set of users
     * @deprecated for removal in the future, in favour of {@link UserManager#findByNames(Collection)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Set<User> getUsersFromString(Collection<String> names) {
        UserManager userManager = UserManager.getInstance();
        Set<User> users = new HashSet<>();

        for (String name : names) {
            userManager.findByName(name)
                    .onEmpty(() -> FunnyGuilds.getPluginLogger().warning("Corrupted user: " + name))
                    .peek(users::add);
        }

        return users;
    }

    /**
     * Gets the user.
     *
     * @param uuid the universally unique identifier of user
     * @return the user
     * @deprecated for removal in the future, in favour of {@link UserManager#findByUuid(UUID)}
     */
    @Nullable
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static User get(UUID uuid) {
        return UserManager.getInstance().findByUuid(uuid).getOrNull();
    }

    /**
     * Gets the user.
     *
     * @param nickname the name of user
     * @return the user
     * @deprecated for removal in the future, in favour of {@link UserManager#findByName(String)}
     */
    @Nullable
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static User get(String nickname) {
        return get(nickname, false);
    }

    /**
     * Gets the user.
     *
     * @param nickname the name of user
     * @param ignoreCase ignore the case of the nickname
     * @return the user
     * @deprecated for removal in the future, in favour of {@link UserManager#findByName(String, boolean)}
     */
    @Nullable
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static User get(String nickname, boolean ignoreCase) {
        return UserManager.getInstance().findByName(nickname, ignoreCase).getOrNull();
    }

    /**
     * Gets the set of usernames from collection of users.
     *
     * @param users collection of users
     * @return set of usernames
     */
    public static Set<String> getNamesOfUsers(Collection<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toSet());
    }

    /**
     * Gets the set of usernames (with tags to format) from collection of users.
     *
     * @param users collection of users
     * @return set of usernames (with tags to format)
     */
    public static Set<String> getOnlineNames(Collection<User> users) {
        Set<String> set = new HashSet<>();

        for (User user : users) {
            set.add(user.isOnline() ? "<online>" + user.getName() + "</online>" : user.getName());
        }

        return set;
    }

    /**
     * Validate username.
     *
     * @param name username to validate
     * @return if username is valid
     */
    public static boolean validateUsername(String name) {
        return USERNAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validate universally unique identifier.
     *
     * @param uuid universally unique identifier to validate
     * @return if universally unique identifier is valid
     */
    public static boolean validateUUID(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }

}
