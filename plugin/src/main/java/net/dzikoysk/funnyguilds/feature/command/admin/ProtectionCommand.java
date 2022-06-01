package net.dzikoysk.funnyguilds.feature.command.admin;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import panda.std.Option;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class ProtectionCommand extends AbstractFunnyCommand {

    private static final DateTimeFormatter PROTECTION_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @FunnyCommand(
            name = "${admin.protection.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoTagGiven);
        when(args.length < 3, messages.adminNoProtectionDateGive);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        String protectionDateAsString = StringUtils.join(args, ' ', 1, 3);

        LocalDateTime protectionDate = Option.attempt(ParseException.class, () -> {
            return LocalDateTime.parse(protectionDateAsString, PROTECTION_DATE_FORMATTER);
        }).orThrow(() -> {
            return new ValidationException(StringUtils.replace(messages.adminErrorInNumber, "{ERROR}", args[1]));
        });

        guild.setProtection(protectionDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{TAG}", guild.getTag())
                .register("{DATE}", protectionDateAsString);

        sendMessage(sender, formatter.format(messages.adminProtectionSetSuccessfully));
    }
}
