package net.dzikoysk.funnyguilds.listener;

import java.util.Map.Entry;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.system.war.WarSystem;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import net.dzikoysk.funnyguilds.util.reflect.event.PacketReceiveEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PacketReceive implements Listener {
	
	@EventHandler
	public void onReceive(PacketReceiveEvent event) {
		try {
			if(!event.getPacketName().equals("PacketPlayInUseEntity")) return;
			Object packet = event.getPacket();
			final Player player = event.getPlayer();
			int id = Reflections.getPrivateField(packet.getClass(), "a").getInt(packet);
			Object actionEnum = Reflections.getPrivateField(packet.getClass(), "action").get(packet);
			int action = Reflections.getPrivateField(actionEnum.getClass(), "d").getInt(actionEnum);
			for(final Entry<Guild, Integer> entry : EntityUtil.map.entrySet()){
				if(!entry.getValue().equals(id)) continue;
				if(action == 1) WarSystem.getInstance().attack(player, entry.getKey());
				else new ExcInfo().execute(player, new String[] { entry.getKey().getTag() });
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}