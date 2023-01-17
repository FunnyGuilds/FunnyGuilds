package net.dzikoysk.funnyguilds.event.rank;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import net.dzikoysk.funnyguilds.event.rank.CombatPointsChangeEvent.CombatTable.Assist;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;
import panda.std.stream.PandaStream;

/**
 * Points change event called only in combat for tracking attacker, victim and assists points change at the same time.
 * Called after normal PointsChangeEvent
 */
public class CombatPointsChangeEvent extends AbstractRankEvent {

    private static final HandlerList handlers = new HandlerList();
    private int attackerPointsChange;
    private int victimPointsChange;
    private final CombatTable assistsMap;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CombatPointsChangeEvent(EventCause eventCause, User attacker, User victim, int attackerPointsChange, int victimPointsChange, Map<User, Assist> assistsMap) {
        super(eventCause, attacker, victim);
        this.attackerPointsChange = attackerPointsChange;
        this.victimPointsChange = victimPointsChange;
        this.assistsMap = new CombatTable(assistsMap);
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

    public CombatTable getAssistsMap() {
        return this.assistsMap;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Combat points change has been cancelled by the server!";
    }

    public static class CombatTable {

        private final Map<User, Assist> assistsMap;

        private CombatTable(Map<User, Assist> assistsMap) {
            this.assistsMap = assistsMap;
        }

        public Map<User, Assist> getAssistsMap() {
            return new HashMap<>(this.assistsMap);
        }

        public Map<User, Integer> getPointChanges() {
            return PandaStream.of(this.assistsMap.entrySet())
                    .toMap(Map.Entry::getKey, entry -> entry.getValue().getPointsChange());
        }

        public Map<User, Double> getDamageShares() {
            return PandaStream.of(this.assistsMap.entrySet())
                    .toMap(Map.Entry::getKey, entry -> entry.getValue().getDamageShare());
        }

        public Option<Assist> getUserAssist(User user) {
            return Option.of(this.assistsMap.get(user));
        }

        public Option<Integer> getUserPointsChange(User user) {
            return this.getUserAssist(user).map(Assist::getPointsChange);
        }

        public Option<Double> getUserDamageShare(User user) {
            return this.getUserAssist(user).map(Assist::getDamageShare);
        }

        public boolean wasAssisting(User user) {
            return this.assistsMap.containsKey(user);
        }

        /**
         * @return if points change was modified for user, false if that user wasn't assisting
         */
        public boolean modifyPointsChange(User user, IntFunction<Integer> update) {
            return this.assistsMap.computeIfPresent(
                    user,
                    (key, value) -> value.setPointsChange(update.apply(value.getPointsChange()))
            ) != null;
        }

        /**
         * Modify damage share for user - it's only visual and don't affect points change
         *
         * @return if damage share was modified for user, false if that user wasn't assisting
         */
        public boolean modifyDamageShare(User user, DoubleFunction<Double> update) {
            return this.assistsMap.computeIfPresent(
                    user,
                    (key, value) -> value.setDamageShare(update.apply(value.getDamageShare()))
            ) != null;
        }

        public boolean isEmpty() {
            return this.assistsMap.isEmpty();
        }

        public static class Assist {

            private int pointsChange;
            private double damageShare;

            public Assist(int pointsChange, double damageShare) {
                this.pointsChange = pointsChange;
                this.damageShare = damageShare;
            }

            public int getPointsChange() {
                return this.pointsChange;
            }

            private Assist setPointsChange(int pointsChange) {
                this.pointsChange = pointsChange;
                return this;
            }

            public double getDamageShare() {
                return this.damageShare;
            }

            private Assist setDamageShare(double damageShare) {
                this.damageShare = damageShare;
                return this;
            }

        }

    }

}
