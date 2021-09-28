package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
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
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @CanManage User user, Guild guild) {
        if (!config.validityWhen.isZero()) {
            long validity = guild.getValidity();
            Duration delta = Duration.between(Instant.now(), Instant.ofEpochMilli(validity));

            when (delta.compareTo(config.validityWhen) > 0, messages.validityWhen.replace("{TIME}", TimeUtils.getDurationBreakdown(delta.minus(config.validityWhen).toMillis())));
        }

        List<ItemStack> requiredItems = config.validityItems;

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }
        
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(EventCause.USER, user, guild, config.validityTime.toMillis()))) {
            return;
        }
        
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        long validity = guild.getValidity();

        if (validity == 0) {
            validity = System.currentTimeMillis();
        }

        validity += config.validityTime.toMillis();
        guild.setValidity(validity);
        player.sendMessage(messages.validityDone.replace("{DATE}", config.dateFormat.format(new Date(validity))));
    }

}
