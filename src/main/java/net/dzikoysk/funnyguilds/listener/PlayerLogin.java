package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.guild.GuildRegex;
import net.dzikoysk.funnyguilds.system.ban.BanSystem;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerLogin implements Listener {

    private final FunnyGuilds plugin;

    public PlayerLogin(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (Bukkit.hasWhitelist()) {
            return;
        }
        
        String name = event.getPlayer().getName();
        if (name.length() < 2) {
            event.disallow(Result.KICK_OTHER, ChatUtils.colored("&cNick jest za krotki!"));
        }
        
        if (name.length() > 16) {
            event.disallow(Result.KICK_OTHER, ChatUtils.colored("&cNick jest za dlugi!"));
        }
        
        if (!name.matches(GuildRegex.LETTERS_DIGITS_UNDERSCORE.getPattern())) {
            event.disallow(Result.KICK_OTHER, ChatUtils.colored("&cNick zawiera niedozwolone znaki!"));
        }

        User user = plugin.getUserManager().getUser(event.getPlayer());
        BanSystem banSystem = plugin.getSystemManager().getBanSystem();

        if (user == null) {
            return;
        }

        banSystem.checkIfBanShouldExpire(user);

        if (!user.isBanned()) {
            return;
        }
        
        event.disallow(Result.KICK_BANNED, banSystem.getBanMessage(user));
    }

}
