package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class ValidityAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.validity.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, this.messages.generalNoTagGiven);
        when(args.length < 2, this.messages.adminNoValidityTimeGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(guild.isBanned(), this.messages.adminGuildBanned);

        long time = TimeUtils.parseTime(args[1]);
        if (time < 1) {
            this.sendMessage(sender, this.messages.adminTimeError);
            return;
        }

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(AdminUtils.getCause(admin), admin, guild, time))) {
            return;
        }

        long validity = guild.getValidity();
        if (validity == 0) {
            validity = System.currentTimeMillis();
        }

        validity += time;
        guild.setValidity(validity);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{VALIDITY}", this.messages.dateFormat.format(validity));

        this.sendMessage(sender, formatter.format(this.messages.adminNewValidity));
    }

}
