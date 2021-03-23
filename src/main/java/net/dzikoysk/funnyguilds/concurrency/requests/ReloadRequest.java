package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public final class ReloadRequest extends DefaultConcurrencyRequest {

    private final CommandSender sender;
    private final long startTime;

    public ReloadRequest(CommandSender sender) {
        this.sender = sender;
        this.startTime =  System.currentTimeMillis();
    }

    @Override
    public void execute() throws IOException, IllegalAccessException {
        FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
        funnyGuilds.reloadPluginConfiguration();
        funnyGuilds.reloadMessageConfiguration();
        funnyGuilds.getDataPersistenceHandler().reloadHandler();
        funnyGuilds.getDynamicListenerManager().reloadAll();

        if (FunnyGuilds.getInstance().getPluginConfiguration().playerListEnable) {
            PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
            AbstractTablist.wipeCache();

            for (Player player : Bukkit.getOnlinePlayers()) {
                AbstractTablist.createTablist(config.playerList, config.playerListHeader, config.playerListFooter, config.playerListPing, player);
            }
        }

        long endTime = System.currentTimeMillis();
        String diff = String.format("%.2f", ((endTime - startTime) / 1000.0));

        sender.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano! (" + ChatColor.AQUA + diff + "s" + ChatColor.GRAY + ")");
    }

}
