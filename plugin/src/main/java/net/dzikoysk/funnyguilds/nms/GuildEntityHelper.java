package net.dzikoysk.funnyguilds.nms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class GuildEntityHelper {

    private static final Map<Guild, FakeEntity> ENTITY_MAP = new ConcurrentHashMap<>();

    private GuildEntityHelper() {
    }

    public static Map<Guild, FakeEntity> getGuildEntities() {
        return ENTITY_MAP;
    }

    public static void spawnGuildHeart(Guild guild) {
        spawnGuildHeart(guild, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public static void spawnGuildHeart(Guild guild, Player... players) {
        /*try {
            FunnyGuilds plugin = FunnyGuilds.getInstance();
            FakeEntity guildHeartEntity;

            if (!ENTITY_MAP.containsKey(guild)) {
                Option<Location> locationOption = guild.getEnderCrystal();

                if (locationOption.isEmpty()) {
                    return;
                }

                //guildHeartEntity = plugin.getNmsAccessor()
                //        .getEntityAccessor()
                //        .createFakeEntity(plugin.getPluginConfiguration().heart.createEntityType, locationOption.get());

                //ENTITY_MAP.put(guild, guildHeartEntity);
            }
            else {
                guildHeartEntity = ENTITY_MAP.get(guild);
            }

            //plugin.getNmsAccessor()
            //        .getEntityAccessor()
             //       .spawnFakeEntityFor(guildHeartEntity, players);
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not spawn guild heart", exception);
        }*/
    }

    public static void despawnGuildHeart(Guild guild) {
        /*try {
            FakeEntity guildHeartEntity = ENTITY_MAP.remove(guild);

            if (guildHeartEntity == null) {
                return;
            }

            FunnyGuilds.getInstance().getNmsAccessor()
                    .getEntityAccessor()
                    .despawnFakeEntityFor(guildHeartEntity, Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not despawn guild heart", exception);
        }*/
    }

    public static void despawnGuildHeart(Guild guild, Player... players) {
        /*try {
            FakeEntity guildHeartEntity = ENTITY_MAP.get(guild);

            if (guildHeartEntity == null) {
                return;
            }

            FunnyGuilds.getInstance().getNmsAccessor()
                    .getEntityAccessor()
                    .despawnFakeEntityFor(guildHeartEntity, players);
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not despawn guild heart", exception);
        }*/
    }

    public static void despawnGuildHearts() {
        for (Guild guild : GuildUtils.getGuilds()) {
            despawnGuildHeart(guild);
        }
    }

}
