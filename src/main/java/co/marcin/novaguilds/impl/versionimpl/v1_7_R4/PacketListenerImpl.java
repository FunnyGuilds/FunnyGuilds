/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.impl.versionimpl.v1_7_R4;

import co.marcin.novaguilds.api.event.PlayerInteractEntityEvent;
import co.marcin.novaguilds.api.util.packet.PacketEvent;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.enums.EntityUseAction;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.impl.util.AbstractPacketHandler;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class PacketListenerImpl extends AbstractListener {
	/**
	 * Registers packet listener
	 * for advanced interact entity event
	 */
	public PacketListenerImpl() {
		new AbstractPacketHandler("PacketPlayInUseEntity", PacketExtension.PacketHandler.Direction.IN) {
			@Override
			public void handle(PacketEvent event) {
				try {
					Object packet = event.getPacket();
					Method cMethod = Reflections.getMethod(packet.getClass(), "c");
					Object enumAction = cMethod.invoke(packet);
					Method nameMethod = Reflections.getMethod(enumAction.getClass(), "name");
					EntityUseAction action = EntityUseAction.valueOf((String) nameMethod.invoke(enumAction));
					Class<?> useEntityClass = Reflections.getCraftClass("PacketPlayInUseEntity");
					FieldAccessor<Integer> useEntityA = Reflections.getField(useEntityClass, int.class, 0);
					int id = useEntityA.get(packet);

					Entity entity = null;
					for(Entity e : event.getPlayer().getNearbyEntities(5, 5, 5)) {
						if(e.getEntityId() == id) {
							entity = e;
						}
					}

					if(entity == null) {
						return;
					}

					PlayerInteractEntityEvent clickEvent = new PlayerInteractEntityEvent(event.getPlayer(), entity, action);
					ListenerManager.getLoggedPluginManager().callEvent(clickEvent);
				}
				catch(IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
					LoggerUtils.exception(e);
				}
			}
		};
	}
}
