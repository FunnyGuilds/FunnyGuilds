package net.dzikoysk.funnyguilds.feature.security.cheat;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.security.SecurityUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import panda.utilities.text.Joiner;
import pl.peridot.yetanothermessageslibrary.replace.replacement.Replacement;
import static java.util.stream.Collectors.toList;

public final class SecurityFreeCam {

    private SecurityFreeCam() {
    }

    public static void on(Player player, Vector origin, Vector hitPoint, double distance) {
        FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
        MessageService messages = funnyGuilds.getMessageService();
        PluginConfiguration config = funnyGuilds.getPluginConfiguration();
        UserManager userManager = funnyGuilds.getUserManager();

        Vector directionToHitPoint = hitPoint.clone().subtract(origin);
        BlockIterator blockIterator = new BlockIterator(player.getWorld(), origin, directionToHitPoint, 0, Math.max((int) distance, 1));

        //TODO: compensationSneaking will be removed after add the cursor height check on each client version.
        int compensationSneaking = player.isSneaking() ? 1 : 0;
        List<Block> blocks = StreamSupport.stream(Spliterators.spliteratorUnknownSize(blockIterator, Spliterator.NONNULL | Spliterator.IMMUTABLE), false)
                .filter(block -> !block.isLiquid())
                .filter(block -> block.getType().isSolid())
                .filter(block -> block.getType().isOccluding() || block.getType() == Material.GLASS)
                .limit(8)
                .collect(toList());

        if (blocks.size() <= config.freeCamCompensation + compensationSneaking) {
            return;
        }

        String blocksString = Joiner.on(", ").join(blocks, b -> MaterialUtils.getMaterialName(b.getType())).toString();

        SecurityUtils.addViolationLevel(userManager.findByPlayer(player).orNull());
        SecurityUtils.sendToOperator(player, CheatType.FREE_CAM, Replacement.of("{BLOCKS}", blocksString));
    }

}
