package net.dzikoysk.funnyguilds.rank;

import java.util.concurrent.CompletableFuture;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public interface RankController {
    
    <T extends Number> Option<T> getUserStatValue(User user, StatKey<T> key);
    
    <T extends Number> CompletableFuture<Void> updateUserStatValue(User user, StatKey<T> key, T valueChange);
    
    <R extends Number> Option<R> getGuildStatValue(Guild guild, StatKey<?> key, GuildStatType<R> statType);
    
    @FunctionalInterface
    interface GuildStatType<R extends Number> {
        
        GuildStatType<Integer> TOTAL_INT = (controller, guild, key) -> {
                    int total = 0;
                    for (User member : guild.getMembers()) {
                        total += controller.getUserStatValue(member, key)
                                .map(Number::intValue)
                                .orElseGet(0);
                    }
                    return total;
                };
        GuildStatType<Double> TOTAL_DOUBLE = (controller, guild, key) -> {
                    double total = 0.0;
                    for (User member : guild.getMembers()) {
                        total += controller.getUserStatValue(member, key)
                                .map(Number::doubleValue)
                                .orElseGet(0.0);
                    }
                    return total;
                };
        GuildStatType<Double> AVERAGE = (controller, guild, key) -> {
                    int memberCount = guild.getMembers().size();
                    if (memberCount == 0) {
                        return 0.0;
                    }
                    
                    double total = 0.0;
                    for (User member : guild.getMembers()) {
                        total += controller.getUserStatValue(member, key)
                                .map(Number::doubleValue)
                                .orElseGet(0.0);
                    }
                    return total / memberCount;
                };
        
        R computeValue(RankController controller, Guild guild, StatKey<?> key);
        
    }
}
