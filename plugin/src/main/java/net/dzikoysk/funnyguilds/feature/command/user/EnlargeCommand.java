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
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
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
        when(!config.regionsEnabled, messages.regionsDisabled);

        Region region = when(guild.getRegion(), messages.regionsDisabled);

        int enlarge = region.getEnlarge();
        when(enlarge > config.enlargeItems.size() - 1, messages.enlargeMaxSize);

        ItemStack need = config.enlargeItems.get(enlarge);
        when(!player.getInventory().containsAtLeast(need, need.getAmount()), FunnyFormatter.formatOnce(messages.enlargeItem,
                "{ITEM}", need.getAmount() + " " + need.getType().toString().toLowerCase()));
        when(this.regionManager.isNearRegion(region.getCenter()), messages.enlargeIsNear);

        if (!SimpleEventHandler.handle(new GuildEnlargeEvent(EventCause.USER, user, guild))) {
            return;
        }

        player.getInventory().removeItem(need);
        region.setEnlarge(++enlarge);
        region.setSize(region.getSize() + config.enlargeSize);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{SIZE}", region.getSize())
                .register("{LEVEL}", region.getEnlarge());

        guild.broadcast(formatter.format(messages.enlargeDone));
    }

}
