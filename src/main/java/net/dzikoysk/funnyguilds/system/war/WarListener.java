package net.dzikoysk.funnyguilds.system.war;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;

// Temp class
public class WarListener {

    private static Class clazz;
    private static Field idField;
    private static Field actionField;
    private static Field actionIdField;

    public static void use(Player player, Object packet) {
        try {
            if (packet == null) {
                return;
            }

            if (clazz == null) {
                if (!packet.getClass().getSimpleName().equals("PacketPlayInUseEntity")) {
                    return;
                }

                clazz = packet.getClass();
                idField = Reflections.getPrivateField(packet.getClass(), "a");
                actionField = Reflections.getPrivateField(packet.getClass(), "action");

                Object actionEnum = actionField.get(packet);
                actionIdField = Reflections.getPrivateField(actionEnum.getClass(), "d");
            }

            if (!packet.getClass().equals(clazz)) {
                return;
            }

            if (actionField == null) {
                return;
            }

            int id = idField.getInt(packet);
            Object actionEnum = actionField.get(packet);

            if (actionEnum == null || actionIdField == null) {
                return;
            }

            int action = actionIdField.getInt(actionEnum);

            call(player, id, action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void call(Player player, int id, int action) {
        for (final Map.Entry<Guild, Integer> entry : EntityUtil.getEntitesMap().entrySet()) {
            if (!entry.getValue().equals(id)) {
                continue;
            }

            Guild guild = entry.getKey();

            if (SecuritySystem.getSecurity().checkPlayer(player, guild)) {
                return;
            }

            if (action == 1) {
                WarSystem.getInstance().attack(player, entry.getKey());
            } else {
                ExcInfo excInfo = new ExcInfo();
                String[] parameters = new String[]{ entry.getKey().getTag() };

                excInfo.execute(player, parameters);
            }
        }
    }

}
