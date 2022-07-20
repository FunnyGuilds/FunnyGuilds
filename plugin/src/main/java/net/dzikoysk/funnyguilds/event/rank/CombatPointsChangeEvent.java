package net.dzikoysk.funnyguilds.event.rank;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

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

    public CombatPointsChangeEvent(EventCause eventCause, User attacker, User victim, int attackerPointsChange, int victimPointsChange, Map<User, Integer> assistsPointsChange) {
        super(eventCause, attacker, victim);
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

    public void setVictimPointsChange(int victimPointsChange) {
        this.victimPointsChange = victimPointsChange;
    }

    public CombatTable getAssistsPointsChange() {
        return this.assistsPointsChange;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Combat points change has been cancelled by the server!";
    }

    public static class CombatTable {

        private final Map<User, Integer> pointsChangeMap;

        private CombatTable(Map<User, Integer> pointsChangeMap) {
            this.pointsChangeMap = pointsChangeMap;
        }

        public Map<User, Integer> getPointsChanges() {
            return new HashMap<>(this.pointsChangeMap);
        }

        /**
         * @return the points change of the user, if user wasn't assisting the return value will be 0
         */
        public Option<Integer> getUserPointsChange(User user) {
            return Option.of(this.pointsChangeMap.get(user));
        }

        public boolean wasAssisting(User user) {
            return this.pointsChangeMap.containsKey(user);
        }

        /**
         * @return if points change was modified for user, false if that user wasn't assisting
         */
        public boolean modifyPointsChange(User user, IntFunction<Integer> update) {
            return this.pointsChangeMap.compute(user, (key, value) -> {
                if (value == null) {
                    return null;
                }

                return update.apply(value);
            }) != null;
        }

    }

}
