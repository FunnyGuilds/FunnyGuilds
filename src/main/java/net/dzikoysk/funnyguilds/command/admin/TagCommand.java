package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.bukkit.command.CommandSender;

public final class TagCommand {

    @FunnyCommand(
        name = "${admin.tag.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (args.length < 2) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        String tag = args[1];

        if (GuildUtils.tagExists(tag)) {
            sender.sendMessage(messages.createTagExists);
            return;
        }
        
        guild.setTag(tag);
        FunnyGuilds.getInstance().getDataModel().save(false);
        sender.sendMessage(messages.adminTagChanged.replace("{TAG}", guild.getTag()));
    }

}
