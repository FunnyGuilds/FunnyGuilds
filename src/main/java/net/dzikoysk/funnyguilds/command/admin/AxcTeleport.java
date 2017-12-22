package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcTeleport implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);

        if (guild == null) {
            player.sendMessage(messages.generalNoGuildFound);
            return;
        }

        Region region = RegionUtils.get(guild.getRegion());

        if (region == null || region.getCenter() == null) {
            player.sendMessage(messages.adminNoRegionFound);
            return;
        }

        player.sendMessage(messages.baseTeleport);
        player.teleport(region.getCenter());
    }

}
