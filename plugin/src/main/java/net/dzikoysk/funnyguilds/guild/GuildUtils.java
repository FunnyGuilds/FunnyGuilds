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

    @Deprecated
    public static Set<Guild> getGuilds() {
        return FunnyGuilds.getInstance().getGuildManager().getGuilds();
    }

    @Deprecated
    public static Set<Guild> getGuilds(Collection<String> names) {
        return FunnyGuilds.getInstance().getGuildManager().getGuildsByNames(names);
    }

    @Nullable
    @Deprecated
    public static Guild getByName(String name) {
        return FunnyGuilds.getInstance().getGuildManager().getGuildByName(name).getOrNull();
    }

    @Nullable
    @Deprecated
    public static Guild getByUUID(UUID uuid) {
        return FunnyGuilds.getInstance().getGuildManager().getGuild(uuid).getOrNull();
    }

    @Nullable
    @Deprecated
    public static Guild getByTag(String tag) {
        return FunnyGuilds.getInstance().getGuildManager().getGuildByTag(tag).getOrNull();
    }

    @Deprecated
    public static boolean nameExists(String name) {
        return FunnyGuilds.getInstance().getGuildManager().guildNameExists(name);
    }

    @Deprecated
    public static boolean tagExists(String tag) {
        return FunnyGuilds.getInstance().getGuildManager().guildTagExists(tag);
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
