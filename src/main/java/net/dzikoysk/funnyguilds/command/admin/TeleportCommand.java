package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class TeleportCommand {

    @FunnyCommand(
        name = "${admin.teleport.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(MessageConfiguration messages, PluginConfiguration config, CommandSender sender, String[] args) {
        when (!config.regionsEnabled, messages.regionsDisabled);
        when (args.length < 1, messages.generalNoTagGiven);

        Player player = (Player) sender;
        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        Region region = guild.getRegion();
        when(region == null || region.getCenter() == null, messages.adminNoRegionFound);

        player.sendMessage(messages.baseTeleport);
        player.teleport(region.getCenter());
    }

}
