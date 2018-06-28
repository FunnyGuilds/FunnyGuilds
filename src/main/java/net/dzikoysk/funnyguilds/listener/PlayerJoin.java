package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.util.Version;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.PacketExtension;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final FunnyGuilds plugin;

    public PlayerJoin(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        User user = User.get(player);
        PluginConfig config = Settings.getConfig();

        if (config.playerlistEnable && !AbstractTablist.hasTablist(player)) {
            AbstractTablist.createTablist(config.playerList, config.playerListHeader, config.playerListFooter, config.playerListPing, player);
        }

        user.getScoreboard();

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(
                new PrefixGlobalUpdatePlayer(player),
                new DummyGlobalUpdateUserRequest(user),
                new RankUpdateUserRequest(user)
        );

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            PacketExtension.registerPlayer(player);
            Version.isNewAvailable(player, false);

            Region region = RegionUtils.getAt(player.getLocation());
            if (region == null || region.getGuild() == null) {
                return;
            }
            
            if (config.createEntityType != null) {
                EntityUtil.spawn(region.getGuild(), player);
            }
        }, 30L);
    }
    
}
