package net.dzikoysk.funnyguilds.util.elo;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.data.Settings;

import java.util.ArrayList;
import java.util.List;

public class EloUtils {

    private static List<EloConstant> constants = new ArrayList<EloConstant>();

    public static void parseData(List<String> eloConstants) {
        constants.clear();
        for (String s : eloConstants) {
            String[] split = s.split(" ");
            if (split.length < 2) {
                FunnyGuilds.exception(new IllegalArgumentException("\"" + s + "\" is not a valid constant String!"));
                continue;
            }

            String[] range = split[0].split("-");
            if (range.length < 2) {
                FunnyGuilds.exception(new IllegalArgumentException("\"" + s + "\" is not a valid constant range String!"));
                continue;
            }

            int rankMin = 0, rankMax = 0, constant = 0;
            try {
                rankMin = Integer.parseInt(range[0]);
                if (rankMin < 0) {
                    FunnyGuilds.exception(new IllegalArgumentException("\"" + rankMin + "\" of constant String \"" + s + "\" is an invalid, negative integer!"));
                    continue;
                }
            } catch (NumberFormatException e) {
                FunnyGuilds.exception(new NumberFormatException("\"" + range[0] + "\" of constant String \"" + s + "\" is not a valid integer!"));
                continue;
            }

            try {
                rankMax = range[1].equals("*") ? Integer.MAX_VALUE : Integer.parseInt(range[1]);
                if (rankMax < 0) {
                    FunnyGuilds.exception(new IllegalArgumentException("\"" + rankMax + "\" of constant String \"" + s + "\" is an invalid, negative integer!"));
                    continue;
                }
            } catch (NumberFormatException e) {
                FunnyGuilds.exception(new NumberFormatException("\"" + range[1] + "\" of constant String \"" + s + "\" is not a valid integer!"));
                continue;
            }

            try {
                constant = Integer.parseInt(split[1]);
                if (constant < 0) {
                    FunnyGuilds.exception(new IllegalArgumentException("\"" + constant + "\" of constant String \"" + s + "\" is an invalid, negative integer!"));
                    continue;
                }
            } catch (NumberFormatException e) {
                FunnyGuilds.exception(new NumberFormatException("\"" + split[1] + "\" of constant String \"" + s + "\" is not a valid integer!"));
                continue;
            }

            constants.add(new EloConstant(rankMin, rankMax, constant));
        }
    }

    private static int getConstantForRank(int rank) {
        for (EloConstant c : constants) {
            if (c.isInRange(rank)) {
                return c.getConstant();
            }
        }

        return 0;
    }

    public static int[] getRankChanges(Rank attacker, Rank victim) {
        int aP = attacker.getPoints();
        int vP = victim.getPoints();
        
        return new int[]{
                (int) Math.round(EloUtils.getConstantForRank(aP) * (1 - (1.0D / (1.0D + Math.pow(Settings.getConfig().eloExponent, (vP - aP) / Settings.getConfig().eloDivider))))),
                (int) Math.round(EloUtils.getConstantForRank(vP) * (0 - (1.0D / (1.0D + Math.pow(Settings.getConfig().eloExponent, (aP - vP) / Settings.getConfig().eloDivider)))) * -1)
        };
    }

}
