package net.dzikoysk.funnyguilds.basic.guild;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemoveGuildRequest;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.util.nms.EntityUtil;
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

    private static final Set<Guild> guilds = ConcurrentHashMap.newKeySet();

    public static Set<Guild> getGuilds() {
        return new HashSet<>(guilds);
    }

    public static void deleteGuild(final Guild guild) {
        PluginConfig config = Settings.getConfig();

        if (guild == null) {
            return;
        }

        Manager.getInstance().stop();

        if (config.regionsEnabled) {
            Region region = guild.getRegion();

            if (region != null) {
                if (config.createEntityType != null) {
                    EntityUtil.despawn(guild);
                } else if (config.createMaterial != null && config.createMaterial.getLeft() != Material.AIR) {
                    Location centerLocation = region.getCenter().clone();

                    Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), () -> {
                        Block block = centerLocation.getBlock().getRelative(BlockFace.DOWN);
                        if (block != null && block.getLocation().getBlockY() > 1) {
                            block.setType(Material.AIR);
                        }
                    });
                }
            }

            RegionUtils.delete(guild.getRegion());
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemoveGuildRequest(guild));

        UserUtils.removeGuild(guild.getMembers());
        RankManager.getInstance().remove(guild);

        for (Guild allay : guild.getAllies()) {
            allay.removeAlly(guild);
        }

        for (Guild enemy : guild.getEnemies()) {
            enemy.removeEnemy(guild);
        }

        if (Settings.getConfig().dataType.flat) {
            Flat.getGuildFile(guild).delete();
        }

        if (Settings.getConfig().dataType.mysql) {
            new DatabaseGuild(guild).delete();
        }

        guild.delete();
        Manager.getInstance().start();
    }

    public static Guild getByName(String name) {
        for (Guild guild : guilds) {
            if (guild.getName() != null && guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }

        return null;
    }

    public static Guild getByUUID(UUID uuid) {
        for (Guild guild : guilds) {
            if (guild.getUUID().equals(uuid)) {
                return guild;
            }
        }

        return null;
    }

    public static Guild getByTag(String tag) {
        for (Guild guild : guilds) {
            if (guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag.toLowerCase())) {
                return guild;
            }
        }

        return null;
    }

    public static boolean nameExists(String name) {
        for (Guild guild : guilds) {
            if (guild.getName() != null && guild.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean tagExists(String tag) {
        for (Guild guild : guilds) {
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
            guilds.add(guild);
        }
    }

    public static synchronized void removeGuild(Guild guild) {
        guilds.remove(guild);
    }

    public static boolean isNameValid(String guildName) {
        return Settings.getConfig().restrictedGuildNames.stream().noneMatch(name -> name.equalsIgnoreCase(guildName));
    }

    public static boolean isTagValid(String guildTag) {
        return Settings.getConfig().restrictedGuildTags.stream().noneMatch(tag -> tag.equalsIgnoreCase(guildTag));
    }

    private GuildUtils() {}

}
