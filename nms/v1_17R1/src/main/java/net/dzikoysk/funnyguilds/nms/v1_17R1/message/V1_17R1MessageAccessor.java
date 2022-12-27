package net.dzikoysk.funnyguilds.nms.v1_17R1.message;

import net.dzikoysk.funnyguilds.nms.api.message.MessageAccessor;
import net.dzikoysk.funnyguilds.nms.api.message.TitleMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class V1_17R1MessageAccessor implements MessageAccessor {

    @Override
    public void sendTitleMessage(TitleMessage titleMessage, Player... players) {
        for (Player player : players) {
            player.sendTitle(titleMessage.getText(), titleMessage.getSubText(), titleMessage.getFadeInDuration(), titleMessage.getStayDuration(), titleMessage.getFadeOutDuration());
        }
    }

    @Override
    public void sendActionBarMessage(String text, Player... players) {
        for (Player player : players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
        }
    }

}
