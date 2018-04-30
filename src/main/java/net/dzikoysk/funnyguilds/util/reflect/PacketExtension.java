package net.dzikoysk.funnyguilds.util.reflect;

import io.netty.channel.*;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.WarUseRequest;
import net.dzikoysk.funnyguilds.util.reflect.Reflections.FieldAccessor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class PacketExtension {

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

    private static Channel getChannel(Player player) {
        try {
            Object eP = handleMethod.invoke(player);
            return clientChannel.get(networkManager.get(playerConnection.get(eP)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void registerPlayer(Player player) {
        try {
            ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
            Channel channel = getChannel(player);

            ChannelHandler handler = new ChannelDuplexHandler() {
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    if (msg == null) {
                        return;
                    }
                    
                    super.write(ctx, msg, promise);
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                    try {
                        if (packet == null) {
                            return;
                        }

                        //IndependentThread.action(ActionType.PACKET_PLAY_IN_USE_ENTITY, p, msg);
                        concurrencyManager.postRequests(new WarUseRequest(player, packet));

                        super.channelRead(ctx, packet);
                    } catch (Exception e) {
                        super.channelRead(ctx, packet);
                    }
                }
            };

            if (channel == null) {
                return;
            }

            ChannelPipeline pipeline = channel.pipeline();

            if (pipeline.names().contains("packet_handler")) {
                if (pipeline.names().contains("FunnyGuilds")) {
                    pipeline.replace("FunnyGuilds", "FunnyGuilds", handler);
                } else {
                    pipeline.addBefore("packet_handler", "FunnyGuilds", handler);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PacketExtension() {}

}