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
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;

        if (args.length < 1) {
            p.sendMessage(m.adminNoTagGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            p.sendMessage(m.adminNoGuildFound);
            return;
        }

        String rs = GuildUtils.byTag(args[0]).getRegion();
        Region region = RegionUtils.get(rs);

        if (region == null || region.getCenter() == null) {
            p.sendMessage(m.adminNoRegionFound);
            return;
        }

        p.sendMessage(m.baseTeleport);
        p.teleport(region.getCenter());
    }
}
