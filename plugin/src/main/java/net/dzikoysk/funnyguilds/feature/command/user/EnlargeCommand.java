package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.purchase.GuildPurchaseService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

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
        int next = guild.getRegion().map(Region::getEnlargementLevel).orElse(0) + 1;
        GuildPurchaseService purchases = this.plugin.getGuildPurchaseService();
        purchases.expand(player, deputy, guild, purchases.quoteExpansion(guild, next));
    }

}
