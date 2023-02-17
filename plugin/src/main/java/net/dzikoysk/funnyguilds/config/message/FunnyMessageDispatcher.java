package net.dzikoysk.funnyguilds.config.message;

import java.util.Locale;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.peri.yetanothermessageslibrary.message.BukkitMessageDispatcher;
import dev.peri.yetanothermessageslibrary.message.Sendable;
import dev.peri.yetanothermessageslibrary.viewer.Viewer;
import dev.peri.yetanothermessageslibrary.viewer.ViewerService;

public class FunnyMessageDispatcher extends BukkitMessageDispatcher<FunnyMessageDispatcher> {

    private final Function<User, CommandSender> supplyReceiver;

    public FunnyMessageDispatcher(
            ViewerService<CommandSender, ? extends Viewer> viewerService,
            Function<Object, Locale> localeSupplier,
            Function<Object, Sendable> messageSupplier,
            Function<User, CommandSender> supplyReceiver
    ) {
        super(viewerService, localeSupplier, messageSupplier);
        this.supplyReceiver = supplyReceiver;
    }

    public FunnyMessageDispatcher receiver(User user) {
        if (user == null) {
            return this;
        }

        CommandSender sender = this.supplyReceiver.apply(user);
        if (sender instanceof Player) {
            this.receiver(sender);
        }
        return this;
    }

    public FunnyMessageDispatcher receivers(Iterable<User> receivers) {
        receivers.forEach(receiver -> this.receiver(this.supplyReceiver.apply(receiver)));
        return this;
    }

    public FunnyMessageDispatcher receiver(Guild guild) {
        return this.receivers(guild.getOnlineMembers());
    }

}
