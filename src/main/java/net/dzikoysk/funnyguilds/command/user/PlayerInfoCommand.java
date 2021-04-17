package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class PlayerInfoCommand {

    @FunnyCommand(
        name = "${user.player.name}",
        description = "${user.player.description}",
        aliases = "${user.player.aliases}",
        permission = "funnyguilds.playerinfo",
        completer = "online-players:3",
        acceptsExceeded = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length == 0 && !(sender instanceof Player), messages.playerOnly);
        
        String name = args.length == 0
                ? sender.getName()
                : args[0];

        User user = UserUtils.get(name, config.playerLookupIgnorecase);
        when (user == null, messages.generalNotPlayedBefore);

        sendInfoMessage(messages.playerInfoList, user, sender);
    }
    
    public void sendInfoMessage(List<String> baseMessage, User infoUser, CommandSender messageTarget) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        Rank rank = infoUser.getRank();

        for (String messageLine : baseMessage) {
            if (infoUser.hasGuild()) {
                messageLine = StringUtils.replace(messageLine, "{GUILD}", infoUser.getGuild().getName());
                messageLine = StringUtils.replace(messageLine, "{TAG}", infoUser.getGuild().getTag());
            }
            else {
                messageLine = StringUtils.replace(messageLine, "{GUILD}", messages.gNameNoValue);
                messageLine = StringUtils.replace(messageLine, "{TAG}", messages.gTagNoValue);
            }

            messageLine = StringUtils.replace(messageLine, "{PLAYER}", infoUser.getName());
            messageLine = StringUtils.replace(messageLine, "{POINTS-FORMAT}", IntegerRange.inRangeToString(rank.getPoints(), config.pointsFormat));
            messageLine = StringUtils.replace(messageLine, "{POINTS}", Integer.toString(rank.getPoints()));
            messageLine = StringUtils.replace(messageLine, "{KILLS}", Integer.toString(rank.getKills()));
            messageLine = StringUtils.replace(messageLine, "{DEATHS}", Integer.toString(rank.getDeaths()));
            messageLine = StringUtils.replace(messageLine, "{ASSISTS}", Integer.toString(rank.getAssists()));
            messageLine = StringUtils.replace(messageLine, "{LOGOUTS}", Integer.toString(rank.getLogouts()));
            messageLine = StringUtils.replace(messageLine, "{KDR}", String.format(Locale.US, "%.2f", rank.getKDR()));
            messageLine = StringUtils.replace(messageLine, "{RANK}", Integer.toString(rank.getPosition()));
            
            messageTarget.sendMessage(messageLine);
        }
    }

}
