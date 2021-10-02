package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class UserUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
    private final static Pattern UUID_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    /**
     * Gets the copied set of users.
     *
     * @return set of users
     * @deprecated for removal in the future, in favour of {@link UserManager#getUsers()}
     */
    @Deprecated
    public static Set<User> getUsers() {
        return UserManager.getInstance().getUsers();
    }

    /**
     * Gets the set of users from collection of strings.
     *
     * @return set of users
     * @deprecated for removal in the future, in favour of {@link UserManager#findByNames(Collection)}
     */
    @Deprecated
    public static Set<User> getUsersFromString(Collection<String> names) {
        UserManager userManager = UserManager.getInstance();
        Set<User> users = new HashSet<>();

        for (String name : names) {
            userManager.getUser(name)
                    .onEmpty(() -> FunnyGuilds.getPluginLogger().warning("Corrupted user: " + name))
                    .peek(users::add);
        }
        return users;
    }

    /**
     * Gets the user.
     *
     * @param nickname the name of User
     * @return the user
     * @deprecated for removal in the future, in favour of {@link UserManager#getUser(String)}
     */
    @Nullable
    @Deprecated
    public static User get(String nickname) {
        return get(nickname, false);
    }

    /**
     * Gets the user.
     *
     * @param nickname   the name of User
     * @param ignoreCase ignore the case of the nickname
     * @return the user
     * @deprecated for removal in the future, in favour of {@link UserManager#getUser(String, boolean)}
     */
    @Nullable
    @Deprecated
    public static User get(String nickname, boolean ignoreCase) {
        return UserManager.getInstance().getUser(nickname, ignoreCase).getOrNull();
    }

    /**
     * Gets the user.
     *
     * @param uuid the universally unique identifier of User
     * @return the user
     * @deprecated for removal in the future, in favour of {@link UserManager#getUser(UUID)}
     */
    @Nullable
    @Deprecated
    public static User get(UUID uuid) {
        return UserManager.getInstance().getUser(uuid).getOrNull();
    }

    public static Set<String> getNamesOfUsers(Collection<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toSet());
    }

    public static Set<String> getOnlineNames(Collection<User> users) {
        Set<String> set = new HashSet<>();

        for (User user : users) {
            set.add(user.isOnline() ? "<online>" + user.getName() + "</online>" : user.getName());
        }

        return set;
    }

    public static boolean validateUsername(String name) {
        return USERNAME_PATTERN.matcher(name).matches();
    }

    public static boolean validateUUID(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }

}
