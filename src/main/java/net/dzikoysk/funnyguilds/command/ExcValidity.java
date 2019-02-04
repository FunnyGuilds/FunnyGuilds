package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.List;

public class ExcValidity implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        Player player = (Player) sender;
        User user = User.get(player);
        Guild guild = user.getGuild();

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        if (config.validityWhen != 0) {
            long c = guild.getValidity();
            long d = c - System.currentTimeMillis();
            
            if (d > config.validityWhen) {
                long when = d - config.validityWhen;
                player.sendMessage(messages.validityWhen.replace("{TIME}", TimeUtils.getDurationBreakdown(when)));
                return;
            }
        }

        List<ItemStack> requiredItems = config.validityItems;

        if (! this.playerHasEnoughItems(player, requiredItems)) {
            return;
        }
        
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(EventCause.USER, user, guild, config.validityTime))) {
            return;
        }
        
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        long c = guild.getValidity();
        if (c == 0) {
            c = System.currentTimeMillis();
        }

        c += config.validityTime;
        guild.setValidity(c);

        player.sendMessage(messages.validityDone.replace("{DATE}", config.dateFormat.format(new Date(c))));
    }

}
