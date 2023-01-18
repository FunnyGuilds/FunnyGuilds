package net.dzikoysk.funnyguilds.config.message;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.peridot.yetanothermessageslibrary.SimpleSendableMessageService;
import pl.peridot.yetanothermessageslibrary.util.SchedulerWrapper;
import pl.peridot.yetanothermessageslibrary.viewer.BukkitViewerDataSupplier;
import pl.peridot.yetanothermessageslibrary.viewer.SimpleViewer;
import pl.peridot.yetanothermessageslibrary.viewer.SimpleViewerService;

public class MessageService extends SimpleSendableMessageService<CommandSender, MessageConfiguration, FunnyMessageDispatcher> {

    public MessageService(BukkitAudiences adventure, SchedulerWrapper schedulerWrapper) {
        super(
                new SimpleViewerService<>(
                        new BukkitViewerDataSupplier(adventure),
                        (receiver, audience, console, schedule) -> new SimpleViewer(audience, console, schedule),
                        schedulerWrapper
                ),
                (viewerService, localeSupplier, messageSupplier) -> new FunnyMessageDispatcher(viewerService, localeSupplier, messageSupplier, user -> Bukkit.getPlayer(user.getUUID()))
        );
    }

    public void reload() {
        this.getMessageRepositories().forEach((locale, repository) -> repository.load());
    }

    public void playerQuit(Player player) {
        this.getViewerService().removeViewer(player);
    }

}
