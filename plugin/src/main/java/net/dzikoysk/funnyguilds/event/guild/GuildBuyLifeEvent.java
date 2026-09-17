package net.dzikoysk.funnyguilds.event.guild;

import java.util.List;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GuildBuyLifeEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final double moneyCost;
    private final List<ItemStack> itemCost;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildBuyLifeEvent(EventCause eventCause, User doer, Guild guild, double moneyCost, List<ItemStack> itemCost) {
        super(eventCause, doer, guild);
        this.moneyCost = moneyCost;
        this.itemCost = itemCost;
    }

    public double getMoneyCost() {
        return this.moneyCost;
    }

    public List<ItemStack> getItemCost() {
        return this.itemCost;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild life purchase has been cancelled by the server!";
    }

}
