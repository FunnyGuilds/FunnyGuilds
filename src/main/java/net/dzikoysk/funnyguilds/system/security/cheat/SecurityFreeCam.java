package net.dzikoysk.funnyguilds.system.security.cheat;

import com.google.common.collect.Streams;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.system.security.SecurityUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.text.Joiner;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SecurityFreeCam {

    private SecurityFreeCam() {}

    public static void on(Player player, Vector origin, Vector hitPoint, double distance, SecuritySystem system) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        MessageConfiguration messages = plugin.getMessageConfiguration();
        PluginConfiguration config = plugin.getPluginConfiguration();
        Vector directionToHitPoint = hitPoint.clone().subtract(origin);
        BlockIterator blockIterator = new BlockIterator(player.getWorld(), origin, directionToHitPoint, 0, Math.max((int) distance, 1));
        /* compensationSneaking will be removed after add the cursor height check on each client version. */
        int compensationSneaking = player.isSneaking() ? 1 : 0;
        List<Block> blocks = Streams.stream(blockIterator)
                .filter(block -> !block.isLiquid())
                .filter(block -> !block.isPassable())
                .filter(block -> block.getType().isSolid())
                .filter(block -> block.getType().isOccluding() || block.getType().equals(Material.GLASS))
                .limit(8)
                .collect(toList());


        if (blocks.size() <= config.freeCamCompensation + compensationSneaking) {
            return;
        }

        User user = plugin.getUserManager().getUser(player);
        String message = messages.securitySystemFreeCam;
        String blocksInfo = Joiner.on(", ").join(blocks, b -> MaterialUtils.getMaterialName(b.getType())).toString();

        message = StringUtils.replace(message, "{BLOCKS}", blocksInfo);
        system.addViolationLevel(user);
        SecurityUtils.sendToOperator(player, "FreeCam", message);
    }

}
