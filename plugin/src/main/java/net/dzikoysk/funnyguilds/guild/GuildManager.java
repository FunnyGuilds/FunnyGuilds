package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemoveGuildRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.feature.hooks.PluginHook;
import net.dzikoysk.funnyguilds.nms.BlockDataChanger;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class GuildManager {

    private final FunnyGuilds plugin;

    private final PluginConfiguration pluginConfiguration;

    private final Map<UUID, Guild> guildsMap = new ConcurrentHashMap<>();

    public GuildManager(FunnyGuilds plugin) {
        this.plugin = plugin;

        this.pluginConfiguration = plugin.getPluginConfiguration();
    }

    public int countGuilds() {
        return this.guildsMap.entrySet().size();
    }

    public Set<Guild> getGuilds() {
        return new HashSet<>(this.guildsMap.values());
    }

    public Set<Guild> findByNames(Collection<String> names) {
        return PandaStream.of(names)
                .flatMap(this::findByName)
                .collect(Collectors.toSet());
    }

    public Set<Guild> findByTags(Collection<String> tags) {
        return PandaStream.of(tags)
                .flatMap(this::findByTag)
                .collect(Collectors.toSet());
    }

    public Option<Guild> findByUuid(UUID uuid) {
        return Option.of(guildsMap.get(uuid));
    }

    public Option<Guild> findByName(String name, boolean ignoreCase) {
        return PandaStream.of(guildsMap.entrySet())
                .find(entry -> ignoreCase
                        ? entry.getValue().getName().equalsIgnoreCase(name)
                        : entry.getValue().getName().equals(name))
                .map(Map.Entry::getValue);
    }

    public Option<Guild> findByName(String name) {
        return this.findByName(name, false);
    }

    public Option<Guild> findByTag(String tag, boolean ignoreCase) {
        return PandaStream.of(guildsMap.entrySet())
                .find(entry -> ignoreCase
                        ? entry.getValue().getTag().equalsIgnoreCase(tag)
                        : entry.getValue().getTag().equals(tag))
                .map(Map.Entry::getValue);
    }

    public Option<Guild> findByTag(String tag) {
        return this.findByTag(tag, false);
    }

    public Guild create(String name) {
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(GuildUtils.validateName(pluginConfiguration, name), "name is not valid!");

        Guild guild = new Guild(name);
        addGuild(guild);

        return guild;
    }

    public Guild create(String name, String tag) {
        Validate.notNull(tag, "tag can't be null!");
        Validate.notBlank(tag, "tag can't be blank!");
        Validate.isTrue(GuildUtils.validateTag(pluginConfiguration, tag), "tag is not valid!");

        Guild guild = create(name);
        guild.setTag(tag);

        return guild;
    }

    public void addGuild(Guild guild) {
        Validate.notNull(guild, "guild can't be null!");
        guildsMap.put(guild.getUUID(), guild);
    }

    public void removeGuild(Guild guild) {
        Validate.notNull(guild, "user can't be null!");
        guildsMap.remove(guild.getUUID());
    }

    public void deleteGuild(Guild guild) {
        if (guild == null) {
            return;
        }

        if (this.pluginConfiguration.regionsEnabled) {
            Region region = guild.getRegion();

            if (region != null) {
                if (this.pluginConfiguration.heart.createEntityType != null) {
                    GuildEntityHelper.despawnGuildHeart(guild);
                }
                else if (this.pluginConfiguration.heart.createMaterial != null && this.pluginConfiguration.heart.createMaterial.getLeft() != Material.AIR) {
                    Location centerLocation = region.getCenter().clone();

                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        Block block = centerLocation.getBlock().getRelative(BlockFace.DOWN);

                        if (block.getLocation().getBlockY() > 1) {
                            block.setType(Material.AIR);
                        }
                    });
                }
            }

            RegionUtils.delete(guild.getRegion());
        }

        this.plugin.getConcurrencyManager().postRequests(new PrefixGlobalRemoveGuildRequest(guild));

        guild.getMembers().forEach(User::removeGuild);

        for (Guild ally : guild.getAllies()) {
            ally.removeAlly(guild);
        }

        for (Guild globalGuild : getGuilds()) {
            globalGuild.removeEnemy(guild);
        }

        if (this.plugin.getDataModel() instanceof FlatDataModel) {
            FlatDataModel dataModel = ((FlatDataModel) this.plugin.getDataModel());
            dataModel.getGuildFile(guild).delete();
        }
        else if (this.plugin.getDataModel() instanceof SQLDataModel) {
            DatabaseGuild.delete(guild);
        }

        removeGuild(guild);
        PluginHook.HOLOGRAPHIC_DISPLAYS.deleteHologram(guild);
    }

    public boolean nameExists(String name) {
        return !this.findByName(name).isEmpty();
    }

    public boolean tagExists(String tag) {
        return !this.findByTag(tag).isEmpty();
    }

    public void spawnHeart(Guild guild) {
        if (this.pluginConfiguration.heart.createMaterial != null && this.pluginConfiguration.heart.createMaterial.getLeft() != Material.AIR) {
            Block heart = guild.getRegion().getCenter().getBlock().getRelative(BlockFace.DOWN);

            heart.setType(this.pluginConfiguration.heart.createMaterial.getLeft());
            BlockDataChanger.applyChanges(heart, this.pluginConfiguration.heart.createMaterial.getRight());
        }
        else if (this.pluginConfiguration.heart.createEntityType != null) {
            GuildEntityHelper.spawnGuildHeart(guild);
        }
    }

}
