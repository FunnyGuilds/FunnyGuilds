package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefix;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.war.WarPacketCallbacks;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsChannelHandler;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final FunnyGuilds plugin;

    private final RankManager rankManager;
    private final UserManager userManager;

    public PlayerJoin(FunnyGuilds plugin) {
        this.plugin = plugin;

        this.rankManager = plugin.getRankManager();
        this.userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PluginConfiguration config = plugin.getPluginConfiguration();
        TablistConfiguration tablistConfig = plugin.getTablistConfiguration();

        Player player = event.getPlayer();
        User user = userManager.getUser(player).orElseGet(() -> userManager.create(player));

        String playerName = player.getName();

        if (!user.getName().equals(playerName)) {
            userManager.updateUsername(user, playerName);
        }

        user.updateReference(player);
        UserCache cache = user.getCache();

        IndividualPlayerList individualPlayerList = new IndividualPlayerList(
                user,
                plugin.getNmsAccessor().getPlayerListAccessor(),
                tablistConfig.playerList,
                tablistConfig.playerListHeader, tablistConfig.playerListFooter,
                tablistConfig.pages,
                tablistConfig.playerListPing,
                tablistConfig.playerListFillCells
        );

        cache.setPlayerList(individualPlayerList);
        cache.updateScoreboardIfNull(player);

        if (cache.getIndividualPrefix() == null && config.guildTagEnabled) {
            IndividualPrefix prefix = new IndividualPrefix(user);
            prefix.initialize();

            cache.setIndividualPrefix(prefix);
        }

        ConcurrencyManager concurrencyManager = plugin.getConcurrencyManager();
        concurrencyManager.postRequests(
                new PrefixGlobalUpdatePlayer(player),
                new DummyGlobalUpdateUserRequest(user),
                new RankUpdateUserRequest(this.rankManager, this.userManager)
        );

        final FunnyGuildsChannelHandler channelHandler = this.plugin.getNmsAccessor().getPacketAccessor().getOrInstallChannelHandler(player);
        channelHandler.getPacketCallbacksRegistry().registerPacketCallback(new WarPacketCallbacks(player));

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            this.plugin.getVersion().isNewAvailable(player, false);

            Region region = RegionUtils.getAt(player.getLocation());
            if (region == null || region.getGuild() == null) {
                return;
            }
            
            if (config.createEntityType != null) {
                GuildEntityHelper.spawnGuildHeart(region.getGuild(), player);
            }
        }, 30L);
    }
    
}
