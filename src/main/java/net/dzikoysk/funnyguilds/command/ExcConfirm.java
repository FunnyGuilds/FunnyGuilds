package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcConfirm implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
        User lp = User.get(p);

        if (!lp.hasGuild()) {
            p.sendMessage(m.deleteHasNotGuild);
            return;
        }

        if (!lp.isOwner()) {
            p.sendMessage(m.deleteIsNotOwner);
            return;
        }

        if (Settings.getConfig().regionDeleteIfNear && lp.getGuild().isSomeoneInRegion()) {
            p.sendMessage(m.deleteSomeoneIsNear);
            return;
        }

        if (!ConfirmationList.contains(lp.getUUID())) {
            p.sendMessage(m.deleteToConfirm);
            return;
        }

        ConfirmationList.remove(lp.getUUID());
        String name = lp.getGuild().getName();
        String tag = lp.getGuild().getTag();
        GuildUtils.deleteGuild(lp.getGuild());

        p.sendMessage(m.deleteSuccessful.replace("{GUILD}", name).replace("{TAG}", tag));

        Bukkit.getServer().broadcastMessage(m.broadcastDelete.replace("{PLAYER}", p.getName()).replace("{GUILD}", name).replace("{TAG}", tag));
    }

}
