package net.dzikoysk.funnyguilds.system.war;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.util.Cooldown;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// Temp class
public final class WarListener {

    private static final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    private static final Class<?> USE_ENTITY_CLASS;
    private static final Field PACKET_ID_FIELD;
    private static final Field PACKET_ACTION_FIELD;
    private static final Field ENUM_HAND_FIELD;

    static {
        USE_ENTITY_CLASS = Reflections.getCraftClass("PacketPlayInUseEntity");
        PACKET_ID_FIELD = Reflections.getPrivateField(USE_ENTITY_CLASS, "a");
        PACKET_ACTION_FIELD = Reflections.getPrivateField(USE_ENTITY_CLASS, "action");
        ENUM_HAND_FIELD = Reflections.getFixedVersion().startsWith("v1_8") ? null : Reflections.getPrivateField(USE_ENTITY_CLASS, "d");
    }

    public static void use(Player player, Object packet) {
        try {
            if (packet == null) {
                return;
            }

            if (!packet.getClass().equals(USE_ENTITY_CLASS)) {
                return;
            }

            if (PACKET_ACTION_FIELD == null) {
                return;
            }

            int id = PACKET_ID_FIELD.getInt(packet);
            Object actionEnum = PACKET_ACTION_FIELD.get(packet);
            Object enumHand = "";

            if (ENUM_HAND_FIELD != null) {
                enumHand = ENUM_HAND_FIELD.get(packet);
            }

            if (actionEnum == null) {
                return;
            }

            call(player, id, actionEnum.toString(), enumHand == null ? "" : enumHand.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void call(Player player, int id, String action, String hand) {
        for (final Map.Entry<Guild, Integer> entry : EntityUtil.getEntitesMap().entrySet()) {
            if (!entry.getValue().equals(id)) {
                continue;
            }

            Guild guild = entry.getKey();

            if (SecuritySystem.getSecurity().checkPlayer(player, guild)) {
                return;
            }

            if ("ATTACK".equalsIgnoreCase(action)) {
                WarSystem.getInstance().attack(player, entry.getKey());
            }
            
            else if ("INTERACT_AT".equalsIgnoreCase(action)) {
                if(informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, Settings.getConfig().infoPlayerCooldown)) {
                    return;
                }

                if (!hand.isEmpty() && !"MAIN_HAND".equalsIgnoreCase(hand)) {
                    return;
                }

                ExcInfo excInfo = new ExcInfo();
                String[] parameters = new String[]{entry.getKey().getTag()};

                excInfo.execute(player, parameters);
            }
        }
    }

}
