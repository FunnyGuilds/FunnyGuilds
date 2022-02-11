package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import org.jetbrains.annotations.ApiStatus;

public final class GuildUtils {

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static int countGuilds() {
        return FunnyGuilds.getInstance().getGuildManager().countGuilds();
    }

    /**
     * Gets the copied set of guilds.
     *
     * @return set of guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#getGuilds()}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Set<Guild> getGuilds() {
        return FunnyGuilds.getInstance().getGuildManager().getGuilds();
    }

    /**
     * Gets the set of guilds from collection of strings (names).
     *
     * @param names collection of names
     * @return set of guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#findByNames(Collection)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Set<Guild> getGuilds(Collection<String> names) {
        return FunnyGuilds.getInstance().getGuildManager().findByNames(names);
    }

    /**
     * Gets the guild.
     *
     * @param uuid the uuid of guild
     * @return the guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#findByUuid(UUID)}
     */
    @Nullable
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Guild getByUUID(UUID uuid) {
        return FunnyGuilds.getInstance().getGuildManager().findByUuid(uuid).getOrNull();
    }

    /**
     * Gets the guild.
     *
     * @param name the name of guild
     * @return the guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#findByName(String)}
     */
    @Nullable
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Guild getByName(String name) {
        return FunnyGuilds.getInstance().getGuildManager().findByName(name).getOrNull();
    }

    /**
     * Gets the guild.
     *
     * @param tag the tag of guild
     * @return the guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#findByTag(String)}
     */
    @Nullable
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static Guild getByTag(String tag) {
        return FunnyGuilds.getInstance().getGuildManager().findByTag(tag).getOrNull();
    }

    /**
     * Check if guild name is taken.
     *
     * @param name the name to check
     * @return the result
     * @deprecated for removal in the future, in favour of {@link GuildManager#nameExists(String)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static boolean nameExists(String name) {
        return FunnyGuilds.getInstance().getGuildManager().nameExists(name);
    }

    /**
     * Check if guild tag is taken.
     *
     * @param tag the tag to check
     * @return the result
     * @deprecated for removal in the future, in favour of {@link GuildManager#nameExists(String)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static boolean tagExists(String tag) {
        return FunnyGuilds.getInstance().getGuildManager().tagExists(tag);
    }

    /**
     * Add guild to storage. If you think you should use this method you probably shouldn't.
     *
     * @param guild guild to add
     * @deprecated for removal in the future, in favour of {@link GuildManager#addGuild(Guild)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void addGuild(Guild guild) {
        FunnyGuilds.getInstance().getGuildManager().addGuild(guild);
    }

    /**
     * Remove guild from storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#deleteGuild(Guild)}.
     *
     * @param guild guild to remove
     * @deprecated for removal in the future, in favour of {@link GuildManager#removeGuild(Guild)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void removeGuild(Guild guild) {
        FunnyGuilds.getInstance().getGuildManager().removeGuild(guild);
    }

    /**
     * Delete guild in every possible way.
     *
     * @param guild guild to delete
     * @deprecated for removal in the future, in favour of {@link GuildManager#deleteGuild(FunnyGuilds, Guild)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
    public static void deleteGuild(Guild guild) {
        FunnyGuilds.getInstance().getGuildManager().deleteGuild(FunnyGuilds.getInstance(), guild);
    }

    /**
     * Gets the set of guild tags from collection of guild.
     *
     * @param guilds collection of users
     * @return set of guild tags
     */
    public static Set<String> getTags(Collection<Guild> guilds) {
        return guilds.stream()
                .filter(Objects::nonNull)
                .map(Guild::getTag)
                .collect(Collectors.toSet());
    }

    /**
     * Validate guild name.
     *
     * @param pluginConfiguration the PluginConfiguration from which pattern will be used
     * @param guildName           guild name to validate
     * @return if guild name is valid
     */
    public static boolean validateName(PluginConfiguration pluginConfiguration, String guildName) {
        return pluginConfiguration.whitelist == pluginConfiguration.restrictedGuildNames.stream()
                .anyMatch(name -> name.equalsIgnoreCase(guildName));
    }

    /**
     * Validate guild tag.
     *
     * @param pluginConfiguration the PluginConfiguration from which pattern will be used
     * @param guildTag            guild tag to validate
     * @return if guild tag is valid
     */
    public static boolean validateTag(PluginConfiguration pluginConfiguration, String guildTag) {
        return pluginConfiguration.whitelist == pluginConfiguration.restrictedGuildTags.stream()
                .anyMatch(tag -> tag.equalsIgnoreCase(guildTag));
    }

    private GuildUtils() {
    }

}
