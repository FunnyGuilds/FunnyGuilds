package net.dzikoysk.funnyguilds.system.security.cheat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.system.security.SecurityUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MinecraftServerUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.PingUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class SecurityReach {

    private static final DecimalFormat FORMAT = new DecimalFormat("##.##");

    private SecurityReach() {}

    public static void on(Player player, double distance) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        double compensation = player.getGameMode().equals(GameMode.CREATIVE) ? 4.5 : 3.0;
        compensation += FunnyGuilds.getInstance().getPluginConfiguration().reachCompensation;
        compensation += SecurityUtils.compensationMs(PingUtils.getPing(player) * 0.93);
        compensation += SecurityUtils.compensationMs((1000 / MinecraftServerUtils.getRecentTPS(0) - 50) * 10);

        if (distance < compensation) {
            return;
        }

        String message = messages.SecuritySystemReach;
        message = StringUtils.replace(message, "{DISTANCE}", FORMAT.format(distance));

        SecurityUtils.addVL(User.get(player));
        SecurityUtils.sendToOperator(player, "Reach", message);
    }


}
