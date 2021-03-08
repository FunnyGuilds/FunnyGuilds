package net.dzikoysk.funnyguilds.system.security;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class SecuritySystem {

    private static SecuritySystem instance;
    private final List<User> blocked;

    public SecuritySystem() {
        instance = this;
        blocked = new ArrayList<>();
    }

    public static SecuritySystem getSecurity() {
        if (instance == null) {
            new SecuritySystem();
        }
        
        return instance;
    }

    public boolean isCheating(Player player, Guild guild) {
        for (CheatType type : CheatType.getByType(SecurityType.GUILD)) {
            if (isCheating(player, type, guild)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isCheating(Player player, CheatType type, Object... values) {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return false;
        }
        
        if (isBanned(User.get(player))) {
            return true;
        }
        
        switch (type) {
            case FREECAM:
                Guild guild = null;

                for (Object value : values) {
                    if (value instanceof Guild) {
                        guild = (Guild) value;
                    }
                }

                if (guild == null) {
                    return false;
                }

                Region region = guild.getRegion();

                if (region == null) {
                    return false;
                }

                double distance = region.getCenter().distance(player.getEyeLocation());

                if (distance < 5.2) {
                    return false;
                }

                sendToOperator(player, type, "Zaatakowal krysztal z odleglosci &c" + distance + " kratek");
                blocked.add(User.get(player));
                return true;
        }
        
        return false;
    }

    public void sendToOperator(Player player, CheatType type, String message) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!(onlinePlayer.isOp() || onlinePlayer.hasPermission("funnyguilds.admin"))) {
                continue;
            }

            onlinePlayer.sendMessage(SecurityUtils.getBustedMessage(player, type));
            onlinePlayer.sendMessage(SecurityUtils.getNoteMessage(message));
        }
    }

    public boolean isBanned(User user) {
        return blocked.contains(user);
    }

}
