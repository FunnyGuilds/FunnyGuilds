package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildBaseChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class SetBaseCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.set-base.name}",
        description = "${user.set-base.description}",
        aliases = "${user.set-base.aliases}",
        permission = "funnyguilds.setbase",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, @CanManage User user, Guild guild) {
        when (!this.pluginConfiguration.regionsEnabled, this.messageConfiguration.regionsDisabled);

        Region region = RegionUtils.get(guild.getName());
        Location location = player.getLocation();
        when (!region.isIn(location), this.messageConfiguration.setbaseOutside);

        if (!SimpleEventHandler.handle(new GuildBaseChangeEvent(EventCause.USER, user, guild, location))) {
            return;
        }

        guild.setHome(location);

        if (guild.getHome().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            for (int y = guild.getHome().getBlockY(); y > 0; y--) {
                guild.getHome().setY(y);

                if (guild.getHome().getBlock().getType() != Material.AIR) {
                    break;
                }
            }
        }

        player.sendMessage(this.messageConfiguration.setbaseDone);
    }

}
