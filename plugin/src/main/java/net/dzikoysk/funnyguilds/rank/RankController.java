package net.dzikoysk.funnyguilds.rank;

import java.util.concurrent.CompletableFuture;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public interface RankController {

    <T extends Number> Option<T> getUserStatValue(
            User user,
            StatKey<T> key
    );

    <T extends Number> CompletableFuture<T> updateUserStatValue(
            User user,
            StatKey<T> key,
            T valueChange
    );

    <T extends Number, R extends Number> Option<R> getGuildStatValue(
            Guild guild,
            StatKey<T> key,
            GuildStatAggregationType<R> aggregationType
    );
}
