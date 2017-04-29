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

package co.marcin.novaguilds.impl.versionimpl.v1_9_R2;

import co.marcin.novaguilds.api.util.Packet;
import co.marcin.novaguilds.api.util.packet.PacketEvent;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.impl.util.AbstractPacketHandler;
import co.marcin.novaguilds.impl.util.signgui.AbstractSignGui;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R2.packet.PacketPlayInUpdateSign;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R2.packet.PacketPlayOutBlockChange;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R2.packet.PacketPlayOutOpenSignEditor;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R2.packet.PacketPlayOutTileEntityData;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SignGUIImpl extends AbstractSignGui {
	/**
	 * The constructor
	 */
	public SignGUIImpl() {
		registerUpdateHandling();
	}

	@Override
	public void open(Player player, String[] defaultText, SignGUIListener response) {
		try {
			final List<Packet> packets = new ArrayList<>();
			Location location = player.getLocation().clone();
			location.setY(0);

			if(defaultText != null) {
				packets.add(new PacketPlayOutBlockChange(location, Material.SIGN_POST, 0));
				packets.add(PacketPlayOutTileEntityData.getSignChange(location, defaultText));
			}

			packets.add(new PacketPlayOutOpenSignEditor(location));

			if(defaultText != null) {
				packets.add(new PacketPlayOutBlockChange(location, null, 0));
			}

			signLocations.put(player.getUniqueId(), location);
			listeners.put(player.getUniqueId(), response);
			PacketSender.sendPacket(player, packets.toArray(new Packet[packets.size()]));
		}
		catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Registers packet the handler
	 */
	protected void registerUpdateHandling() {
		new AbstractPacketHandler("PacketPlayInUpdateSign", PacketExtension.PacketHandler.Direction.IN) {
			@Override
			public void handle(PacketEvent event) {
				try {
					final PacketPlayInUpdateSign packetPlayInUpdateSign = new PacketPlayInUpdateSign(event.getPacket());
					final Player player = event.getPlayer();

					Location v = getSignLocations().remove(player.getUniqueId());

					if(v == null
							|| packetPlayInUpdateSign.getBlockPositionWrapper().getX() != v.getBlockX()
							|| packetPlayInUpdateSign.getBlockPositionWrapper().getY() != v.getBlockY()
							|| packetPlayInUpdateSign.getBlockPositionWrapper().getZ() != v.getBlockZ()) {
						return;
					}

					final SignGUIListener response = getListeners().remove(player.getUniqueId());

					if(response != null) {
						event.setCancelled(true);
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								response.onSignDone(player, packetPlayInUpdateSign.getLines());
							}
						});
					}
				}
				catch(IllegalAccessException e) {
					LoggerUtils.exception(e);
				}
			}
		};
	}
}
