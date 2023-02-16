package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Locale;
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
    public void execute(Player player, @CanManage User deputy, Guild guild) {
        when(!this.config.regionsEnabled, config -> config.regionsDisabled);

        Region region = when(guild.getRegion(), config -> config.regionsDisabled);

        int enlarge = region.getEnlarge();
        when(enlarge > this.config.enlargeItems.size() - 1, config -> config.enlargeMaxSize);

        ItemStack need = this.config.enlargeItems.get(enlarge);
        when(!player.getInventory().containsAtLeast(need, need.getAmount()), config -> config.enlargeItem,
                FunnyFormatter.of("{ITEM}", need.getAmount() + " " + need.getType().toString().toLowerCase(Locale.ROOT)));
        when(this.regionManager.isNearRegion(region.getCenter()), config -> config.enlargeIsNear);

        if (!SimpleEventHandler.handle(new GuildEnlargeEvent(EventCause.USER, deputy, guild))) {
            return;
        }

        player.getInventory().removeItem(need);
        region.setEnlarge(++enlarge);
        region.setSize(region.getSize() + this.config.enlargeSize);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{SIZE}", region.getSize())
                .register("{LEVEL}", region.getEnlarge());

        this.messageService.getMessage(config -> config.enlargeDone)
                .receiver(guild)
                .with(formatter)
                .send();
    }

}
