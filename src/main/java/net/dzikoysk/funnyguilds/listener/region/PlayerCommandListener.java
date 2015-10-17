package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("funnyguilds.admin"))
            return;

        String[] splited = event.getMessage().split("\\s+");
        if (splited.length == 0)
            return;
        String command = splited[0];
        for (String s : Settings.getInstance().regionCommands) {
            if (("/" + s).equalsIgnoreCase(command)) {
                command = null;
                break;
            }
        }
        if (command != null)
            return;

        Region region = RegionUtils.getAt(player.getLocation());
        if (region == null)
            return;

        Guild guild = region.getGuild();
        User user = User.get(player);
        if (guild.getMembers().contains(user))
            return;

        event.setCancelled(true);
        player.sendMessage(Messages.getInstance().getMessage("regionCommand"));
    }

}
