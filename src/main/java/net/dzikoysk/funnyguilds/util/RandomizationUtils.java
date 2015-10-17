package net.dzikoysk.funnyguilds.util;

import java.util.Random;

public class RandomizationUtils {

    public static boolean chance(double chance) {
        return chance >= 100 || chance >= new Random().nextDouble() * 100;
    }

}
