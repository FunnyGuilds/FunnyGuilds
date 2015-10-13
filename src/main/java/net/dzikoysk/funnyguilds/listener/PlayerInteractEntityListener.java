package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.command.ExcPlayer;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof Player) {
            if (Settings.getInstance().infoPlayerSneaking && !event.getPlayer().isSneaking())
                return;
            Player clicked = (Player) entity;
            new ExcPlayer().execute(event.getPlayer(), new String[] {clicked.getName()});
        } else if (entity instanceof EnderCrystal) {
            EnderCrystal ec = (EnderCrystal) entity;
            Region region = RegionUtils.getAt(ec.getLocation());
            if (region == null)
                return;
            event.setCancelled(true);
            if (region.getCenter().getBlock().getRelative(BlockFace.UP).getLocation().toVector()
                    .equals(ec.getLocation().getBlock().getLocation().toVector())) {
                Guild guild = region.getGuild();
                if (guild == null)
                    return;
                new ExcInfo().execute(event.getPlayer(), new String[] {guild.getTag()});
            }
        }
    }

}
