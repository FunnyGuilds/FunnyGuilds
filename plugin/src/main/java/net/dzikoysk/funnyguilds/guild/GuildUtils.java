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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GuildUtils {

    private static final Set<Guild> GUILDS = ConcurrentHashMap.newKeySet();

    public static Set<Guild> getGuilds() {
        return new HashSet<>(GUILDS);
    }

    public static int countGuilds() {
        return GUILDS.size();
    }

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
                }
                else if (config.createMaterial != null && config.createMaterial.getLeft() != Material.AIR) {
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

        guild.delete();
    }

    public static void spawnHeart(Guild guild) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (config.createMaterial != null && config.createMaterial.getLeft() != Material.AIR) {
            Block heart = guild.getRegion().getCenter().getBlock().getRelative(BlockFace.DOWN);

            heart.setType(config.createMaterial.getLeft());
            BlockDataChanger.applyChanges(heart, config.createMaterial.getRight());
        }
        else if (config.createEntityType != null) {
            GuildEntityHelper.spawnGuildHeart(guild);
        }
    }

    public static Guild getByName(String name) {
        for (Guild guild : GUILDS) {
            if (guild.getName() != null && guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }

        return null;
    }

    public static Guild getByUUID(UUID uuid) {
        for (Guild guild : GUILDS) {
            if (guild.getUUID().equals(uuid)) {
                return guild;
            }
        }

        return null;
    }

    public static Guild getByTag(String tag) {
        for (Guild guild : GUILDS) {
            if (guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag.toLowerCase())) {
                return guild;
            }
        }

        return null;
    }

    public static boolean nameExists(String name) {
        for (Guild guild : GUILDS) {
            if (guild.getName() != null && guild.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean tagExists(String tag) {
        for (Guild guild : GUILDS) {
            if (guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag)) {
                return true;
            }
        }

        return false;
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

    public static Set<Guild> getGuilds(Collection<String> names) {
        return names.stream()
                .map(GuildUtils::getByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static synchronized void addGuild(Guild guild) {
        if (guild == null) {
            return;
        }

        if (getByName(guild.getName()) == null) {
            GUILDS.add(guild);
        }
    }

    public static synchronized void removeGuild(Guild guild) {
        GUILDS.remove(guild);
    }

    public static boolean isNameValid(String guildName) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        return config.whitelist == config.restrictedGuildNames.stream().anyMatch(name -> name.equalsIgnoreCase(guildName));
    }

    public static boolean isTagValid(String guildTag) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        return config.whitelist == config.restrictedGuildTags.stream().anyMatch(tag -> tag.equalsIgnoreCase(guildTag));
    }

    private GuildUtils() {}

}
