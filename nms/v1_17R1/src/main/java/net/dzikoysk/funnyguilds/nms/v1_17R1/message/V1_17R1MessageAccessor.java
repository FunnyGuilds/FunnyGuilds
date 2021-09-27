package net.dzikoysk.funnyguilds.nms.v1_17R1.message;

import java.util.Collection;
import java.util.UUID;
import net.dzikoysk.funnyguilds.nms.api.message.MessageAccessor;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_17R1MessageAccessor implements MessageAccessor {
    private static final UUID SENDER_ALWAYS_DISPLAY = new UUID(0L, 0L);

    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Player... players) {
        ClientboundSetTitleTextPacket titlePacket =
                new ClientboundSetTitleTextPacket(CraftChatMessage.fromStringOrNull(titleMessage.getText(), false));
        ClientboundSetSubtitleTextPacket subtitlePacket =
                new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromStringOrNull(titleMessage.getSubText(), false));
        ClientboundSetTitlesAnimationPacket timesPacket =
                new ClientboundSetTitlesAnimationPacket(titleMessage.getFadeInDuration(), titleMessage.getStayDuration(), titleMessage.getFadeOutDuration());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().b.sendPacket(titlePacket);
            ((CraftPlayer) player).getHandle().b.sendPacket(subtitlePacket);
            ((CraftPlayer) player).getHandle().b.sendPacket(timesPacket);
        }
    }

    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Collection<? extends Player> players) {
        this.sendTitleMessage(titleMessage, players.toArray(new Player[0]));
    }

    @Override
    public void sendActionBarMessage(String text, Player... players) {
        PacketPlayOutChat actionBarPacket =
                new PacketPlayOutChat(CraftChatMessage.fromStringOrNull(text, true), ChatMessageType.c, SENDER_ALWAYS_DISPLAY);

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().b.sendPacket(actionBarPacket);
        }
    }

    @Override
    public void sendActionBarMessage(String text, Collection<? extends Player> players) {
        this.sendActionBarMessage(text, players.toArray(new Player[0]));
    }
}
