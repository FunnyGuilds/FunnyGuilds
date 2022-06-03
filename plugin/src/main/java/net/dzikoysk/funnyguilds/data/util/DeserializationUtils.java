package net.dzikoysk.funnyguilds.data.util;

import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.user.BukkitUserProfile;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserBan;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import panda.std.Option;

public final class DeserializationUtils {

    private DeserializationUtils() {
    }

    public static Option<User> deserializeUser(UserManager userManager, Object[] values) {
        UUID playerUniqueId = UUID.fromString((String) values[0]);
        String playerName = (String) values[1];

        UserProfile profile = new BukkitUserProfile(playerUniqueId, Bukkit.getServer()); // :(
        User user = userManager.create(playerUniqueId, playerName, profile);

        user.getRank().setPoints((int) values[2]);
        user.getRank().setKills((int) values[3]);
        user.getRank().setDeaths((int) values[4]);
        user.getRank().setAssists((int) values[5]);
        user.getRank().setLogouts((int) values[6]);

        long banTime = (long) values[7];
        if (banTime > 0) {
            user.setBan(new UserBan((String) values[8], banTime));
        }

        return Option.of(user);
    }

    @SuppressWarnings("unchecked")
    public static Option<Guild> deserializeGuild(PluginConfiguration pluginConfiguration, GuildManager guildManager, Object[] values) {
        if (values == null) {
            FunnyGuilds.getPluginLogger().deserialize("Cannot deserialize guild, caused by: null");
            return Option.none();
        }

        UUID guildUuid = (UUID) values[0];
        String guildName = (String) values[1];
        String rawGuildTag = (String) values[2];
        String guildTag = pluginConfiguration.guildTagKeepCase
                ? rawGuildTag
                : pluginConfiguration.guildTagUppercase
                ? rawGuildTag.toUpperCase()
                : rawGuildTag.toLowerCase();

        Guild guild = guildManager.findByUuid(guildUuid).orElseGet(() -> {
            Guild newGuild = new Guild(guildUuid, guildName, guildTag);
            guildManager.addGuild(newGuild);
            return newGuild;
        });

        guild.setOwner((User) values[3]);
        guild.setHome((Location) values[4]);

        if (pluginConfiguration.regionsEnabled) {
            guild.setRegion((Region) values[5]);
        }

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

        return Option.of(guild);
    }

    public static Option<Region> deserializeRegion(RegionManager regionManager, Object[] values) {
        if (values == null) {
            FunnyGuilds.getPluginLogger().deserialize("Cannot deserialize region, caused by: null");
            return Option.none();
        }

        String regionName = (String) values[0];
        Region region = regionManager.findByName(regionName).orElseGet(new Region(regionName, (Location) values[1]));

        region.setSize((int) values[2]);
        region.setEnlarge((int) values[3]);
        region.update();

        return Option.of(region);
    }

}
