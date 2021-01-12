package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EnlargeCommand {

    @FunnyCommand(
        name = "${user.enlarge.name}",
        description = "${user.enlarge.description}",
        aliases = "${user.enlarge.aliases}",
        permission = "funnyguilds.enlarge",
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, User user) {
        if (!config.regionsEnabled) {
            player.sendMessage(messages.regionsDisabled);
            return;
        }
        
        if (!config.enlargeEnable) {
            return;
        }

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        Region region = user.getGuild().getRegion();

        if (region == null) {
            player.sendMessage(messages.regionsDisabled);
            return;
        }

        int enlarge = region.getEnlarge();

        if (enlarge > config.enlargeItems.size() - 1) {
            player.sendMessage(messages.enlargeMaxSize);
            return;
        }

        ItemStack need = config.enlargeItems.get(enlarge);

        if (!player.getInventory().containsAtLeast(need, need.getAmount())) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(need.getAmount());
            messageBuilder.append(" ");
            messageBuilder.append(need.getType().toString().toLowerCase());

            player.sendMessage(messages.enlargeItem.replace("{ITEM}", messageBuilder.toString()));
            return;
        }

        if (RegionUtils.isNear(region.getCenter())) {
            player.sendMessage(messages.enlargeIsNear);
            return;
        }

        if (!SimpleEventHandler.handle(new GuildEnlargeEvent(EventCause.USER, user, user.getGuild()))) {
            return;
        }
        
        player.getInventory().removeItem(need);
        region.setEnlarge(++enlarge);
        region.setSize(region.getSize() + config.enlargeSize);

        String enlargeDoneMessage = messages.enlargeDone
                .replace("{SIZE}", Integer.toString(region.getSize()))
                .replace("{LEVEL}", Integer.toString(region.getEnlarge()));

        user.getGuild().broadcast(enlargeDoneMessage);
    }
}
