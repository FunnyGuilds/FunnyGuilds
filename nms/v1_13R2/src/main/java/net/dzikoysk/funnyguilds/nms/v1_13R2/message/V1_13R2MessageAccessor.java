package net.dzikoysk.funnyguilds.nms.v1_13R2.message;

import java.util.Collection;
import net.dzikoysk.funnyguilds.nms.api.message.MessageAccessor;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import net.minecraft.server.v1_13_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_13_R2.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_13R2MessageAccessor implements MessageAccessor {

    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Player... players) {
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE,
                CraftChatMessage.fromStringOrNull(titleMessage.getText(), false));
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
                CraftChatMessage.fromStringOrNull(titleMessage.getSubText(), false));
        PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(titleMessage.getFadeInDuration(),
                titleMessage.getStayDuration(), titleMessage.getFadeOutDuration());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timesPacket);
        }
    }

    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Collection<? extends Player> players) {
        this.sendTitleMessage(titleMessage, players.toArray(new Player[0]));
    }

    @Override
    public void sendActionBarMessage(String text, Player... players) {
        PacketPlayOutChat actionBarPacket = new PacketPlayOutChat(CraftChatMessage.fromStringOrNull(text, true), ChatMessageType.GAME_INFO);

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionBarPacket);
        }
    }

    @Override
    public void sendActionBarMessage(String text, Collection<? extends Player> players) {
        this.sendActionBarMessage(text, players.toArray(new Player[0]));
    }

}
