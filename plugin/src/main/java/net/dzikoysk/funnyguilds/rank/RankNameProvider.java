package net.dzikoysk.funnyguilds.rank;

import java.util.UUID;
import java.util.function.Function;

@FunctionalInterface
public interface RankNameProvider extends Function<UUID, String> {
}
