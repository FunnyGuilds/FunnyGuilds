package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper;
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
        User user = plugin.getUserManager().getUser(player);

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
