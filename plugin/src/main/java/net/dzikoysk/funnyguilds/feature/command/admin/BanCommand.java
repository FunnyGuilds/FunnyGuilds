package net.dzikoysk.funnyguilds.feature.command.admin;

import java.time.Duration;
import java.util.Arrays;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildBanEvent;
import net.dzikoysk.funnyguilds.feature.ban.BanUtils;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Joiner;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class BanCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.ban.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.generalNoTagGiven);
        when(args.length < 2, config -> config.adminNoBanTimeGiven);
        when(args.length < 3, config -> config.adminNoReasonGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(guild.isBanned(), config -> config.adminGuildBanned);

        Duration time = TimeUtils.parseTime(args[1]);
        when(time.toMillis() < 1, config -> config.adminTimeError);

        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length)).toString();
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildBanEvent(AdminUtils.getCause(admin), admin, guild, time, reason))) {
            return;
        }

        BanUtils.ban(guild, time, reason);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{TIME}", args[1])
                .register("{REASON}", ChatUtils.colored(reason));

        this.messageService.getMessage(config -> config.adminGuildBan)
                .receiver(sender)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.broadcastBan)
                .broadcast()
                .with(formatter)
                .send();
    }

}
