package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;

public final class ReloadRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;

    private final CommandSender sender;
    private final long startTime;

    public ReloadRequest(FunnyGuilds plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void execute() throws Exception {
        this.plugin.reloadConfiguration();
        this.plugin.getDataPersistenceHandler().reloadHandler();
        this.plugin.getDynamicListenerManager().reloadAll();

        if (this.plugin.getTablistConfiguration().playerListEnable) {
            PluginConfiguration config = this.plugin.getPluginConfiguration();
            TablistConfiguration tablistConfig = this.plugin.getTablistConfiguration();
            UserManager userManager = this.plugin.getUserManager();

            try (PandaStream<? extends Player> players = PandaStream.of(Bukkit.getOnlinePlayers())) {
                players.flatMap(userManager::findByPlayer)
                        .forEach(user -> {
                            IndividualPlayerList playerList = new IndividualPlayerList(user,
                                    plugin.getNmsAccessor().getPlayerListAccessor(),
                                    tablistConfig.playerList,
                                    tablistConfig.playerListHeader, tablistConfig.playerListFooter,
                                    tablistConfig.playerListAnimated, tablistConfig.pages,
                                    tablistConfig.heads.textures,
                                    tablistConfig.playerListPing,
                                    tablistConfig.playerListFillCells,
                                    config.top.enableLegacyPlaceholders
                            );

                            user.getCache().setPlayerList(playerList);
                        });
            }
        }

        long endTime = System.currentTimeMillis();
        String diff = String.format("%.2f", (endTime - startTime) / 1000.0D);

        sender.sendMessage(FunnyFormatter.formatOnce(this.plugin.getMessageConfiguration().reloadTime, "{TIME}", diff));
    }

}
