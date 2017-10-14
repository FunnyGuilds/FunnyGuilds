package net.dzikoysk.funnyguilds.util.reflect.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPacketReceiveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private Object packet;

    public AsyncPacketReceiveEvent(Object packet, Player player) {
        super(true);
        this.packet = packet;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Object getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
    }

    public String getPacketName() {
        return this.packet.getClass().getSimpleName();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}