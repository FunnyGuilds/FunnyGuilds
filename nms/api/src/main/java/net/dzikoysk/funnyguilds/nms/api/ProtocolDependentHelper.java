package net.dzikoysk.funnyguilds.nms.api;

import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public class ProtocolDependentHelper {
    private static final int V1_19_3_PROTOCOL_VERSION = 761;

    private static boolean viaApiAvailable = false;

    static {
        try {
            Class.forName("com.viaversion.viaversion.api.ViaAPI");
            viaApiAvailable = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static String getGameProfileNameBasedOnPlayerProtocolVersion(Player player, String paddedIdentifier, String defaultIdentifier) {
        // NOTE: Sorting changed in 1.19.3 and GameProfile name is being included in the sorting process.
        //       To mitigate it when server is using ViaVersion and client protocol version mismatches with the server protocol version,
        //       we're checking the protocol version that player uses and return proper identifier for specific version.
        Integer playerProtocolVersion = getPlayerProtocolVersion(player);

        if (playerProtocolVersion == null) {
            return defaultIdentifier;
        }

        if (playerProtocolVersion >= V1_19_3_PROTOCOL_VERSION) {
            return paddedIdentifier;
        }

        return " ";
    }

    private static Integer getPlayerProtocolVersion(Player player) {
        if (!viaApiAvailable) {
            return null;
        }

        return getPlayerProtocolVersionImpl(player);
    }

    private static Integer getPlayerProtocolVersionImpl(Player player) {
        return Via.getAPI().getPlayerVersion(player.getUniqueId());
    }
}
