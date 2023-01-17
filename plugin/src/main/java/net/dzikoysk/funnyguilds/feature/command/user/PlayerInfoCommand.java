package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Locale;
import java.util.function.Function;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.peridot.yetanothermessageslibrary.message.Sendable;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class PlayerInfoCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.player.name}",
            description = "${user.player.description}",
            aliases = "${user.player.aliases}",
            permission = "funnyguilds.playerinfo",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length == 0 && !(sender instanceof Player), config -> config.playerOnly);

        String name = args.length == 0 ? sender.getName() : args[0];
        User user = when(this.userManager.findByName(name, this.config.playerLookupIgnorecase), config -> config.generalNotPlayedBefore);

        this.sendInfoMessage(config -> config.playerInfoList, user, sender);
    }

    public void sendInfoMessage(Function<MessageConfiguration, Sendable> baseMessage, User infoUser, CommandSender messageTarget) {
        UserRank rank = infoUser.getRank();

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", infoUser.getName())
                .register("{POINTS-FORMAT}", NumberRange.inRangeToString(rank.getPoints(), this.config.pointsFormat))
                .register("{POINTS}", rank.getPoints())
                .register("{KILLS}", rank.getKills())
                .register("{DEATHS}", rank.getDeaths())
                .register("{ASSISTS}", rank.getAssists())
                .register("{LOGOUTS}", rank.getLogouts())
                .register("{KDR}", String.format(Locale.US, "%.2f", rank.getKDR()))
                .register("{KDA}", String.format(Locale.US, "%.2f", rank.getKDA()))
                .register("{RANK}", rank.getPosition(DefaultTops.USER_POINTS_TOP));

        this.messageService.getMessage(baseMessage)
                .with(formatter)
                .with(CommandSender.class, receiver -> {
                    FunnyFormatter guildFormatter = new FunnyFormatter();
                    if (infoUser.hasGuild()) {
                        Guild guild = infoUser.getGuild().get();
                        guildFormatter.register("{GUILD}", guild.getName());
                        guildFormatter.register("{TAG}", guild.getTag());
                    } else {
                        guildFormatter.register("{GUILD}", this.messageService.<String>get(receiver, config -> config.gNameNoValue));
                        guildFormatter.register("{TAG}", this.messageService.<String>get(receiver, config -> config.gTagNoValue));
                    }
                    return guildFormatter;
                })
                .receiver(messageTarget)
                .send();
    }

}
