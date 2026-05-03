package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import java.util.Locale;
import java.util.function.Function;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.RankFormatter;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class PlayerInfoCommand extends AbstractFunnyCommand {

    @Inject
    private RankSystem rankSystem;

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
                .register("{POINTS}", rank.getPoints())
                .register("{KILLS}", rank.getKills())
                .register("{DEATHS}", rank.getDeaths())
                .register("{ASSISTS}", rank.getAssists())
                .register("{LOGOUTS}", rank.getLogouts())
                .register("{KDR}", String.format(Locale.US, "%.2f", rank.getKDR()))
                .register("{KDA}", String.format(Locale.US, "%.2f", rank.getKDA()))
                .register("{RANK}", rank.getPosition(DefaultTops.USER_POINTS_TOP));

        this.messageService.getMessage(baseMessage)
                .receiver(messageTarget)
                .with(formatter)
                .with(CommandSender.class, receiver -> {
                    FunnyFormatter perReceiverFormatter = new FunnyFormatter();
                    if (infoUser.hasGuild()) {
                        Guild guild = infoUser.getGuild().get();
                        perReceiverFormatter.register("{GUILD}", guild.getName());
                        perReceiverFormatter.register("{TAG}", guild.getTag());
                    } else {
                        perReceiverFormatter.register("{GUILD}", this.messageService.<Component>get(receiver, config -> config.gNameNoValue));
                        perReceiverFormatter.register("{TAG}", this.messageService.<Component>get(receiver, config -> config.gTagNoValue));
                    }
                    this.registerPredictedPointsPlaceholders(perReceiverFormatter, receiver, infoUser);
                    return perReceiverFormatter;
                })
                .send();
    }

    private void registerPredictedPointsPlaceholders(FunnyFormatter formatter, CommandSender receiver, User infoUser) {
        int gain = 0;
        int loss = 0;
        if (receiver instanceof Player) {
            User receiverUser = this.userManager.findByPlayer((Player) receiver).orNull();
            if (receiverUser != null && !receiverUser.equals(infoUser)) {
                int receiverPoints = receiverUser.getRank().getPoints();
                int infoUserPoints = infoUser.getRank().getPoints();
                gain = this.rankSystem.calculate(this.config.rankSystem, receiverPoints, infoUserPoints).getAttackerPoints();
                loss = -this.rankSystem.calculate(this.config.rankSystem, infoUserPoints, receiverPoints).getVictimPoints();
            }
        }
        formatter.register("{POINTS-GAIN}", gain);
        formatter.register("{POINTS-GAIN-FORMATTED}", RankFormatter.formatPointsChange(gain, this.config.killPointsChangeFormat));
        formatter.register("{POINTS-LOSS}", loss);
        formatter.register("{POINTS-LOSS-FORMATTED}", RankFormatter.formatPointsChange(loss, this.config.killPointsChangeFormat));
    }

}
