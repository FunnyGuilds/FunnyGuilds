package net.dzikoysk.funnyguilds.nms.heart;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.nms.api.NmsAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import panda.std.Option;

public class GuildEntityHelper {

    private final PluginConfiguration pluginConfiguration;
    private final NmsAccessor nmsAccessor;

    private final Map<Guild, FakeEntity> entityMap = new ConcurrentHashMap<>();

    public GuildEntityHelper(PluginConfiguration pluginConfiguration, NmsAccessor nmsAccessor) {
        this.pluginConfiguration = pluginConfiguration;
        this.nmsAccessor = nmsAccessor;
    }

    public Map<Guild, FakeEntity> getGuildEntities() {
        return entityMap;
    }

    public Option<FakeEntity> getOrCreateGuildEntity(Guild guild) {
        return Option.of(entityMap.computeIfAbsent(guild, key -> {
            if (this.pluginConfiguration.heart.createEntityType == null) {
                return null;
            }

            Option<Location> locationOption = guild.getEnderCrystal();
            if (locationOption.isEmpty()) {
                return null;
            }

            return nmsAccessor.getEntityAccessor().createFakeEntity(pluginConfiguration.heart.createEntityType, locationOption.get());
        }));
    }

    public void spawnGuildEntity(Guild guild) {
        this.getOrCreateGuildEntity(guild).peek(entity -> nmsAccessor.getEntityAccessor().spawnFakeEntityFor(entity, Bukkit.getOnlinePlayers()));
    }

    public void spawnGuildEntities(GuildManager guildManager) {
        guildManager.getGuilds()
                .forEach(this::spawnGuildEntity);
    }

    public void spawnGuildEntity(Guild guild, Player player) {
        this.getOrCreateGuildEntity(guild).peek(entity -> nmsAccessor.getEntityAccessor().spawnFakeEntityFor(entity, player));
    }

    public void despawnGuildEntity(Guild guild) {
        FakeEntity guildHeartEntity = entityMap.get(guild);
        if (guildHeartEntity == null) {
            return;
        }

        nmsAccessor.getEntityAccessor().despawnFakeEntityFor(guildHeartEntity, Bukkit.getOnlinePlayers());
        entityMap.remove(guild);
    }

    public void despawnGuildEntities(GuildManager guildManager) {
        guildManager.getGuilds()
                .forEach(this::despawnGuildEntity);
    }
}
