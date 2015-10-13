package net.dzikoysk.funnyguilds.event.net;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketReceiveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private Object packet;
    private boolean cancelled;

    public PacketReceiveEvent(Object packet, Player player) {
        this.packet = packet;
        this.player = player;
        this.cancelled = false;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

}
