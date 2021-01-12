package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.CanManage;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class EnlargeCommand {

    @FunnyCommand(
        name = "${user.enlarge.name}",
        description = "${user.enlarge.description}",
        aliases = "${user.enlarge.aliases}",
        permission = "funnyguilds.enlarge",
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @CanManage User user, Guild guild) {
        when (!config.regionsEnabled, messages.regionsDisabled);

        Region region = guild.getRegion();
        when (region == null, messages.regionsDisabled);

        int enlarge = region.getEnlarge();
        when (enlarge > config.enlargeItems.size() - 1, messages.enlargeMaxSize);

        ItemStack need = config.enlargeItems.get(enlarge);
        when (!player.getInventory().containsAtLeast(need, need.getAmount()), messages.enlargeItem.replace("{ITEM}", need.getAmount() + " " + need.getType().toString().toLowerCase()));
        when (RegionUtils.isNear(region.getCenter()), messages.enlargeIsNear);

        if (!SimpleEventHandler.handle(new GuildEnlargeEvent(EventCause.USER, user, user.getGuild()))) {
            return;
        }
        
        player.getInventory().removeItem(need);
        region.setEnlarge(++enlarge);
        region.setSize(region.getSize() + config.enlargeSize);

        guild.broadcast(messages.enlargeDone
                .replace("{SIZE}", Integer.toString(region.getSize()))
                .replace("{LEVEL}", Integer.toString(region.getEnlarge())));
    }

}
