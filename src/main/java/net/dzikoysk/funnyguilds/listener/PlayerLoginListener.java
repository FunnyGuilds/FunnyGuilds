package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (Bukkit.hasWhitelist())
            return;
        String name = event.getPlayer().getName();
        if (name.length() < 2)
            event.disallow(Result.KICK_OTHER, StringUtils.colored("&cNick jest za krotki!"));
        if (name.length() > 16)
            event.disallow(Result.KICK_OTHER, StringUtils.colored("&cNick jest za dlugi!"));
        if (!name.matches("[a-zA-Z0-9_]+"))
            event.disallow(Result.KICK_OTHER, StringUtils.colored("&cNick zawiera niedozwolone znaki!"));
        if (!UserUtils.playedBefore(event.getPlayer().getName()))
            return;

        User user = User.get(event.getPlayer());
        if (!user.isBanned())
            return;
        if (!BanUtils.check(user))
            return;
        event.disallow(Result.KICK_BANNED, BanUtils.getBanMessage(user));
    }

}
