package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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

            PandaStream.of(Bukkit.getOnlinePlayers())
                    .flatMap(userManager::findByPlayer)
                    .forEach(user -> {
                        IndividualPlayerList playerList = new IndividualPlayerList(user,
                                plugin.getNmsAccessor().getPlayerListAccessor(),
                                tablistConfig.playerList,
                                tablistConfig.playerListHeader, tablistConfig.playerListFooter,
                                tablistConfig.playerListAnimated, tablistConfig.pages,
                                tablistConfig.heads.fillerTexture.toSkinTexture(),
                                tablistConfig.playerListPing,
                                tablistConfig.playerListFillCells,
                                config.top.enableLegacyPlaceholders
                        );

                        user.getCache().setPlayerList(playerList);
                    });
        }

        long endTime = System.currentTimeMillis();
        String diff = String.format("%.2f", ((endTime - startTime) / 1000.0));

        sender.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano! (" + ChatColor.AQUA + diff + "s" + ChatColor.GRAY + ")");
    }

}
