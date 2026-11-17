package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.Locale;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
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
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.ENLARGE) User deputy, Guild guild) {
        when(!this.config.regionsEnabled, config -> config.regionsDisabled);

        Region region = when(guild.getRegion(), config -> config.regionsDisabled);

        int currentEnlargementLevel = region.getEnlargementLevel();
        when(currentEnlargementLevel > this.config.enlargeItems.size() - 1, config -> config.enlargeMaxSize);

        ItemStack need = this.config.enlargeItems.get(currentEnlargementLevel);
        when(!player.getInventory().containsAtLeast(need, need.getAmount()), config -> config.enlargeItem,
             Replacement.string("{ITEM}", need.getAmount() + " " + need.getType().toString().toLowerCase(Locale.ROOT)));
        when(this.regionManager.isNearRegion(region.getCenter()), config -> config.enlargeIsNear);

        if (!SimpleEventHandler.handle(new GuildEnlargeEvent(EventCause.USER, deputy, guild))) {
            return;
        }

        player.getInventory().removeItem(need);
        this.regionManager.changeRegionEnlargement(region, currentEnlargementLevel + 1);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{SIZE}", region.getSize())
                .register("{LEVEL}", region.getEnlargementLevel());

        this.messageService.getMessage(config -> config.enlargeDone)
                .receiver(guild)
                .with(formatter)
                .send();
    }

}
