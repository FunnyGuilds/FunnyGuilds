package net.dzikoysk.funnyguilds.basic.user;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");

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
     * @param nickname the name of User
     * @param ignoreCase ignore the case of the nickname
     * @return the user
     * @deprecated for removal in the future, in favour of {@link UserManager#getUser(String, boolean)}
     */
    @Nullable
    @Deprecated
    public static User get(String nickname, boolean ignoreCase) {
        return UserManager.getInstance().getUser(nickname, ignoreCase).orElse(null);
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
        return UserManager.getInstance().getUser(uuid).orElse(null);
    }

    /**
     * Add the user to UserManager.
     *
     * @param user User to be appended to UserManager.
     * @deprecated for removal in the future, in favour of {@link UserManager#addUser(User)}
     */
    @Deprecated
    public static void addUser(User user) {
        UserManager.getInstance().addUser(user);
    }

    /**
     * Remove the user from UserManager.
     *
     * @param user User to be removed from UserManager, if present
     * @deprecated for removal in the future, in favour of {@link UserManager#removeUser(User)}
     */
    @Deprecated
    public static void removeUser(User user) {
        UserManager.getInstance().removeUser(user);
    }

    /**
     * Updates the user's name.
     *
     * @param user the user
     * @param newUsername the new name to be assigned to user
     * @deprecated for removal in the future, in favour of {@link UserManager#updateUsername(User, String)}
     */
    @Deprecated
    public static void updateUsername(User user, String newUsername) {
        UserManager.getInstance().updateUsername(user, newUsername);
    }

    /**
     * Checks if the player has ever been on the server.
     *
     * @param nickname name of player
     * @return true if the player has played before
     * @deprecated for removal in the future, in favour of {@link UserManager#playedBefore(String)}
     */
    @Deprecated
    public static boolean playedBefore(String nickname) {
        return playedBefore(nickname, false);
    }

    /**
     * Checks if the player has ever been on the server.
     *
     * @param nickname name of player
     * @param ignoreCase ignore the case of the name
     * @return true if the player has played before
     * @deprecated for removal in the future, in favour of {@link UserManager#playedBefore(String, boolean)}
     */
    @Deprecated
    public static boolean playedBefore(String nickname, boolean ignoreCase) {
        return UserManager.getInstance().playedBefore(nickname, ignoreCase);
    }

    public static Set<String> getNamesOfUsers(Collection<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toSet());
    }

    public static Set<User> getUsersFromString(Collection<String> names) {
        Set<User> users = new HashSet<>();

        for (String name : names) {
            User user = User.get(name);

            if (user == null) {
                FunnyGuilds.getPluginLogger().warning("Corrupted user: " + name);
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

    /**
     * Gets the number of users.
     *
     * @return the number of users
     * @deprecated for removal in the future, in favour of {@link UserManager#usersSize()}
     */
    @Deprecated
    public static int usersSize() {
        return UserManager.getInstance().usersSize();
    }

    public static boolean validateUsername(String name) {
        return USERNAME_PATTERN.matcher(name).matches();
    }

}
