package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemoveGuildRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.nms.BlockDataChanger;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuildUtils {

    @Deprecated
    public static Set<Guild> getGuilds() {
        return FunnyGuilds.getInstance().getGuildManager().getGuilds();
    }

    @Deprecated
    public static int countGuilds() {
        return FunnyGuilds.getInstance().getGuildManager().countGuilds();
    }

    @Deprecated
    public static void deleteGuild(Guild guild) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (guild == null) {
            return;
        }

        if (config.regionsEnabled) {
            Region region = guild.getRegion();

            if (region != null) {
                if (config.createEntityType != null) {
                    GuildEntityHelper.despawnGuildHeart(guild);
                } else if (config.createMaterial != null && config.createMaterial.getLeft() != Material.AIR) {
                    Location centerLocation = region.getCenter().clone();

                    Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), () -> {
                        Block block = centerLocation.getBlock().getRelative(BlockFace.DOWN);

                        if (block.getLocation().getBlockY() > 1) {
                            block.setType(Material.AIR);
                        }
                    });
                }
            }

            RegionUtils.delete(guild.getRegion());
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemoveGuildRequest(guild));

        guild.getMembers().forEach(User::removeGuild);

        for (Guild ally : guild.getAllies()) {
            ally.removeAlly(guild);
        }

        for (Guild globalGuild : GuildUtils.getGuilds()) {
            globalGuild.removeEnemy(guild);
        }

        if (FunnyGuilds.getInstance().getDataModel() instanceof FlatDataModel) {
            FlatDataModel dataModel = ((FlatDataModel) FunnyGuilds.getInstance().getDataModel());
            dataModel.getGuildFile(guild).delete();
        }
        else if (FunnyGuilds.getInstance().getDataModel() instanceof SQLDataModel) {
            DatabaseGuild.delete(guild);
        }

        FunnyGuilds.getInstance().getGuildManager().removeGuild(guild);
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
    public static Set<Guild> getGuilds(Collection<String> names) {
        return FunnyGuilds.getInstance().getGuildManager().getGuildsByNames(names);
    }

    @Deprecated
    public static void addGuild(Guild guild) {
        FunnyGuilds.getInstance().getGuildManager().addGuild(guild);
    }

    @Deprecated
    public static void removeGuild(Guild guild) {
        FunnyGuilds.getInstance().getGuildManager().removeGuild(guild);
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

    public static void spawnHeart(PluginConfiguration pluginConfiguration, Guild guild) {
        if (pluginConfiguration.createMaterial != null && pluginConfiguration.createMaterial.getLeft() != Material.AIR) {
            Block heart = guild.getRegion().getCenter().getBlock().getRelative(BlockFace.DOWN);

            heart.setType(pluginConfiguration.createMaterial.getLeft());
            BlockDataChanger.applyChanges(heart, pluginConfiguration.createMaterial.getRight());
        } else if (pluginConfiguration.createEntityType != null) {
            GuildEntityHelper.spawnGuildHeart(guild);
        }
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
