package net.dzikoysk.funnyguilds.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.dzikoysk.funnyguilds.util.reflect.Reflections.FieldAccessor;
import net.dzikoysk.funnyguilds.util.reflect.event.PacketReceiveEvent;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPipeline;
import net.minecraft.util.io.netty.channel.ChannelPromise;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketExtension {

	private static FieldAccessor<Channel> clientChannel;
	private static Field playerConnection;
	private static Field networkManager;
	private static Method handleMethod;
	static {
		try {
			clientChannel = Reflections.getField(Reflections.getCraftClass("NetworkManager"), Channel.class, 0);
			playerConnection = Reflections.getField(Reflections.getCraftClass("EntityPlayer"), "playerConnection");
			networkManager = Reflections.getField(Reflections.getCraftClass("PlayerConnection"), "networkManager");
			handleMethod = Reflections.getMethod(Reflections.getBukkitClass("entity.CraftEntity"), "getHandle");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Channel getChannel(Player p) {
		try {
			Object eP = handleMethod.invoke(p);
			return clientChannel.get(networkManager.get(playerConnection.get(eP)));
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static void registerPlayer(final Player p) {
		try {
			Channel c = getChannel(p);
			ChannelHandler handler = new ChannelDuplexHandler() {
				@Override
				public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception{
					if(msg == null) return;
					super.write(ctx, msg, promise);
				}
				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
					try {
						if(msg == null) return;
						PacketReceiveEvent event = new PacketReceiveEvent(msg, p);
						Bukkit.getPluginManager().callEvent(event);
						if (event.isCancelled() || event.getPacket() == null) return;
						super.channelRead(ctx, event.getPacket());
					} catch (Exception e) {
						super.channelRead(ctx, msg);
					}
				}
			};
			ChannelPipeline cp = c.pipeline();
			if(cp.names().contains("packet_handler")){
				if(cp.names().contains("FunnyGuilds")) cp.replace("FunnyGuilds", "FunnyGuilds", handler);
				else cp.addBefore("packet_handler", "FunnyGuilds", handler);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unregisterFunnyGuildsChannel() {
	}

}