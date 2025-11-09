package net.dzikoysk.funnyguilds.rank;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public interface RankService {

    <T extends Number> Option<T> getUserStatValue(
            User user,
            StatKey<T> key
    );

    <T extends Number> Option<Integer> getUserPosition(
            User user,
            StatKey<T> key
    );

    <T extends Number> Option<User> getUserAtPosition(
            int position,
            StatKey<T> key
    );
    
    <T extends Number> T setUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    );
    
    <T extends Number> T changeUserStatValue(
            User user,
            StatKey<T> key,
            UnaryOperator<T> updateFunction
    );
    
    <T extends Number> CompletableFuture<T> queueSetUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    );
    
    <T extends Number> CompletableFuture<T> queueChangeUserStatValue(
            User user,
            StatKey<T> key,
            UnaryOperator<T> updateFunction
    );

    <T extends Number, R extends Number> Option<R> getGuildStatValue(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    );

    <T extends Number> Option<Integer> getGuildPosition(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<?> aggregationType
    );

    <T extends Number> Option<Guild> getGuildAtPosition(
            int position,
            StatKey<T> key,
            GuildStatAggregationType<?> aggregationType
    );

}
