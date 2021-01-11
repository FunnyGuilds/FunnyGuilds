package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildBaseChangeEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SetBaseCommand {

    @FunnyCommand(
        name = "${user.set-base.name}",
        description = "${user.set-base.description}",
        aliases = "${user.set-base.aliases}",
        permission = "funnyguilds.setbase",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(CommandSender sender) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        Player player = (Player) sender;
        User user = User.get(player);

        if (! FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
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
