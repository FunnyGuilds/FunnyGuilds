package net.dzikoysk.funnyguilds.util.elo;

public class EloConstant {

    private int minRank;
    private int maxRank;
    private int constant;

    public EloConstant(int minRank, int maxRank, int constant) {
        this.minRank = minRank;
        this.maxRank = maxRank;
        this.constant = constant;
    }

    public boolean isInRange(int rank) {
        return rank >= this.minRank && rank <= this.maxRank;
    }

    public int getConstant() {
        return this.constant;
    }
}