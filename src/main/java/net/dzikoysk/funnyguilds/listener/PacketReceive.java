package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.system.war.WarSystem;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import net.dzikoysk.funnyguilds.util.reflect.event.AsyncPacketReceiveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.Map.Entry;

public class PacketReceive implements Listener {

    @EventHandler
    public void onReceive(AsyncPacketReceiveEvent event) {
        try {
            if (!event.getPacketName().equals("PacketPlayInUseEntity")) {
                return;
            }
            
            Object packet = event.getPacket();
            
            if (packet == null) {
                return;   
            }

            Field aField = Reflections.getPrivateField(packet.getClass(), "a");

            if (aField == null) {
                return;
            }

            int id = aField.getInt(packet);
            Field actionField = Reflections.getPrivateField(packet.getClass(), "action");

            if (actionField == null) {
                return;
            }

            Object actionEnum = actionField.get(packet);

            if (actionEnum == null) {
                return;
            }

            Player player = event.getPlayer();
            Field actionIdField = Reflections.getPrivateField(actionEnum.getClass(), "d");

            if (actionIdField == null) {
                return;
            }

            int action = actionIdField.getInt(actionEnum);
            
            for (final Entry<Guild, Integer> entry : EntityUtil.map.entrySet()) {
                if (!entry.getValue().equals(id)) {
                    continue;
                }

                Guild guild = entry.getKey();

                if (SecuritySystem.getSecurity().checkPlayer(player, guild)) {
                    return;
                }

                if (action == 1) {
                    WarSystem.getInstance().attack(player, entry.getKey());
                }
                else {
                    ExcInfo excInfo = new ExcInfo();
                    String[] parameters = new String[]{ entry.getKey().getTag() };

                    excInfo.execute(player, parameters);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
