package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefix;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.war.WarPacketCallbacks;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsInboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.api.packet.FunnyGuildsOutboundChannelHandler;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntitySupplier;
import net.dzikoysk.funnyguilds.user.BukkitUserProfile;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import net.dzikoysk.funnyguilds.user.UserProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin extends AbstractFunnyListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = this.userManager.findByPlayer(player)
                .peek(foundUser -> foundUser.getProfile().refresh())
                .orElseGet(() -> {
                    UserProfile profile = new BukkitUserProfile(player.getUniqueId(), this.funnyServer);
                    return this.userManager.create(player.getUniqueId(), player.getName(), profile);
                });

        String playerName = player.getName();
        if (!user.getName().equals(playerName)) {
            this.userManager.updateUsername(user, playerName);
        }

        UserCache cache = user.getCache();

        if (this.tablistConfig.playerListEnable) {
            IndividualPlayerList individualPlayerList = new IndividualPlayerList(
                    user,
                    this.nmsAccessor.getPlayerListAccessor(),
                    this.funnyServer,
                    this.tablistConfig.playerList,
                    this.tablistConfig.playerListHeader, this.tablistConfig.playerListFooter,
                    this.tablistConfig.playerListAnimated, this.tablistConfig.pages,
                    this.tablistConfig.heads.textures,
                    this.tablistConfig.playerListPing,
                    this.tablistConfig.playerListFillCells,
                    this.config.top.enableLegacyPlaceholders
            );

            individualPlayerList.send();
            cache.setPlayerList(individualPlayerList);
        }

        cache.updateScoreboardIfNull(player);

        if (this.config.guildTagEnabled && cache.getIndividualPrefix().isEmpty()) {
            IndividualPrefix prefix = new IndividualPrefix(user);
            prefix.initialize();
            cache.setIndividualPrefix(prefix);
        }

        this.concurrencyManager.postRequests(
                new PrefixGlobalUpdatePlayer(this.individualPrefixManager, player),
                new DummyGlobalUpdateUserRequest(user)
        );

        FunnyGuildsInboundChannelHandler inboundChannelHandler = this.nmsAccessor.getPacketAccessor().getOrInstallInboundChannelHandler(player);
        inboundChannelHandler.getPacketCallbacksRegistry().registerPacketCallback(new WarPacketCallbacks(this.plugin, user));

        FunnyGuildsOutboundChannelHandler outboundChannelHandler = this.nmsAccessor.getPacketAccessor().getOrInstallOutboundChannelHandler(player);
        outboundChannelHandler.getPacketSuppliersRegistry().registerPacketSupplier(new GuildEntitySupplier(this.guildEntityHelper));

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            this.plugin.getVersion().isNewAvailable(player, false);
        }, 30L);
    }

}
