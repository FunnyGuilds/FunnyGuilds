package net.dzikoysk.funnyguilds.util.reflect;

import net.dzikoysk.funnyguilds.util.reflect.Reflections.FieldAccessor;
import net.dzikoysk.funnyguilds.util.reflect.event.PacketReceiveEvent;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelPipeline;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
		Channel c = getChannel(p);
		ChannelHandler handler = new ChannelDuplexHandler() {
			@Override
			public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception{
				if(msg == null) return;
				super.write(ctx, msg, promise);
			}
			@Override
			public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
				if(msg == null) return;
				PacketReceiveEvent event = new PacketReceiveEvent(msg, p);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled() || event.getPacket() == null) return;
				try {
					super.channelRead(ctx, event.getPacket());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		ChannelPipeline cp = c.pipeline();
		cp.addBefore("packet_handler", "FunnyGuilds", handler);
	}

	public static void unregisterFunnyGuildsChannel() {
		for(Player player : Bukkit.getOnlinePlayers()){
			Channel c = getChannel(player);
			if(c == null || c.pipeline() == null) continue;
			ChannelPipeline cp = c.pipeline();
			if(cp.names().contains("FunnyGuilds")) cp.remove("FunnyGuilds");
		}
	}

}