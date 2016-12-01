package net.dzikoysk.funnyguilds.util.reflect.transition;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;

public class PacketPlayOutPlayerInfo {

    private static final Class<?> packetClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
    private static final Class<?>[] typesClass = new Class<?>[]{ String.class, boolean.class, int.class };
    private static int type = 0;

    static {
        try {
            if (packetClass.getConstructor(typesClass) == null) {
                type = 1;
            }
        } catch (Exception e) {
            type = 1;
        }
    }

    public static Object getPacket(String s, boolean b, int i) {
        try {
            if (type == 0) {
                return packetClass.getConstructor(typesClass).newInstance(s, b, i);
            }
            else if (type == 1) {
                Class<?> clazz = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
                Object packet = packetClass.getConstructor().newInstance();
                Reflections.getPrivateField(clazz, "username").set(packet, s);
                Reflections.getPrivateField(clazz, "gamemode").set(packet, 1);
                Reflections.getPrivateField(clazz, "ping").set(packet, i);
                Reflections.getPrivateField(clazz, "player").set(packet, new OfflineUser(s).getProfile());
                if (!b) {
                    Reflections.getPrivateField(clazz, "action").set(packet, 4);
                }
                return packet;
            }
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
