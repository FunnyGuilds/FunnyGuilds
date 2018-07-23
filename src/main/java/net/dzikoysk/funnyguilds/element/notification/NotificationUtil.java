package net.dzikoysk.funnyguilds.element.notification;

import com.google.common.collect.Lists;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.util.reflect.PacketCreator;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public final class NotificationUtil {

    private static final Class<?> PACKET_PLAY_OUT_TITLE_CLASS;
    private static final Class<?> PACKET_PLAY_OUT_CHAT_CLASS;
    private static final Class<?> TITLE_ACTION_CLASS;
    private static final Class<?> CHAT_MESSAGE_TYPE_CLASS;

    private static final Method CREATE_BASE_COMPONENT_NMS;
    private static final Method CREATE_BASE_COMPONENT_CRAFTBUKKIT;

    private static final String BASE_COMPONENT_JSON_PATTERN = "{\"text\": \"{TEXT}\"}";

    static {
        PACKET_PLAY_OUT_TITLE_CLASS = Reflections.getNMSClass("PacketPlayOutTitle");
        PACKET_PLAY_OUT_CHAT_CLASS = Reflections.getNMSClass("PacketPlayOutChat");
        TITLE_ACTION_CLASS = "v1_8_R1".equals(Reflections.SERVER_VERSION) ? Reflections.getNMSClass("EnumTitleAction") : Reflections.getNMSClass("PacketPlayOutTitle$EnumTitleAction");

        if ("v1_12_R1".equals(Reflections.SERVER_VERSION)) {
            CHAT_MESSAGE_TYPE_CLASS = Reflections.getNMSClass("ChatMessageType");
        } else {
            CHAT_MESSAGE_TYPE_CLASS = null;
        }
        
        CREATE_BASE_COMPONENT_NMS = Reflections.getMethod(Reflections.getNMSClass("IChatBaseComponent$ChatSerializer"), "a", String.class);
        CREATE_BASE_COMPONENT_CRAFTBUKKIT = Reflections.getMethod(Reflections.getCraftBukkitClass("util.CraftChatMessage"), "fromString", String.class, boolean.class);
    }

    public static List<Object> createTitleNotification(String text, String subText, int fadeIn, int stay, int fadeOut) {
        List<Object> packets = Lists.newArrayList();

        Object titlePacket = PacketCreator.of(PACKET_PLAY_OUT_TITLE_CLASS)
                        .create()
                        .withField("a", TITLE_ACTION_CLASS.getEnumConstants()[0])
                        .withField("b", createBaseComponent(text, false))
                        .withField("c", fadeIn)
                        .withField("d", stay)
                        .withField("e", fadeOut)
                        .getPacket();
        
        Object subtitlePacket = PacketCreator.of(PACKET_PLAY_OUT_TITLE_CLASS)
                        .create()
                        .withField("a", TITLE_ACTION_CLASS.getEnumConstants()[1])
                        .withField("b", createBaseComponent(subText, false))
                        .withField("c", fadeIn)
                        .withField("d", stay)
                        .withField("e", fadeOut)
                        .getPacket();

        packets.addAll(Arrays.asList(titlePacket, subtitlePacket));
        return packets;
    }

    public static Object createBaseComponent(String text, boolean keepNewLines) {
        String text0 = text != null ? text : "";
        
        try {
            return keepNewLines ? Array.get(CREATE_BASE_COMPONENT_CRAFTBUKKIT.invoke(null, text0, true), 0) : CREATE_BASE_COMPONENT_NMS.invoke(null, StringUtils.replace(BASE_COMPONENT_JSON_PATTERN, "{TEXT}", text0));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            FunnyLogger.exception(ex.getMessage(), ex.getStackTrace());
            return null;
        }
    }

    public static Object createActionbarNotification(String text) {
        Object actionbarPacket;

        if (CHAT_MESSAGE_TYPE_CLASS != null) {
            actionbarPacket = PacketCreator.of(PACKET_PLAY_OUT_CHAT_CLASS)
                    .create()
                    .withField("a", createBaseComponent(text, false))
                    .withField("b", CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2])
                    .getPacket();
        }
        else {
            actionbarPacket = PacketCreator.of(PACKET_PLAY_OUT_CHAT_CLASS)
                    .create()
                    .withField("a", createBaseComponent(text, false))
                    .withField("b", (byte) 2)
                    .getPacket();
        }

        
        return actionbarPacket;
    }

    private NotificationUtil() {}
}
