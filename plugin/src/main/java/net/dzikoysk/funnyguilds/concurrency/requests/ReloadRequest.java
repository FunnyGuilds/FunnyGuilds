package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import panda.std.stream.PandaStream;

public final class ReloadRequest extends DefaultConcurrencyRequest {

    private final CommandSender sender;
    private final long startTime;

    public ReloadRequest(CommandSender sender) {
        this.sender = sender;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void execute() throws Exception {
        FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
        funnyGuilds.reloadPluginConfiguration();
        funnyGuilds.reloadTablistConfiguration();
        funnyGuilds.reloadMessageConfiguration();
        funnyGuilds.getDataPersistenceHandler().reloadHandler();
        funnyGuilds.getDynamicListenerManager().reloadAll();

        if (FunnyGuilds.getInstance().getTablistConfiguration().playerListEnable) {
            TablistConfiguration tablistConfig = FunnyGuilds.getInstance().getTablistConfiguration();

            DefaultTablistVariables.clearFunnyVariables();

            UserManager userManager = funnyGuilds.getUserManager();
            PandaStream.of(Bukkit.getOnlinePlayers())
                    .flatMap(userManager::getUser)
                    .forEach(user -> {
                        IndividualPlayerList playerList = new IndividualPlayerList(user,
                                funnyGuilds.getNmsAccessor().getPlayerListAccessor(),
                                tablistConfig.playerList,
                                tablistConfig.playerListHeader, tablistConfig.playerListFooter,
                                tablistConfig.pages,
                                tablistConfig.playerListPing,
                                tablistConfig.playerListFillCells
                        );

                        user.getCache().setPlayerList(playerList);
                    });
        }

        long endTime = System.currentTimeMillis();
        String diff = String.format("%.2f", ((endTime - startTime) / 1000.0));

        sender.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano! (" + ChatColor.AQUA + diff + "s" + ChatColor.GRAY + ")");
    }

}
