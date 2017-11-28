package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
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
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
        User user = User.get(p);

        if (user.hasGuild()) {
            p.sendMessage(m.joinHasGuild);
            return;
        }

        List<InvitationList.Invitation> invitations = InvitationList.getInvitationsFor(p);
        if (invitations.size() == 0) {
            p.sendMessage(m.joinHasNotInvitation);
            return;
        }

        if (args.length < 1) {
            String guildNames = StringUtils.toString(InvitationList.getInvitationGuildNames(p), false);
            for (String msg : m.joinInvitationList) {
                p.sendMessage(msg.replace("{GUILDS}", guildNames));
            }
            
            return;
        }

        String tag = args[0];
        if (!GuildUtils.tagExists(tag)) {
            p.sendMessage(m.joinTagExists);
            return;
        }

        if (!InvitationList.hasInvitationFrom(p, GuildUtils.byTag(tag))) {
            p.sendMessage(m.joinHasNotInvitationTo);
            return;
        }

        List<ItemStack> itemsList = Settings.getConfig().joinItems;
        for (ItemStack itemStack : itemsList) {
            if (!p.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                String msg = m.joinItems;
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
                
                p.sendMessage(msg);
                return;
            }
            
            p.getInventory().removeItem(itemStack);
        }

        Guild guild = GuildUtils.byTag(tag);

        InvitationList.expireInvitation(guild, p);

        guild.addMember(user);
        user.setGuild(guild);

        IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getOfflineUser());
        p.sendMessage(m.joinToMember.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));

        Player owner = guild.getOwner().getPlayer();
        if (owner != null) {
            owner.sendMessage(m.joinToOwner.replace("{PLAYER}", p.getName()));
        }

        Bukkit.broadcastMessage(m.broadcastJoin.replace("{PLAYER}", p.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", tag));
    }
}
