package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class GuildUtils {

    @Deprecated
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
    public static Set<Guild> getGuilds() {
        return FunnyGuilds.getInstance().getGuildManager().getGuilds();
    }

    /**
     * Gets the set of guilds from collection of strings.
     *
     * @return set of guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#findByNames(Collection)}
     */
    @Deprecated
    public static Set<Guild> getGuilds(Collection<String> names) {
        return FunnyGuilds.getInstance().getGuildManager().findByNames(names);
    }

    /**
     * Gets the guild.
     *
     * @param uuid the uuid of Guild
     * @return the guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#findByUuid(UUID)}
     */
    @Nullable
    @Deprecated
    public static Guild getByUUID(UUID uuid) {
        return FunnyGuilds.getInstance().getGuildManager().findByUuid(uuid).getOrNull();
    }

    /**
     * Gets the guild.
     *
     * @param name the name of Guild
     * @return the guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#findByName(String)}
     */
    @Nullable
    @Deprecated
    public static Guild getByName(String name) {
        return FunnyGuilds.getInstance().getGuildManager().findByName(name).getOrNull();
    }

    /**
     * Gets the guild.
     *
     * @param tag the name of Guild
     * @return the guild
     * @deprecated for removal in the future, in favour of {@link GuildManager#findByTag(String)}
     */
    @Nullable
    @Deprecated
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
    public static boolean tagExists(String tag) {
        return FunnyGuilds.getInstance().getGuildManager().tagExists(tag);
    }

    @Deprecated
    public static void addGuild(Guild guild) {
        FunnyGuilds.getInstance().getGuildManager().addGuild(guild);
    }

    @Deprecated
    public static void removeGuild(Guild guild) {
        FunnyGuilds.getInstance().getGuildManager().removeGuild(guild);
    }

    @Deprecated
    public static void deleteGuild(Guild guild) {
        FunnyGuilds.getInstance().getGuildManager().deleteGuild(guild);
    }

    public static Set<String> getNames(Collection<Guild> guilds) {
        return guilds.stream()
                .filter(Objects::nonNull)
                .map(Guild::getName)
                .collect(Collectors.toSet());
    }

    public static List<String> getTags(Collection<Guild> guilds) {
        return guilds.stream()
                .filter(Objects::nonNull)
                .map(Guild::getTag)
                .collect(Collectors.toList());
    }

    public static boolean validateName(PluginConfiguration pluginConfiguration, String guildName) {
        return pluginConfiguration.whitelist == pluginConfiguration.restrictedGuildNames.stream()
                .anyMatch(name -> name.equalsIgnoreCase(guildName));
    }

    public static boolean validateTag(PluginConfiguration pluginConfiguration, String guildTag) {
        return pluginConfiguration.whitelist == pluginConfiguration.restrictedGuildTags.stream()
                .anyMatch(tag -> tag.equalsIgnoreCase(guildTag));
    }

    private GuildUtils() {}

}
