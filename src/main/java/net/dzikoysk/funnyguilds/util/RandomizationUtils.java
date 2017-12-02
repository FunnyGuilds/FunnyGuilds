package net.dzikoysk.funnyguilds.util;

import java.util.Random;

public final class RandomizationUtils {

    private final static Random RANDOM_INSTANCE = new Random();

    private RandomizationUtils() {
    }

    public static boolean chance(double chance) {
        return (chance >= 100) || (chance >= (RANDOM_INSTANCE.nextDouble() * 100));
    }
}
