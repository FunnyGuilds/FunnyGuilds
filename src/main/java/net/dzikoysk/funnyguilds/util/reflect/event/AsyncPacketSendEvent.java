package net.dzikoysk.funnyguilds.util.reflect.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPacketSendEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private Object packet;

    public AsyncPacketSendEvent(Object packet, Player player) {
        super(true);
        this.packet = packet;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Object getPacket() {
        return packet;
    }

    public String getPacketName() {
        return this.packet.getClass().getSimpleName();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
