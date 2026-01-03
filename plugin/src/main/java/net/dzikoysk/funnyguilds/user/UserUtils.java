package net.dzikoysk.funnyguilds.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.permission.GenericGuildPermissions;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker;
import net.dzikoysk.funnyguilds.shared.FunnyValidator;
import net.dzikoysk.funnyguilds.shared.FunnyValidator.NameResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.StringUtils;
import panda.std.Option;
import panda.std.Result;

public final class UserUtils {

    private UserUtils() {
    }

    /**
     * Gets the set of components 
     *
     * @param users collection of users
     * @return list of components with usernames
     */
    public static List<Component> getOnlineNames(Collection<User> users) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        
        boolean respectVanish = config.usersListRespectVanish;
        TextColor onlineColor = config.onlineColor;
        TextColor offlineColor = config.offlineColor;

        return users.stream()
                // TODO: Add sorting
                .<Component>map(user -> {
                    boolean online = user.isOnline();
                    if (online && respectVanish) {
                        online = !user.isVanished();
                    }
                    TextColor applicableColor = online ? onlineColor : offlineColor;
                    return Component.text(user.getName(), applicableColor);
                })
                .toList();
    }

    public static Component getUserPosition(GuildPermissionChecker permissionChecker, @Nullable User user) {
        return Option.of(user)
                .flatMap(User::getGuild)
                .flatMap(guild -> permissionChecker.getPermissionValue(guild, user, GenericGuildPermissions.USER_POSITION))
                .orElseGet(Component.empty());
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
