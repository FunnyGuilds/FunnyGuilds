package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.TimeInflection.Case;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class ValidityCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.validity.name}",
            description = "${user.validity.description}",
            aliases = "${user.validity.aliases}",
            permission = "funnyguilds.validity",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @CanManage User deputy, Guild guild) {
        if (!this.config.validityWhen.isZero()) {
            Instant validity = guild.getValidity();
            Duration delta = Duration.between(Instant.now(), validity);

            when(delta.compareTo(this.config.validityWhen) > 0, FunnyFormatter.format(this.messages.validityWhen, "{TIME}",
                    TimeUtils.formatTime(this.messages, delta.minus(this.config.validityWhen), Case.NOMINATIVE)));
        }

        List<ItemStack> requiredItems = this.config.validityItems;
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, this.messages.validityItems)) {
            return;
        }

        Duration validityTime = this.config.validityTime;
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(EventCause.USER, deputy, guild, validityTime))) {
            return;
        }

        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        Instant validity = guild.getValidity();
        if (validity.toEpochMilli() == 0) {
            validity = Instant.now();
        }

        validity = validity.plus(validityTime);
        guild.setValidity(validity);

        String formattedValidity = this.messages.dateFormat.format(validity);
        deputy.sendMessage(FunnyFormatter.format(this.messages.validityDone, "{DATE}", formattedValidity));
    }

}
