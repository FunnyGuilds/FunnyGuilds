package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.IsOwner;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

@FunnyComponent
public final class ConfirmCommand {

    @FunnyCommand(
        name = "${user.confirm.name}",
        description = "${user.confirm.description}",
        aliases = "${user.confirm.aliases}",
        permission = "funnyguilds.delete",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @IsOwner User user, Guild guild) {
        if (config.guildDeleteCancelIfSomeoneIsOnRegion && guild.isSomeoneInRegion()) {
            player.sendMessage(messages.deleteSomeoneIsNear);
            return;
        }

        if (!ConfirmationList.contains(user.getUUID())) {
            player.sendMessage(messages.deleteToConfirm);
            return;
        }

        ConfirmationList.remove(user.getUUID());

        if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.USER, user, guild))) {
            return;
        }

        GuildUtils.deleteGuild(guild);

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", player.getName());

        player.sendMessage(formatter.format(messages.deleteSuccessful));
        Bukkit.getServer().broadcastMessage(formatter.format(messages.broadcastDelete));
    }

}
