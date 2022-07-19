package net.dzikoysk.funnyguilds.event.rank;

import java.util.Map;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Points change event called only in combat for tracking attacker, victim and assists points change at the same time.
 * Called after normal PointsChangeEvent
 */
public class CombatPointsChangeEvent extends AbstractRankEvent {

    private static final HandlerList handlers = new HandlerList();
    private int attackerPointsChange;
    private int victimPointsChange;
    private final Map<User, Integer> assistsPointsChange;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CombatPointsChangeEvent(EventCause eventCause, @Nullable User doer, User affected, int attackerPointsChange, int victimPointsChange, Map<User, Integer> assistsPointsChange) {
        super(eventCause, doer, affected);
        this.attackerPointsChange = attackerPointsChange;
        this.victimPointsChange = victimPointsChange;
        this.assistsPointsChange = assistsPointsChange;
    }

    public User getAttacker() {
        return getDoer().get();
    }

    public User getVictim() {
        return getAffected();
    }

    public int getAttackerPointsChange() {
        return this.attackerPointsChange;
    }

    public void setAttackerPointsChange(int attackerPointsChange) {
        this.attackerPointsChange = attackerPointsChange;
    }

    public int getVictimPointsChange() {
        return this.victimPointsChange;
    }

    public void setVictimPointsChange(int victimPointsChange) {
        this.victimPointsChange = victimPointsChange;
    }

    /**
     * @return mutable map of assists and their points change, by modifying this map, you will change the points change of the assists or remove player from final assists
     */
    public Map<User, Integer> getAssistsPointsChange() {
        return this.assistsPointsChange;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Kill points change has been cancelled by the server!";
    }

}
