package net.dzikoysk.funnyguilds.event.rank;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
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
    private final CombatTable assistsPointsChange;

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
        this.assistsPointsChange = new CombatTable(assistsPointsChange);
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

    public CombatTable getAssistsPointsChange() {
        return this.assistsPointsChange;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Kill points change has been cancelled by the server!";
    }

    public static class CombatTable {

        private final Map<User, Integer> pointsChangeMap;

        private CombatTable(Map<User, Integer> pointsChangeMap) {
            this.pointsChangeMap = pointsChangeMap;
        }

        public Map<User, Integer> getPointsChange() {
            return new HashMap<>(this.pointsChangeMap);
        }

        /**
         * @return if points change was modified for user, false if that user wasn't assisting
         */
        public boolean modify(User user, IntFunction<Integer> update) {
            if (!this.pointsChangeMap.containsKey(user)) {
                return false;
            }

            this.pointsChangeMap.computeIfPresent(user, (key, value) -> update.apply(value));
            return true;
        }

    }

}
