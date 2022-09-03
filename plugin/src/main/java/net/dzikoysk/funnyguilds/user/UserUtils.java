package net.dzikoysk.funnyguilds.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.shared.FunnyValidator;
import net.dzikoysk.funnyguilds.shared.FunnyValidator.NameResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import panda.std.Option;
import panda.std.Result;

public final class UserUtils {

    private UserUtils() {
    }

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
     * @param nickname   the name of user
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

    public static String getUserPosition(PluginConfiguration pluginConfiguration, @Nullable User user) {
        if (user == null) {
            return "";
        }

        if (user.isOwner()) {
            return pluginConfiguration.chatPositionLeader.getValue();
        }

        if (user.isDeputy()) {
            return pluginConfiguration.chatPositionDeputy.getValue();
        }

        return pluginConfiguration.chatPositionMember.getValue();
    }

    /**
     * Check if user file is correct and if not - try migrating it
     *
     * @param config plugin configuration
     * @param file user file
     * @return A final (source or migrated) user file
     */
    public static Option<File> checkUserFile(PluginConfiguration config, File file) {
        String filenameWithoutExtension = StringUtils.removeEnd(file.getName(), ".yml");
        if (FunnyValidator.validateUUID(filenameWithoutExtension)) {
            return Option.of(file);
        }

        if (FunnyValidator.validateUsername(config, filenameWithoutExtension) != NameResult.VALID) {
            return migrateUserFile(file)
                    .onError(error -> FunnyGuilds.getPluginLogger().error(error))
                    .toOption();
        }

        return Option.none();
    }

    /**
     * Try migrating a user file to a new name
     *
     * @param file user file
     * @return Result with migrated user file or a migration error message
     */
    public static Result<File, String> migrateUserFile(File file) {
        YamlWrapper wrapper = new YamlWrapper(file);
        String id = wrapper.getString("uuid");

        if (id == null || !FunnyValidator.validateUUID(id)) {
            return Result.error("Migration of user file '" + file.getName() + "' failed, UUID is invalid");
        }

        Path source = file.toPath();
        Path target = source.resolveSibling(String.format("%s.yml", id));

        if (Files.exists(target)) {
            return Result.ok(target.toFile());
        }

        return Result.attempt(IOException.class, () -> Files.move(source, target, StandardCopyOption.REPLACE_EXISTING).toFile())
                .mapErr(error -> "Could not move file '" + source + "' to '" + target + "': " + error.getMessage());
    }

}
