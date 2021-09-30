package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import org.apache.commons.lang3.Validate;
import panda.std.Option;
import panda.std.stream.PandaStream;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GuildManager {

    private final PluginConfiguration pluginConfiguration;

    private final Map<UUID, Guild> guildsMap = new ConcurrentHashMap<>();

    public GuildManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    public Set<Guild> getGuilds() {
        return new HashSet<>(this.guildsMap.values());
    }

    public Set<Guild> getGuildsByNames(Collection<String> names) {
        return PandaStream.of(names)
                .flatMap(this::getGuildByName)
                .collect(Collectors.toSet());
    }

    public Set<Guild> getGuildsByTags(Collection<String> tags) {
        return PandaStream.of(tags)
                .flatMap(this::getGuildByTag)
                .collect(Collectors.toSet());
    }

    public Option<Guild> getGuild(UUID uuid) {
        return Option.of(guildsMap.get(uuid));
    }

    public Option<Guild> getGuildByName(String name, boolean ignoreCase) {
        return PandaStream.of(guildsMap.entrySet())
                .find(entry -> {
                    if(ignoreCase) {
                        return entry.getValue().getName().equalsIgnoreCase(name);
                    } else {
                        return entry.getValue().getName().equals(name);
                    }
                })
                .map(Map.Entry::getValue);
    }

    public Option<Guild> getGuildByName(String name) {
        return this.getGuildByName(name, false);
    }

    public Option<Guild> getGuildByTag(String tag, boolean ignoreCase) {
        return PandaStream.of(guildsMap.entrySet())
                .find(entry -> {
                    if(ignoreCase) {
                        return entry.getValue().getTag().equalsIgnoreCase(tag);
                    } else {
                        return entry.getValue().getTag().equals(tag);
                    }
                })
                .map(Map.Entry::getValue);
    }

    public Option<Guild> getGuildByTag(String tag) {
        return this.getGuildByTag(tag, false);
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

    public boolean guildExistsByName(String name) {
        return !this.getGuildByName(name).isEmpty();
    }

    public boolean guildExistsByTag(String tag) {
        return !this.getGuildByTag(tag).isEmpty();
    }

    public int countGuilds() {
        return this.guildsMap.entrySet().size();
    }

}
