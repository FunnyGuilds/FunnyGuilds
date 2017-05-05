package net.dzikoysk.funnyguilds.util;

import java.util.Random;

public class RandomizationUtils {

    private final static Random random = new Random();

    public static boolean chance(double chance) {
        return chance >= 100 || chance >= random.nextDouble() * 100;
    }

}
