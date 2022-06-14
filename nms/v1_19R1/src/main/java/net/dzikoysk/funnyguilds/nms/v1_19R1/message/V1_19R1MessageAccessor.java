package net.dzikoysk.funnyguilds.nms.v1_19R1.message;

import java.util.Collection;
import java.util.UUID;
import net.dzikoysk.funnyguilds.nms.api.message.MessageAccessor;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class V1_19R1MessageAccessor implements MessageAccessor {

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
            ((CraftPlayer) player).getHandle().b.a(titlePacket);    // a -> sendPacket
            ((CraftPlayer) player).getHandle().b.a(subtitlePacket); // a -> sendPacket
            ((CraftPlayer) player).getHandle().b.a(timesPacket);    // a -> sendPacket
        }
    }

    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Collection<? extends Player> players) {
        this.sendTitleMessage(titleMessage, players.toArray(new Player[0]));
    }

    @Override
    public void sendActionBarMessage(String text, Player... players) {
        //każde wywołanie NMS to jeden martwy kotek :(
        for (Player p : players) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
        }
    }

    @Override
    public void sendActionBarMessage(String text, Collection<? extends Player> players) {
        this.sendActionBarMessage(text, players.toArray(new Player[0]));
    }
}
