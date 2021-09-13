package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.region.Region;
import net.dzikoysk.funnyguilds.guild.region.RegionUtils;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

    private final FunnyGuilds plugin;

    public PlayerRespawn(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(final PlayerRespawnEvent event) {
        PluginConfiguration config = plugin.getPluginConfiguration();
        Player player = event.getPlayer();
        User user = UserUtils.get(player.getUniqueId());

        if (! user.hasGuild()) {
            return;
        }

        Location home = user.getGuild().getHome();

        if (home == null) {
            return;
        }

        event.setRespawnLocation(home);

        if (config.createEntityType == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin,  () -> {
            Region guildRegion = RegionUtils.getAt(home);

            if (guildRegion == null) {
                return;
            }

            Guild guild = guildRegion.getGuild();
            GuildEntityHelper.spawnGuildHeart(guild, player);
        });
    }
}
