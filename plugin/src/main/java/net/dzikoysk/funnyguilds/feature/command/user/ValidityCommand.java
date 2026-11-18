package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Duration;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.sections.GuildPurchaseConfiguration;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.purchase.GuildPurchaseService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

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
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.EXTEND_VALIDITY) User deputy, Guild guild, String[] args) {
        Duration period = this.config.validityTime;
        if (args.length > 0) {
            try {
                period = GuildPurchaseConfiguration.parsePeriod(args[0]);
            } catch (IllegalArgumentException | ArithmeticException exception) {
                throw new InternalValidationException(config -> config.validityPeriodUnavailable);
            }
        }
        GuildPurchaseService purchases = this.plugin.getGuildPurchaseService();
        purchases.renew(player, deputy, guild, purchases.quoteRenewal(guild, period));
    }

}
