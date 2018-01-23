package net.dzikoysk.funnyguilds.command.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildBaseChangeEvent;

public class MxcBase implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!Settings.getConfig().regionsEnabled) {
            player.sendMessage(messages.regionsDisabled);
            return;
        }
        
        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        Guild guild = user.getGuild();
        Region region = RegionUtils.get(guild.getName());
        Location loc = player.getLocation();

        if (!region.isIn(loc)) {
            player.sendMessage(messages.setbaseOutside);
            return;
        }

        if (!SimpleEventHandler.handle(new GuildBaseChangeEvent(EventCause.USER, user, guild, loc))) {
            return;
        }
        
        guild.setHome(loc);
        if (guild.getHome().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            for (int i = guild.getHome().getBlockY(); i > 0; i--) {
                guild.getHome().setY(i);
                if (guild.getHome().getBlock().getType() != Material.AIR) {
                    break;
                }
            }
        }

        player.sendMessage(messages.setbaseDone);
    }
}
