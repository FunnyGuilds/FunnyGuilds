package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemoveGuildRequest;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildUtils {

    private static final List<Guild> guilds = new ArrayList<>();

    public static List<Guild> getGuilds() {
        return new ArrayList<>(guilds);
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

    public static List<String> getNames(List<Guild> lsg) {
        List<String> list = new ArrayList<>();
        if (lsg == null) {
            return list;
        }

        for (Guild g : lsg) {
            if (g == null) {
                continue;
            }

            if (g.getName() != null) {
                list.add(g.getName());
            }
        }

        return list;
    }

    public static List<String> getTags(List<Guild> lsg) {
        if (lsg == null) {
            return null;
        }

        List<String> list = new ArrayList<>();
        for (Guild g : lsg) {
            if (g.getName() != null) {
                list.add(g.getTag());
            }
        }

        return list;
    }

    public static List<Guild> getGuilds(List<String> names) {
        if (names == null) {
            return null;
        }

        List<Guild> list = new ArrayList<>();

        for (String name : names) {
            Guild guild = GuildUtils.getByName(name);

            if (guild != null) {
                list.add(guild);
            }
        }

        return list;
    }

    public static void addGuild(Guild guild) {
        if (guild == null || guild.getName() == null) {
            return;
        }

        if (getByName(guild.getName()) == null) {
            guilds.add(guild);
        }
    }

    public static void removeGuild(Guild guild) {
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
