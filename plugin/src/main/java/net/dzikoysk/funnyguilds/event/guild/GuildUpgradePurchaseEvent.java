package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildUpgradePurchaseEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();

    private final String upgradeKey;
    private final int newLevel;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildUpgradePurchaseEvent(EventCause eventCause, User doer, Guild guild, String upgradeKey, int newLevel) {
        super(eventCause, doer, guild);
        this.upgradeKey = upgradeKey;
        this.newLevel = newLevel;
    }

    public String getUpgradeKey() {
        return this.upgradeKey;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild upgrade purchase has been cancelled by the server!";
    }

}
