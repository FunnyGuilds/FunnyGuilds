package net.dzikoysk.funnyguilds.system.security.cheat;

import com.google.common.collect.Streams;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.system.security.SecurityUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.text.Joiner;

import java.util.ArrayList;

import static java.util.stream.Collectors.toCollection;

public class SecurityFreeCam {

    private SecurityFreeCam() {}

    public static void on(Player player, Vector origin, Vector hitPoint, double distance) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        BlockIterator blockIterator = new BlockIterator(player.getWorld(), origin, hitPoint, 0, Math.max((int) distance, 1));
        ArrayList<Block> blocks = Streams.stream(blockIterator)
                .filter(block -> !block.getType().isAir())
                .filter(block -> !block.isLiquid())
                .filter(block -> !block.isPassable())
                .filter(block -> block.getType().isSolid())
                .filter(block -> block.getType().isOccluding() || block.getType().equals(Material.GLASS))
                .limit(8)
                .collect((toCollection(ArrayList::new)));


        if (blocks.size() <= config.freeCamCompensation) {
            return;
        }

        String message = messages.SecuritySystemFreeCam;
        message = StringUtils.replace(message, "{BLOCKS}, ", Joiner.on(", ").join(blocks, b -> MaterialUtils.getMaterialName(b.getType())).toString());

        SecurityUtils.addVL(User.get(player));
        SecurityUtils.sendToOperator(player, "FreeCam", message);
    }

}
