package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
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
        if (!this.pluginConfig.validityWhen.isZero()) {
            long validity = guild.getValidity();
            Duration delta = Duration.between(Instant.now(), Instant.ofEpochMilli(validity));

            when (delta.compareTo(this.pluginConfig.validityWhen) > 0, this.messageConfig.validityWhen.replace("{TIME}", TimeUtils.getDurationBreakdown(delta.minus(this.pluginConfig.validityWhen).toMillis())));
        }

        List<ItemStack> requiredItems = this.pluginConfig.validityItems;

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }
        
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(EventCause.USER, user, guild, this.pluginConfig.validityTime.toMillis()))) {
            return;
        }
        
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        long validity = guild.getValidity();

        if (validity == 0) {
            validity = System.currentTimeMillis();
        }

        validity += this.pluginConfig.validityTime.toMillis();
        guild.setValidity(validity);
        player.sendMessage(this.messageConfig.validityDone.replace("{DATE}", this.pluginConfig.dateFormat.format(new Date(validity))));
    }

}
