package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.RandomizationUtils;
import net.dzikoysk.funnyguilds.util.SpaceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;
import java.util.Map;

public class EntityExplode implements Listener {

    private final FunnyGuilds plugin;

    public EntityExplode(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        List<Block> destroyed = event.blockList();
        Location loc = event.getLocation();
        Settings s = Settings.getInstance();

        List<Location> sphere = SpaceUtils.sphere(loc, s.explodeRadius, s.explodeRadius, false, true, 0);
        Map<Material, Double> materials = s.explodeMaterials;

        if (RegionUtils.isIn(loc)) {
            Region region = RegionUtils.getAt(loc);
            Guild guild = region.getGuild();

            if (guild.isValid()) {
                event.setCancelled(true);
                return;
            }

            Location protect = region.getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation();

            destroyed.removeIf(block -> block.getLocation().equals(protect));

            guild.setBuild(System.currentTimeMillis() + Settings.getInstance().regionExplode * 1000L);
            for (User user : guild.getMembers()) {
                Player player = this.plugin.getServer().getPlayer(user.getName());
                if (player != null) {
                    player.sendMessage(Messages.getInstance().getMessage("regionExplode")
                                               .replace("{TIME}", Integer.toString(Settings.getInstance().regionExplode)));
                }
            }
        }

        for (Location l : sphere) {
            Material material = l.getBlock().getType();
            if (!materials.containsKey(material)) {
                continue;
            }
            if (material == Material.WATER || material == Material.LAVA) {
                if (RandomizationUtils.chance(materials.get(material))) {
                    l.getBlock().setType(Material.AIR);
                }
            }
            else {
                if (RandomizationUtils.chance(materials.get(material))) {
                    l.getBlock().breakNaturally();
                }
            }
        }
    }

}