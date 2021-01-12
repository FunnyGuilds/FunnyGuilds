package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.commons.text.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ProtectionCommand {

    private static final SimpleDateFormat PROTECTION_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @FunnyCommand(
        name = "${admin.protection.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(messages.adminNoAdditionalProtectionDateGiven);
            return;
        }

        String protectionDateAsString = StringUtils.join(args, ' ', 1, 3);
        Date protectionDate;

        try {
            protectionDate = PROTECTION_DATE_FORMAT.parse(protectionDateAsString);
        }
        catch (ParseException e) {
            sender.sendMessage(messages.adminInvalidAdditionalProtectionDate);
            return;
        }

        guild.setAdditionalProtection(protectionDate.getTime());

        Formatter formatter = new Formatter()
                .register("{TAG}", guild.getTag())
                .register("{DATE}", protectionDateAsString);


        sender.sendMessage(formatter.format(messages.adminAdditionalProtectionSetSuccessfully));
    }
}
