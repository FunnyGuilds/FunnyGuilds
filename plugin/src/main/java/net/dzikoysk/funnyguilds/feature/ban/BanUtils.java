package net.dzikoysk.funnyguilds.feature.ban;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserBan;
import org.bukkit.ChatColor;
import panda.std.stream.PandaStream;

public final class BanUtils {

    private BanUtils() {
    }

    public static void ban(Guild guild, Duration time, String reason) {
        guild.setBan(Instant.now().plus(time));
        PandaStream.of(guild.getMembers())
                .map(member -> ban(member, time, reason))
                .forEach(member -> member.getProfile().kick(getBanMessage(member)));
    }

    public static User ban(User user, Duration time, String reason) {
        user.setBan(new UserBan(reason, Instant.now().plus(time)));
        return user;
    }

    public static void unban(Guild guild) {
        guild.getMembers().forEach(BanUtils::unban);
    }

    public static void unban(User user) {
        user.setBan(null);
    }

    public static void checkIfBanShouldExpire(User user) {
        user.getBan()
                .filterNot(UserBan::isBanned)
                .peek(time -> user.setBan(null));
    }

    public static String getBanMessage(User user) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        return user.getBan()
                .map(ban -> {
                    FunnyFormatter formatter = new FunnyFormatter()
                            .register("{NEWLINE}", ChatColor.RESET + "\n")
                            .register("{DATE}", messages.dateFormat.format(LocalDateTime.ofInstant(ban.getTime(), ZoneOffset.systemDefault())))
                            .register("{REASON}", ban.getReason())
                            .register("{PLAYER}", user.getName());

                    return formatter.format(messages.banMessage);
                })
                .orElseGet("");
    }

}
