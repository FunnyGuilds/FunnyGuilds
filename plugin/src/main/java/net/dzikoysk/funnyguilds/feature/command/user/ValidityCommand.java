package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
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
    public void execute(Player player, @CanManage User user, Guild guild) {
        if (!this.config.validityWhen.isZero()) {
            long validity = guild.getValidity();
            Duration delta = Duration.between(Instant.now(), Instant.ofEpochMilli(validity));

            when(delta.compareTo(this.config.validityWhen) > 0, FunnyFormatter.formatOnce(this.messages.validityWhen, "{TIME}",
                    TimeUtils.getDurationBreakdown(delta.minus(this.config.validityWhen).toMillis())));
        }

        List<ItemStack> requiredItems = this.config.validityItems;
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, this.messages.validityItems)) {
            return;
        }

        long validityTime = this.config.validityTime.toMillis();
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(EventCause.USER, user, guild, validityTime))) {
            return;
        }

        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        long validity = guild.getValidity();

        if (validity == 0) {
            validity = System.currentTimeMillis();
        }

        validity += validityTime;
        guild.setValidity(validity);

        String formattedValidity = this.messages.dateFormat.format(validity);
        sendMessage(player, FunnyFormatter.formatOnce(this.messages.validityDone, "{DATE}", formattedValidity));
    }

}
