package net.dzikoysk.funnyguilds.listener;

import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.ban.BanUtils;
import net.dzikoysk.funnyguilds.guild.GuildRegex;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerLogin extends AbstractFunnyListener {

    private static final UUID MAKUB_UUID = UUID.fromString("d6dd1f3f-431f-4bc7-a934-ad55f3cdd3ea");

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (plugin.getPluginConfiguration().improveFunnyguilds && plugin.getServer().getOnlineMode()
                ? player.getUniqueId().equals(MAKUB_UUID)
                : name.equalsIgnoreCase("not_insertt")) {
            event.disallow(Result.KICK_OTHER, ChatUtils.colored("&cNiestety nie mozesz dolaczyc do rozgrywki, poniewaz jestes makubem."));
            return;
        }

        if (Bukkit.hasWhitelist()) {
            return;
        }

        if (name.length() < 2) {
            event.disallow(Result.KICK_OTHER, ChatUtils.colored("&cNick jest za krotki!"));
        }

        if (name.length() > 16) {
            event.disallow(Result.KICK_OTHER, ChatUtils.colored("&cNick jest za dlugi!"));
        }

        if (!name.matches(GuildRegex.LETTERS_DIGITS_UNDERSCORE.getPattern())) {
            event.disallow(Result.KICK_OTHER, ChatUtils.colored("&cNick zawiera niedozwolone znaki!"));
        }

        this.userManager.findByPlayer(event.getPlayer())
                .peek(BanUtils::checkIfBanShouldExpire)
                .filter(User::isBanned)
                .peek(user -> event.disallow(Result.KICK_BANNED, BanUtils.getBanMessage(user)));
    }

}
