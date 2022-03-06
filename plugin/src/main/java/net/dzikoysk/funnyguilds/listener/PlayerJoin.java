package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefix;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.war.WarPacketCallbacks;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntitySupplier;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin extends AbstractFunnyListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = this.userManager.findByPlayer(player).orElseGet(() -> userManager.create(player));

        String playerName = player.getName();

        if (!user.getName().equals(playerName)) {
            this.userManager.updateUsername(user, playerName);
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
                tablistConfig.playerListFillCells,
                config.top.enableLegacyPlaceholders
        );
        individualPlayerList.send();

        cache.setPlayerList(individualPlayerList);
        cache.updateScoreboardIfNull(player);

        if (cache.getIndividualPrefix() == null && config.guildTagEnabled) {
            IndividualPrefix prefix = new IndividualPrefix(user);
            prefix.initialize();

            cache.setIndividualPrefix(prefix);
        }

        this.concurrencyManager.postRequests(
                new PrefixGlobalUpdatePlayer(player),
                new DummyGlobalUpdateUserRequest(user)
        );

        final FunnyGuildsInboundChannelHandler inboundChannelHandler = this.plugin.getNmsAccessor().getPacketAccessor().getOrInstallInboundChannelHandler(player);
        inboundChannelHandler.getPacketCallbacksRegistry().registerPacketCallback(new WarPacketCallbacks(plugin, user));
        final FunnyGuildsOutboundChannelHandler outboundChannelHandler = this.plugin.getNmsAccessor().getPacketAccessor().getOrInstallOutboundChannelHandler(player);
        outboundChannelHandler.getPacketSuppliersRegistry().registerPacketSupplier(new GuildEntitySupplier(this.plugin.getGuildEntityHelper()));

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            this.plugin.getVersion().isNewAvailable(player, false);
        }, 30L);
    }

}
