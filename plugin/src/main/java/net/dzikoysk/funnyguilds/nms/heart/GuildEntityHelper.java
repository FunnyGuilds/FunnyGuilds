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

    public void createGuildEntity(Guild guild) {
        if(this.pluginConfiguration.heart.createEntityType == null) {
            return;
        }

        if (entityMap.containsKey(guild)) {
            return;
        }

        Option<Location> locationOption = guild.getEnderCrystal();
        if (locationOption.isEmpty()) {
            return;
        }

        FakeEntity guildHeartEntity = nmsAccessor.getEntityAccessor()
                .createFakeEntity(pluginConfiguration.heart.createEntityType, locationOption.get());

        Bukkit.getOnlinePlayers()
                .forEach(player -> nmsAccessor.getEntityAccessor().spawnFakeEntityFor(guildHeartEntity, player));
        entityMap.put(guild, guildHeartEntity);
    }

    public void createGuildsEntities(GuildManager guildManager) {
        guildManager.getGuilds()
                .forEach(this::createGuildEntity);
    }

    public void despawnGuildEntity(Guild guild) {
        FakeEntity guildHeartEntity = entityMap.get(guild);
        if (guildHeartEntity == null) {
            return;
        }

        Bukkit.getOnlinePlayers()
                .forEach(player -> nmsAccessor.getEntityAccessor().despawnFakeEntityFor(guildHeartEntity, player));
        entityMap.remove(guild);
    }

    public void despawnGuildEntities(GuildManager guildManager) {
        guildManager.getGuilds()
                .forEach(this::despawnGuildEntity);
    }
}
