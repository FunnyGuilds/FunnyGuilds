package net.dzikoysk.funnyguilds.config.message;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pl.peridot.yetanothermessageslibrary.SimpleSendableMessageService;
import pl.peridot.yetanothermessageslibrary.adventure.BukkitAudienceSupplier;

public class MessageService extends SimpleSendableMessageService<CommandSender, MessageConfiguration, FunnyMessageDispatcher> {

    public MessageService(BukkitAudiences adventure) {
        super(
                new BukkitAudienceSupplier(adventure),
                (audienceSupplier, localeSupplier, messageSupplier) -> new FunnyMessageDispatcher(audienceSupplier, localeSupplier, messageSupplier, user -> Bukkit.getPlayer(user.getUUID()))
        );
    }

}
