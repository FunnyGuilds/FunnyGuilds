package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcTeleport implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(messages.generalNoTagGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            player.sendMessage(messages.generalNoGuildFound);
            return;
        }

        Region region = RegionUtils.get(GuildUtils.byTag(args[0]).getRegion());
        if (region == null || region.getCenter() == null) {
            player.sendMessage(messages.adminNoRegionFound);
            return;
        }

        player.sendMessage(messages.baseTeleport);
        player.teleport(region.getCenter());
    }
}
