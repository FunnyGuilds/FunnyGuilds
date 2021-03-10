package net.dzikoysk.funnyguilds.system.security;

import com.google.common.collect.Streams;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MinecraftServerUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.PingUtils;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.panda_lang.utilities.commons.text.Joiner;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

public final class SecuritySystem {

    private static SecuritySystem instance;
    private final List<User> blocked = new ArrayList<>();

    public SecuritySystem() {
        instance = this;
    }

    public static SecuritySystem getSecurity() {
        if (instance == null) {
            new SecuritySystem();
        }
        
        return instance;
    }

    public boolean isCheating(Player player, Guild guild) {
        return isCheating(player, SecurityType.GUILD, guild);
    }

    private boolean isCheating(Player player, SecurityType securityType, Object... values) {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return false;
        }
        
        if (isBanned(User.get(player))) {
            return true;
        }

        Guild guild = null;

        for (Object value : values) {
            if (value instanceof Guild) {
                guild = (Guild) value;
            }
        }

        if (securityType.equals(SecurityType.GUILD)) {
            if (guild == null) {
                return false;
            }

            Region region = guild.getRegion();

            if (region == null) {
                return false;
            }

            double x = region.getCenter().getX() + 0.5;
            double y = region.getCenter().getY();
            double z = region.getCenter().getZ() + 0.5;

            Vector origin = player.getEyeLocation().toVector();
            Vector direction = player.getEyeLocation().getDirection();
            BoundingBox boundingBox = new BoundingBox(x - 1.0, y - 1.0 ,z - 1.0, x + 1.0, y + 1.0 ,z + 1.0);
            RayTraceResult rayTraceResult = boundingBox.rayTrace(origin, direction, 6);

            if (rayTraceResult == null) {
                return false;
            }

            Vector hitPoint = rayTraceResult.getHitPosition();
            double distance = hitPoint.distance(player.getEyeLocation().toVector());

            for (CheatType cheatType : CheatType.getByType(securityType)) {
                switch (cheatType) {
                    case FREECAM:
                        BlockIterator blockIterator = new BlockIterator(player.getWorld(), origin, hitPoint, 0, Math.max((int) distance, 1));
                        ArrayList<Block> blocks = Streams.stream(blockIterator)
                                .filter(block -> !block.isLiquid())
                                .filter(block -> !block.isPassable())
                                .filter(block -> block.getType().isSolid())
                                .limit(8)
                                .collect((toCollection(ArrayList::new)));


                        if (blocks.size() == 0) {
                            break;
                        }

                        ArrayList<String> materials = blocks.stream()
                                .map(block -> MaterialUtils.getMaterialName(block.getType()))
                                .collect(toCollection(ArrayList::new));

                        SecurityUtils.sendToOperator(player, cheatType, "Zaatakowal krysztal przez bloki &c" + Joiner.on(", ").join(materials));
                        blocked.add(User.get(player));
                        return true;
                    case REACH:
                        double compensation = player.getGameMode().equals(GameMode.CREATIVE) ? 4.5 : 3.0;
                        compensation += SecurityUtils.compensationMs(PingUtils.getPing(player));
                        compensation += SecurityUtils.compensationMs(1000.0 / MinecraftServerUtils.getRecentTPS(0));

                        if (distance < compensation) {
                            break;
                        }

                        SecurityUtils.sendToOperator(player, cheatType, "Zaatakowal krysztal z odleglosci &c" + distance + " kratek");
                        blocked.add(User.get(player));
                        return true;
                }
            }
        }
        
        return false;
    }

    public boolean isBanned(User user) {
        return blocked.contains(user);
    }

}
