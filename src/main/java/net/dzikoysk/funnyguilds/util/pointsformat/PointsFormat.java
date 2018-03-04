package net.dzikoysk.funnyguilds.util.pointsformat;

public class PointsFormat {

    private final int minRank;
    private final int maxRank;
    private final String format;

    public PointsFormat(int minRank, int maxRank, String format) {
        this.minRank = minRank;
        this.maxRank = maxRank;
        this.format = format;
    }

    public boolean isInRange(int rank) {
        return rank >= this.minRank && rank <= this.maxRank;
    }

    public String getFormat() {
        return this.format;
    }

}
