package net.dzikoysk.funnyguilds.util.pointsformat;

import net.dzikoysk.funnyguilds.util.FunnyLogger;
import net.dzikoysk.funnyguilds.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PointsFormatUtils {

    private static List<PointsFormat> formats = new ArrayList<>();
    
    public static void parseData(List<String> data) {
        formats.clear();
        
        for (String s : data) {
            String[] split = s.split(" ");
            if (split.length < 2) {
                FunnyLogger.exception(new IllegalArgumentException("\"" + s + "\" is not a valid format String!"));
                continue;
            }

            String[] range = split[0].split("-");
            if (range.length < 2) {
                FunnyLogger.exception(new IllegalArgumentException("\"" + s + "\" is not a valid format range String!"));
                continue;
            }

            int rankMin = 0;
            int rankMax = 0;
            
            try {
                rankMin = Integer.parseInt(range[0]);
                if (rankMin < 0) {
                    FunnyLogger.exception(new IllegalArgumentException("\"" + rankMin + "\" of format String \"" + s + "\" is an invalid, negative integer!"));
                    continue;
                }
            } catch (NumberFormatException e) {
                FunnyLogger.exception(new NumberFormatException("\"" + range[0] + "\" of format String \"" + s + "\" is not a valid integer!"));
                continue;
            }

            try {
                rankMax = range[1].equals("*") ? Integer.MAX_VALUE : Integer.parseInt(range[1]);
                if (rankMax < 0) {
                    FunnyLogger.exception(new IllegalArgumentException("\"" + rankMax + "\" of format String \"" + s + "\" is an invalid, negative integer!"));
                    continue;
                }
            } catch (NumberFormatException e) {
                FunnyLogger.exception(new NumberFormatException("\"" + range[1] + "\" of format String \"" + s + "\" is not a valid integer!"));
                continue;
            }

            formats.add(new PointsFormat(rankMin, rankMax, StringUtils.colored(split[1])));
        }
    }
    
    public static String getFormatForRank(int rank) {
        for (PointsFormat f : formats) {
            if (f.isInRange(rank)) {
                return f.getFormat();
            }
        }

        return "";
    }
    
    private PointsFormatUtils() {}    
    
}
