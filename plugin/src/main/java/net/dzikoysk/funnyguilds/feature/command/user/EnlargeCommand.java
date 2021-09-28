package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class EnlargeCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.enlarge.name}",
        description = "${user.enlarge.description}",
        aliases = "${user.enlarge.aliases}",
        permission = "funnyguilds.enlarge",
        playerOnly = true
    )
    public void execute(Player player, @CanManage User user, Guild guild) {
        when (!pluginConfiguration.regionsEnabled, messageConfiguration.regionsDisabled);

        Region region = guild.getRegion();
        when (region == null, messageConfiguration.regionsDisabled);

        int enlarge = region.getEnlarge();
        when (enlarge > pluginConfiguration.enlargeItems.size() - 1, messageConfiguration.enlargeMaxSize);

        ItemStack need = pluginConfiguration.enlargeItems.get(enlarge);
        when (!player.getInventory().containsAtLeast(need, need.getAmount()), messageConfiguration.enlargeItem.replace("{ITEM}", need.getAmount() + " " + need.getType().toString().toLowerCase()));
        when (RegionUtils.isNear(region.getCenter()), messageConfiguration.enlargeIsNear);

        if (!SimpleEventHandler.handle(new GuildEnlargeEvent(EventCause.USER, user, user.getGuild()))) {
            return;
        }
        
        player.getInventory().removeItem(need);
        region.setEnlarge(++enlarge);
        region.setSize(region.getSize() + pluginConfiguration.enlargeSize);

        guild.broadcast(messageConfiguration.enlargeDone
                .replace("{SIZE}", Integer.toString(region.getSize()))
                .replace("{LEVEL}", Integer.toString(region.getEnlarge())));
    }

}
