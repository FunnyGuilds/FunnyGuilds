package net.dzikoysk.funnyguilds.util.reflect;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.dzikoysk.funnyguilds.util.reflect.Reflections.FieldAccessor;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
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
            clientChannel = Reflections.getField(Reflections.getNMSClass("NetworkManager"), Channel.class, 0);
            playerConnection = Reflections.getField(Reflections.getNMSClass("EntityPlayer"), "playerConnection");
            networkManager = Reflections.getField(Reflections.getNMSClass("PlayerConnection"), "networkManager");
            handleMethod = Reflections.getMethod(Reflections.getCraftBukkitClass("entity.CraftEntity"), "getHandle");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Channel getChannel(Player p) {
        try {
            Object eP = handleMethod.invoke(p);
            return clientChannel.get(networkManager.get(playerConnection.get(eP)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void registerPlayer(final Player p) {
        try {
            Channel c = getChannel(p);
            ChannelHandler handler = new ChannelDuplexHandler() {
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    if (msg == null) {
                        return;
                    }
                    
                    super.write(ctx, msg, promise);
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    try {
                        if (msg == null) {
                            return;
                        }

                        IndependentThread.action(ActionType.PACKET_PLAY_IN_USE_ENTITY, p, msg);
                        super.channelRead(ctx, msg);
                    } catch (Exception e) {
                        super.channelRead(ctx, msg);
                    }
                }
            };

            ChannelPipeline cp = c.pipeline();
            if (cp.names().contains("packet_handler")) {
                if (cp.names().contains("FunnyGuilds")) {
                    cp.replace("FunnyGuilds", "FunnyGuilds", handler);
                } else {
                    cp.addBefore("packet_handler", "FunnyGuilds", handler);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}