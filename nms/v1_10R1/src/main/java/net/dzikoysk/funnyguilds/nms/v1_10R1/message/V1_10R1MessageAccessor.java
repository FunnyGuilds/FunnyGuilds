package net.dzikoysk.funnyguilds.nms.v1_10R1.message;

import net.dzikoysk.funnyguilds.nms.api.message.MessageAccessor;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_10R1MessageAccessor implements MessageAccessor {
    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Player... players) {
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(
                EnumTitleAction.TITLE,
                CraftChatMessage.fromString(titleMessage.getText(), false)[0]
        );
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(
                EnumTitleAction.SUBTITLE,
                CraftChatMessage.fromString(titleMessage.getSubText(), false)[0]
        );
        PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(
                titleMessage.getFadeInDuration(),
                titleMessage.getStayDuration(),
                titleMessage.getFadeOutDuration()
        );

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timesPacket);
        }
    }

    @Override
    public void sendActionBarMessage(String text, Player... players) {
        for (Player player : players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
        }
    }

}
