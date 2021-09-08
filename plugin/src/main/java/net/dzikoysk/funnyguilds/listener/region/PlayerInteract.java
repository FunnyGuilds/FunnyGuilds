package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.feature.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class PlayerInteract implements Listener {

    private final InfoCommand infoExecutor = new InfoCommand();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();
        Action eventAction = event.getAction();
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();

        if (eventAction != Action.RIGHT_CLICK_BLOCK && eventAction != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (clicked == null) {
            return;
        }

        Region region = RegionUtils.getAt(clicked.getLocation());

        if (region == null) {
            return;
        }

        Block heart = region.getCenter().getBlock().getRelative(BlockFace.DOWN);

        if (clicked.equals(heart)) {
            if (heart.getType() == Material.DRAGON_EGG) {
                event.setCancelled(true);
            }

            Guild guild = region.getGuild();

            if (SecuritySystem.onHitCrystal(player, guild)) {
                return;
            }

            event.setCancelled(true);

            if (eventAction == Action.LEFT_CLICK_BLOCK) {
                WarSystem.getInstance().attack(player, guild);
                return;
            }

            if (!config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                try {
                    infoExecutor.execute(plugin, config, plugin.getMessageConfiguration(), player, new String[] { guild.getTag() });
                } catch (ValidationException validatorException) {
                    validatorException.getValidationMessage().peek(player::sendMessage);
                }
            }

            return;
        }

        if (eventAction == Action.RIGHT_CLICK_BLOCK) {
            Guild guild = region.getGuild();

            if (guild == null || guild.getName() == null) {
                return;
            }

            User user = UserUtils.get(player.getUniqueId());
            boolean blocked = config.blockedInteract.contains(clicked.getType());

            if (guild.getMembers().contains(user)) {
                event.setCancelled(blocked && config.regionExplodeBlockInteractions && !guild.canBuild());
            } else {
                event.setCancelled(blocked && !player.hasPermission("funnyguilds.admin.interact"));
            }
        }

    }

}
