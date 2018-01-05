package net.dzikoysk.funnyguilds.util;

import com.google.common.collect.Lists;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.reflect.PacketCreator;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;

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

    private static final Method CREATE_BASE_COMPONENT;

    static {
        PACKET_PLAY_OUT_TITLE_CLASS = Reflections.getCraftClass("PacketPlayOutTitle");
        PACKET_PLAY_OUT_CHAT_CLASS = Reflections.getCraftClass("PacketPlayOutChat");
        TITLE_ACTION_CLASS =  Reflections.getFixedVersion().equals("v1_8_R1") ? Reflections.getCraftClass("EnumTitleAction") : Reflections.getCraftClass("PacketPlayOutTitle$EnumTitleAction");
        if ("v1_12_R1".equals(Reflections.getFixedVersion())) {
            CHAT_MESSAGE_TYPE_CLASS = Reflections.getCraftClass("ChatMessageType");
        } else {
            CHAT_MESSAGE_TYPE_CLASS = null;
        }

        CREATE_BASE_COMPONENT = Reflections.getMethod(Reflections.getBukkitClass("util.CraftChatMessage"), "fromString", String.class, boolean.class);
    }

    public static List<Object> createTitleNotification(String text, String subText, int fadeIn, int stay, int fadeOut) {
        List<Object> packets = Lists.newArrayList();

        Object titlePacket = PacketCreator.of(PACKET_PLAY_OUT_TITLE_CLASS)
                .create()
                .withField("a", TITLE_ACTION_CLASS.getEnumConstants()[0])
                .withField("b", createBaseComponent(text, false))
                .withField("c", -1)
                .withField("d", -1)
                .withField("e", -1)
                .getPacket();
        Object subtitlePacket = PacketCreator.of(PACKET_PLAY_OUT_TITLE_CLASS)
                .create()
                .withField("a", TITLE_ACTION_CLASS.getEnumConstants()[1])
                .withField("b", createBaseComponent(subText, false))
                .withField("c", -1)
                .withField("d", -1)
                .withField("e", -1)
                .getPacket();
        Object timerPacket = PacketCreator.of(PACKET_PLAY_OUT_TITLE_CLASS)
                .create()
                .withField("a", TITLE_ACTION_CLASS.getEnumConstants()[2])
                .withField("c", fadeIn)
                .withField("d", stay)
                .withField("e", fadeOut)
                .getPacket();

        packets.addAll(Arrays.asList(titlePacket, subtitlePacket, timerPacket));
        return packets;
    }

    public static Object createBaseComponent(String text, boolean keepNewLines) {
        String text0 = text != null ? text : "";
        try {
            return Array.get(CREATE_BASE_COMPONENT.invoke(null, text0, keepNewLines), 0);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
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
        } else {
            actionbarPacket = PacketCreator.of(PACKET_PLAY_OUT_CHAT_CLASS)
                    .create()
                    .withField("a", createBaseComponent(text, false))
                    .withField("b", (byte) 2)
                    .getPacket();
        }

        return actionbarPacket;
    }

    private NotificationUtil() {

    }
}
