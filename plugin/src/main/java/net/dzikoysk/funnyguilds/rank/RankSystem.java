package net.dzikoysk.funnyguilds.rank;

import com.google.common.collect.ImmutableMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;

import java.util.function.BiFunction;

public class RankSystem {

    private ImmutableMap<RankType, BiFunction<Integer, Integer, RankResult>> map;

    private RankSystem() {
    }

    public RankResult calculate(RankType type, Integer attackerPoints, Integer victimPoints) {
        return map.get(type).apply(attackerPoints, victimPoints);
    }

    public enum RankType {
        ELO,
        PERCENT,
        STATIC,
    }

    public static class RankResult {

        private final Integer attackerPoints;
        private final Integer victimPoints;

        public RankResult(Integer attackerPoints, Integer victimPoints) {
            this.attackerPoints = attackerPoints;
            this.victimPoints = victimPoints;
        }

        public RankResult(Integer samePoints) {
            this.attackerPoints = samePoints;
            this.victimPoints = samePoints;
        }

        public Integer getAttackerPoints() {
            return attackerPoints;
        }

        public Integer getVictimPoints() {
            return victimPoints;
        }
    }

    public static RankSystem create() {
        RankSystem rankSystem = new RankSystem();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        rankSystem.map = new ImmutableMap.Builder<RankType, BiFunction<Integer, Integer, RankResult>>()
                .put(RankType.ELO, (attackerPoints, victimPoints) -> {
                    int attackerElo = IntegerRange.inRange(attackerPoints, config.eloConstants).orElseGet(0);
                    int victimElo = IntegerRange.inRange(victimPoints, config.eloConstants).orElseGet(0);

                    double attackerE = 1.0D / (1.0D + Math.pow(config.eloExponent, (victimPoints - attackerPoints) / config.eloDivider));
                    double victimE = 1.0D / (1.0D + Math.pow(config.eloExponent, (attackerPoints - victimPoints) / config.eloDivider));

                    attackerElo = (int) Math.round(attackerElo * (1 - attackerE));
                    victimElo = (int) Math.round(victimElo * (0 - victimE) * - 1);

                    return new RankResult(attackerElo, victimElo);
                })
                .put(RankType.PERCENT, (attackerPoints, victimPoints) -> new RankResult((int) (victimPoints * (config.percentRankChange / 100.0))))
                .put(RankType.STATIC, (attackerPoints, victimPoints) -> new RankResult(config.staticAttackerChange, config.staticVictimChange))
                .build();

        return rankSystem;
    }

}
