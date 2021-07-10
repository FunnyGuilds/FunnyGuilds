package net.dzikoysk.funnyguilds.element.notification;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class NotificationUtil {

    private static final Method CREATE_BASE_COMPONENT_NMS;
    private static final Method CREATE_BASE_COMPONENT_CRAFTBUKKIT;

    private static final String BASE_COMPONENT_JSON_PATTERN = "{\"text\": \"{TEXT}\"}";

    static {
        CREATE_BASE_COMPONENT_NMS = "v1_8_R1".equals(Reflections.SERVER_VERSION) ? Reflections.getMethod(Reflections.getNMSClass("ChatSerializer"), "a", String.class) : Reflections.getMethod(Reflections.getNMSClass("IChatBaseComponent$ChatSerializer"), "a", String.class);
        CREATE_BASE_COMPONENT_CRAFTBUKKIT = Reflections.getMethod(Reflections.getCraftBukkitClass("util.CraftChatMessage"), "fromString", String.class, boolean.class);
    }

    public static Object createBaseComponent(String text, boolean keepNewLines) {
        String text0 = text != null ? text : "";

        try {
            return keepNewLines ? Array.get(CREATE_BASE_COMPONENT_CRAFTBUKKIT.invoke(null, text0, true), 0) : CREATE_BASE_COMPONENT_NMS.invoke(null, StringUtils.replace(BASE_COMPONENT_JSON_PATTERN, "{TEXT}", text0));
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            FunnyGuilds.getPluginLogger().error("Could not create base component", ex);
            return null;
        }
    }

    private NotificationUtil() {
    }
}
