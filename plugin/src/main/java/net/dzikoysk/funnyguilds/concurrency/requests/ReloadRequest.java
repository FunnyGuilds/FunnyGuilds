package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.element.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
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

        if (FunnyGuilds.getInstance().getPluginConfiguration().playerListEnable) {
            PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
            AbstractTablist.wipeCache();

            for (Player player : Bukkit.getOnlinePlayers()) {
                User user = funnyGuilds.getUserManager().getUser(player).getOrNull();

                if (user == null) {
                    continue;
                }

                IndividualPlayerList playerList = new IndividualPlayerList(user,
                        funnyGuilds.getNmsAccessor().getPlayerListAccessor().createPlayerList(PlayerListConstants.DEFAULT_CELL_COUNT),
                        config.playerList,
                        config.playerListHeader, config.playerListFooter,
                        config.playerListPing,
                        config.playerListFillCells
                );

                user.getCache().setPlayerList(playerList);
            }
        }

        long endTime = System.currentTimeMillis();
        String diff = String.format("%.2f", ((endTime - startTime) / 1000.0));

        sender.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano! (" + ChatColor.AQUA + diff + "s" + ChatColor.GRAY + ")");
    }

}
