package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.panda_lang.panda.utilities.commons.text.MessageFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AxcProtection implements Executor {

    private static final SimpleDateFormat PROTECTION_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
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

        String protectionDateAsString = StringUtils.join(args, ' ', 1, 2);
        Date protectionDate;

        try {
            protectionDate = PROTECTION_DATE_FORMAT.parse(protectionDateAsString);
        }
        catch (ParseException e) {
            sender.sendMessage(messages.adminInvalidAdditionalProtectionDate);
            return;
        }

        guild.setAdditionalProtection(protectionDate.getTime());

        MessageFormatter formatter = new MessageFormatter()
                .register("{TAG}", guild.getTag())
                .register("{DATE}", protectionDateAsString);


        sender.sendMessage(formatter.format(messages.adminAdditionalProtectionSetSuccessfully));
    }
}
