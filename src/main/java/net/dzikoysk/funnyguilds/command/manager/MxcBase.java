package net.dzikoysk.funnyguilds.command.manager;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MxcBase implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages m = Messages.getInstance();
        Player p = (Player) sender;
        User user = User.get(p);

        if (!user.hasGuild()) {
            p.sendMessage(m.getMessage("setbaseHasNotGuild"));
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            p.sendMessage(m.getMessage("setbaseIsNotOwner"));
            return;
        }

        Guild guild = user.getGuild();
        Region region = RegionUtils.get(guild.getName());
        Location loc = p.getLocation();

        if (!region.isIn(loc)) {
            p.sendMessage(m.getMessage("setbaseOutside"));
            return;
        }

        guild.setHome(loc);
        if (guild.getHome().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            for (int i = guild.getHome().getBlockY(); i > 0; i--) {
                guild.getHome().setY(i);
                if (guild.getHome().getBlock().getType() != Material.AIR)
                    break;
            }
        }

        p.sendMessage(m.getMessage("setbaseDone"));
    }

}
