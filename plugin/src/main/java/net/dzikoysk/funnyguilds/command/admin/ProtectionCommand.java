package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class ProtectionCommand {

    private static final SimpleDateFormat PROTECTION_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @FunnyCommand(
        name = "${admin.protection.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);
        when (args.length < 3, messages.adminNoProtectionDateGive);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        String protectionDateAsString = StringUtils.join(args, ' ', 1, 3);
        Date protectionDate;

        try {
            protectionDate = PROTECTION_DATE_FORMAT.parse(protectionDateAsString);
        } catch (ParseException e) {
            sender.sendMessage(messages.adminInvalidProtectionDate);
            return;
        }

        guild.setProtection(protectionDate.getTime());

        Formatter formatter = new Formatter()
                .register("{TAG}", guild.getTag())
                .register("{DATE}", protectionDateAsString);


        sender.sendMessage(formatter.format(messages.adminProtectionSetSuccessfully));
    }
}
