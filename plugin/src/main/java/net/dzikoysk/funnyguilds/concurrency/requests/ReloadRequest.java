package net.dzikoysk.funnyguilds.concurrency.requests;

import java.time.Duration;
import java.time.Instant;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import panda.std.stream.PandaStream;

public final class ReloadRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;
    private final CommandSender sender;
    private final Instant startTime;

    public ReloadRequest(FunnyGuilds plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
        this.startTime = Instant.now();
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
                        IndividualPlayerList playerList = new IndividualPlayerList(
                                user,
                                this.plugin.getNmsAccessor().getPlayerListAccessor(),
                                this.plugin.getFunnyServer(),
                                tablistConfig.playerList,
                                tablistConfig.playerListHeader, tablistConfig.playerListFooter,
                                tablistConfig.playerListAnimated, tablistConfig.pages,
                                tablistConfig.heads.textures,
                                tablistConfig.playerListPing,
                                tablistConfig.playerListFillCells
                        );

                        user.getCache().setPlayerList(playerList);
                    });
        }

        String diff = String.format("%.2f", Duration.between(Instant.now(), this.startTime).getSeconds());

        String message = FunnyFormatter.format(this.plugin.getMessageConfiguration().reloadTime, "{TIME}", diff);
        ChatUtils.sendMessage(this.sender, message);
    }

}
