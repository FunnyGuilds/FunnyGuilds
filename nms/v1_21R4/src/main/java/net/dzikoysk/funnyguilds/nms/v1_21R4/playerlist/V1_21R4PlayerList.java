package net.dzikoysk.funnyguilds.nms.v1_21R4.playerlist;

import com.mojang.authlib.GameProfile;
import java.util.EnumSet;
import net.dzikoysk.funnyguilds.nms.api.ProtocolDependentHelper;
import net.dzikoysk.funnyguilds.nms.v1_20R5.playerlist.V1_20R5PlayerList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import org.bukkit.entity.Player;

public class V1_21R4PlayerList extends V1_20R5PlayerList {

    // base chosen randomly to override other entries
    private static final int PRIORITY_BASE = 999;

    public V1_21R4PlayerList(int cellCount) {
        super(cellCount);
    }

    @Override
    protected String getGameProfileName(
            Player player,
            String paddedIdentifier
    ) {
        return ProtocolDependentHelper.EMPTY_IDENTIFIER;
    }

    @Override
    protected Entry createPlayerInfoData(
            int index,
            GameProfile gameProfile,
            int ping,
            Component component
    ) {
        // higher priority first
        int cellPriority = PRIORITY_BASE - index;
        return new Entry(
                gameProfile.getId(),
                gameProfile,
                true,
                ping,
                DEFAULT_GAME_MODE,
                component,
                false,
                cellPriority,
                null
        );
    }

    @Override
    protected EnumSet<Action> getAddPlayerActions() {
        return EnumSet.of(
                Action.ADD_PLAYER,
                Action.UPDATE_GAME_MODE,
                Action.UPDATE_LISTED,
                Action.UPDATE_LATENCY,
                Action.UPDATE_DISPLAY_NAME,
                Action.UPDATE_LIST_ORDER
        );
    }

}
