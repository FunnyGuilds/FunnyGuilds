package net.dzikoysk.funnyguilds.util;

import com.google.common.collect.Lists;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.reflect.PacketCreator;
import net.dzikoysk.funnyguilds.util.reflect.PacketSender;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public final class NotificationUtil {

    private static final Class<?> packetPlayOutTitleClass;
    private static final Class<?> packetPlayOutChatClass;
    private static final Class<?> titleActionClass;

    private static final Method createBaseComponent;

    static {
        packetPlayOutTitleClass = Reflections.getCraftClass("PacketPlayOutTitle");
        packetPlayOutChatClass = Reflections.getCraftClass("PacketPlayOutChat");
        titleActionClass = Reflections.getCraftClass("PacketPlayOutTitle$EnumTitleAction");

        createBaseComponent = Reflections.getMethod(Reflections.getBukkitClass("util.CraftChatMessage"), "fromString", String.class, boolean.class);
    }

    public static List<Object> createTitleNotification(String text, String subText, int fadeIn, int stay, int fadeOut) {
        final List<Object> packets = Lists.newArrayList();

        final Object titlePacket = PacketCreator.of(packetPlayOutTitleClass)
                .withField("a", titleActionClass.getEnumConstants()[0])
                .withField("b", createBaseComponent(text, false))
                .withField("c", -1)
                .withField("d", -1)
                .withField("e", -1)
                .create();
        final Object subtitlePacket = PacketCreator.of(packetPlayOutTitleClass)
                .withField("a", titleActionClass.getEnumConstants()[1])
                .withField("b", createBaseComponent(text, false))
                .withField("c", -1)
                .withField("d", -1)
                .withField("e", -1)
                .create();
        final Object timerPacket = PacketCreator.of(packetPlayOutTitleClass)
                .withField("a", titleActionClass.getEnumConstants()[2])
                .withField("b", null)
                .withField("c", fadeIn)
                .withField("d", stay)
                .withField("e", fadeOut)
                .create();

        packets.addAll(Arrays.asList(titlePacket, subtitlePacket, timerPacket));
        return packets;
    }

    public static Object createBaseComponent(String text, boolean keepNewLines) {
        String text0 = text != null ? text : "";
        try {
            return Array.get(createBaseComponent.invoke(null, text0, keepNewLines), 0);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            FunnyGuilds.exception(ex.getMessage(), ex.getStackTrace());
            return null;
        }
    }

    public static Object createActionbarNotification(String text) {
        final Object actionbarPacket = PacketCreator.of(packetPlayOutChatClass)
                .withField("a", createBaseComponent(text, false))
                .withField("b", (byte) 2)
                .create();

        return actionbarPacket;
    }

    public static void sendNotification(Player player, List<Object> notifications) {
        PacketSender.sendPacket(player, notifications);
    }

    private NotificationUtil() {

    }

}
