package net.dzikoysk.funnyguilds.config.message;

import java.util.Locale;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.peridot.yetanothermessageslibrary.adventure.AudienceSupplier;
import pl.peridot.yetanothermessageslibrary.message.BukkitMessageDispatcher;
import pl.peridot.yetanothermessageslibrary.message.Sendable;

public class FunnyMessageDispatcher extends BukkitMessageDispatcher<FunnyMessageDispatcher> {

    private final Function<User, CommandSender> supplyReceiver;

    public FunnyMessageDispatcher(
            AudienceSupplier<CommandSender> audienceSupplier,
            Function<Object, Locale> localeSupplier,
            Function<Object, Sendable> messageSupplier,
            Function<User, CommandSender> supplyReceiver
    ) {
        super(audienceSupplier, localeSupplier, messageSupplier);
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
