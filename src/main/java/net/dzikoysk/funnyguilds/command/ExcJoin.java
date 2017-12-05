package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.data.util.MessageTranslator;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExcJoin implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (user.hasGuild()) {
            player.sendMessage(messages.joinHasGuild);
            return;
        }

        List<InvitationList.Invitation> invitations = InvitationList.getInvitationsFor(player);

        if (invitations.size() == 0) {
            player.sendMessage(messages.joinHasNotInvitation);
            return;
        }

        if (args.length < 1) {
            String guildNames = StringUtils.toString(InvitationList.getInvitationGuildNames(player), false);
            for (String msg : messages.joinInvitationList) {
                player.sendMessage(msg.replace("{GUILDS}", guildNames));
            }
            
            return;
        }

        String tag = args[0];
        Guild guild = Guild.get(tag);

        if (guild == null) {
            player.sendMessage(messages.joinTagExists);
            return;
        }

        if (!InvitationList.hasInvitationFrom(player, GuildUtils.byTag(tag))) {
            player.sendMessage(messages.joinHasNotInvitationTo);
            return;
        }

        List<ItemStack> itemsList = Settings.getConfig().joinItems;

        for (ItemStack itemStack : itemsList) {
            if (!player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                String msg = messages.joinItems;
                if (msg.contains("{ITEM}")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(itemStack.getAmount());
                    sb.append(" ");
                    sb.append(itemStack.getType().toString().toLowerCase());
                    msg = msg.replace("{ITEM}", sb.toString());
                }
                
                if (msg.contains("{ITEMS}")) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (ItemStack it : itemsList) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(it.getAmount());
                        sb.append(" ");
                        sb.append(it.getType().toString().toLowerCase());
                        list.add(sb.toString());
                    }
                    
                    msg = msg.replace("{ITEMS}", StringUtils.toString(list, true));
                }
                
                player.sendMessage(msg);
                return;
            }
            
            player.getInventory().removeItem(itemStack);
        }

        InvitationList.expireInvitation(guild, player);
        guild.addMember(user);
        user.setGuild(guild);
        IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getOfflineUser());

        MessageTranslator translator = new MessageTranslator()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", player.getName());

        player.sendMessage(translator.translate(messages.joinToMember));
        Bukkit.broadcastMessage(translator.translate(messages.broadcastJoin));

        Player owner = guild.getOwner().getPlayer();
        if (owner != null) {
            owner.sendMessage(translator.translate(messages.joinToOwner));
        }
    }

}
