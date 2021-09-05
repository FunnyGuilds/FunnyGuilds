package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        funnyGuilds.reloadMessageConfiguration();
        funnyGuilds.getDataPersistenceHandler().reloadHandler();
        funnyGuilds.getDynamicListenerManager().reloadAll();

        if (FunnyGuilds.getInstance().getTablistConfiguration().playerListEnable) {
            TablistConfiguration tablistConfig = FunnyGuilds.getInstance().getTablistConfiguration();

            DefaultTablistVariables.clearFunnyVariables();

            for (Player player : Bukkit.getOnlinePlayers()) {
                User user = funnyGuilds.getUserManager().getUser(player).getOrNull();

                if (user == null) {
                    continue;
                }

                IndividualPlayerList playerList = new IndividualPlayerList(user,
                        funnyGuilds.getNmsAccessor().getPlayerListAccessor(),
                        tablistConfig.playerList,
                        tablistConfig.playerListHeader, tablistConfig.playerListFooter,
                        tablistConfig.playerListPing,
                        tablistConfig.playerListFillCells
                );

                user.getCache().setPlayerList(playerList);
            }
        }

        long endTime = System.currentTimeMillis();
        String diff = String.format("%.2f", ((endTime - startTime) / 1000.0));

        sender.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano! (" + ChatColor.AQUA + diff + "s" + ChatColor.GRAY + ")");
    }

}
