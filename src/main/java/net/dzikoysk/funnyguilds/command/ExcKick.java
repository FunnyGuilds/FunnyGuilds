package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.MessageTranslator;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcKick implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.kickHasNotGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.kickIsNotOwner);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.kickPlayer);
            return;
        }

        User formerUser = User.get(args[0]);
        OfflineUser formerOffline = formerUser.getOfflineUser();

        if (!formerUser.hasGuild()) {
            player.sendMessage(messages.kickToHasNotGuild);
            return;
        }

        if (!user.getGuild().equals(formerUser.getGuild())) {
            player.sendMessage(messages.kickOtherGuild);
            return;
        }

        if (formerUser.isOwner()) {
            player.sendMessage(messages.kickOwner);
            return;
        }

        Guild guild = user.getGuild();
        IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, formerOffline);

        guild.removeMember(formerUser);
        formerUser.removeGuild();

        if (formerOffline.isOnline()) {
            IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, player);
        }

        MessageTranslator translator = new MessageTranslator()
                .register("{PLAYER}", formerUser.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        player.sendMessage(translator.translate(messages.kickToOwner));
        Bukkit.broadcastMessage(translator.translate(messages.broadcastKick));

        Player formerPlayer = formerUser.getPlayer();

        if (formerPlayer != null) {
            formerPlayer.sendMessage(translator.translate(messages.kickToPlayer));
        }
    }

}
