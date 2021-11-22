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

    /**
     * Gets the copied set of guilds.
     *
     * @return set of guild
     */
    public Set<Guild> getGuilds() {
        return new HashSet<>(this.guildsMap.values());
    }

    /**
     * Gets the set of guilds from collection of strings (names).
     *
     * @param names collection of names
     * @return set of guild
     */
    public Set<Guild> findByNames(Collection<String> names) {
        return PandaStream.of(names)
                .flatMap(this::findByName)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the set of guilds from collection of strings (tags).
     *
     * @param tags collection of tags
     * @return set of guild
     */
    public Set<Guild> findByTags(Collection<String> tags) {
        return PandaStream.of(tags)
                .flatMap(this::findByTag)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the guild.
     *
     * @param uuid the uuid of guild
     * @return the guild
     */
    public Option<Guild> findByUuid(UUID uuid) {
        return Option.of(guildsMap.get(uuid));
    }

    /**
     * Gets the guild.
     *
     * @param name the name of guild
     * @param ignoreCase ignore the case of the name
     * @return the guild
     */
    public Option<Guild> findByName(String name, boolean ignoreCase) {
        return PandaStream.of(guildsMap.entrySet())
                .find(entry -> ignoreCase
                        ? entry.getValue().getName().equalsIgnoreCase(name)
                        : entry.getValue().getName().equals(name))
                .map(Map.Entry::getValue);
    }

    /**
     * Gets the guild.
     *
     * @param name the name of guild
     * @return the guild
     */
    public Option<Guild> findByName(String name) {
        return this.findByName(name, false);
    }

    /**
     * Gets the guild.
     *
     * @param tag the tag of guild
     * @param ignoreCase ignore the case of the tag
     * @return the guild
     */
    public Option<Guild> findByTag(String tag, boolean ignoreCase) {
        return PandaStream.of(guildsMap.entrySet())
                .find(entry -> ignoreCase
                        ? entry.getValue().getTag().equalsIgnoreCase(tag)
                        : entry.getValue().getTag().equals(tag))
                .map(Map.Entry::getValue);
    }

    /**
     * Gets the guild.
     *
     * @param tag the tag of guild
     * @return the guild
     */
    public Option<Guild> findByTag(String tag) {
        return this.findByTag(tag, false);
    }

    /**
     * Create the guild and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#findByUuid(UUID)}, {@link GuildManager#findByName(String)} etc.
     *
     * @param name the name which will be assigned to Guild
     * @return the guild
     */
    public Guild create(String name) {
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(GuildUtils.validateName(pluginConfiguration, name), "name is not valid!");

        Guild guild = new Guild(name);
        addGuild(guild);

        return guild;
    }

    /**
     * Create the guild and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#findByUuid(UUID)}, {@link GuildManager#findByName(String)} etc.
     *
     * @param name the name which will be assigned to Guild
     * @param tag the tag which will be assigned to Guild
     * @return the guild
     */
    public Guild create(String name, String tag) {
        Validate.notNull(tag, "tag can't be null!");
        Validate.notBlank(tag, "tag can't be blank!");
        Validate.isTrue(GuildUtils.validateTag(pluginConfiguration, tag), "tag is not valid!");

        Guild guild = create(name);
        guild.setTag(tag);

        return guild;
    }

    /**
     * Add guild to storage. If you think you should use this method you probably shouldn't.
     *
     * @param guild the guild to addition
     */
    public void addGuild(Guild guild) {
        Validate.notNull(guild, "guild can't be null!");
        guildsMap.put(guild.getUUID(), guild);
    }

    /**
     * Remove guild from storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#deleteGuild(Guild)}.
     *
     * @param guild the guild to removal
     */
    public void removeGuild(Guild guild) {
        Validate.notNull(guild, "user can't be null!");
        guildsMap.remove(guild.getUUID());
    }

    /**
     * Delete guild in every possible way.
     *
     * @param guild the guild to deletion
     */
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

    /**
     * Checks if guild with given name exists.
     *
     * @param name the guild name to check if exists
     * @return if guild with given name exists
     */
    public boolean nameExists(String name) {
        return this.findByName(name).isPresent();
    }

    /**
     * Checks if guild with given tag exists.
     *
     * @param tag the guild tag to check if exists
     * @return if guild with given tag exists
     */
    public boolean tagExists(String tag) {
        return this.findByTag(tag).isPresent();
    }

    /**
     * Spawn heart for guild. You probably shouldn't use this method.
     *
     * @param guild the guild for which heart should be spawned
     */
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
