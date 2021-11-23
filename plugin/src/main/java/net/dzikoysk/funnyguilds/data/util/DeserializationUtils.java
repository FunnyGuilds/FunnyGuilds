package net.dzikoysk.funnyguilds.data.util;

import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserBan;
import org.bukkit.Location;

public final class DeserializationUtils {

    @SuppressWarnings("unchecked")
    public static Guild deserializeGuild(FunnyGuilds plugin, Object[] values) {
        if (values == null) {
            FunnyGuilds.getPluginLogger().error("[Deserialize] Cannot deserialize guild! Caused by: null");
            return null;
        }

        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        GuildManager guildManager = plugin.getGuildManager();

        UUID guildUuid = (UUID) values[0];
        String guildName = (String) values[1];
        String rawGuildTag = (String) values[2];
        String guildTag = pluginConfiguration.guildTagKeepCase
                ? rawGuildTag
                : pluginConfiguration.guildTagUppercase
                ? rawGuildTag.toUpperCase()
                : rawGuildTag.toLowerCase();
        Guild guild = guildManager.findByUuid(guildUuid).orElseGet(guildManager.create(guildUuid, guildName, guildTag));

        guild.setOwner((User) values[3]);
        guild.setHome((Location) values[4]);
        guild.setRegion((Region) values[5]);
        guild.setMembers((Set<User>) values[6]);
        guild.setAllies((Set<Guild>) values[7]);
        guild.setEnemies((Set<Guild>) values[8]);
        guild.setBorn((long) values[9]);
        guild.setValidity((long) values[10]);
        guild.setProtection((long) values[11]);
        guild.setLives((int) values[12]);
        guild.setBan((long) values[13]);
        guild.setDeputies((Set<User>) values[14]);
        guild.setPvP((boolean) values[15]);
        guild.deserializationUpdate();

        return guild;
    }

    public static Region deserializeRegion(Object[] values) {
        if (values == null) {
            FunnyGuilds.getPluginLogger().error("Cannot deserialize region! Caused by: null");
            return null;
        }

        Region region = Region.getOrCreate((String) values[0]);

        region.setCenter((Location) values[1]);
        region.setSize((int) values[2]);
        region.setEnlarge((int) values[3]);
        region.update();

        return region;
    }

    public static User deserializeUser(Object[] values) {
        UUID playerUniqueId = UUID.fromString((String) values[0]);
        String playerName = (String) values[1];

        User user = FunnyGuilds.getInstance().getUserManager().create(playerUniqueId, playerName);

        user.getRank().setPoints((int) values[2]);
        user.getRank().setKills((int) values[3]);
        user.getRank().setDeaths((int) values[4]);
        user.getRank().setAssists((int) values[5]);
        user.getRank().setLogouts((int) values[6]);

        long banTime = (long) values[7];

        if (banTime > 0) {
            user.setBan(new UserBan((String) values[8], banTime));
        }

        return user;
    }

    private DeserializationUtils() {
    }

}
