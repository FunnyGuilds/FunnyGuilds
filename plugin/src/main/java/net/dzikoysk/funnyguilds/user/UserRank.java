package net.dzikoysk.funnyguilds.user;

import java.util.function.IntFunction;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.rank.Rank;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
public class UserRank extends Rank<User> implements Comparable<UserRank> {

    UserRank(User user) {
        super(user);
    }

    public User getUser() {
        return this.entity;
    }

    @Override
    public int getPoints() {
        return FunnyGuilds.getInstance().getRankService().getUserStatValue(
                this.entity,
                UserStatKeys.POINTS
        ).orElseGet(0);
    }

    public void setPoints(int points) {
        FunnyGuilds.getInstance().getRankService().setUserStatValue(
                this.entity,
                UserStatKeys.POINTS,
                Math.max(
                        0,
                        points
                )
        );
    }

    public void updatePoints(IntFunction<Integer> update) {
        FunnyGuilds.getInstance().getRankService().changeUserStatValue(
                this.entity,
                UserStatKeys.POINTS,
                current -> Math.max(
                        0,
                        update.apply(current)
                )
        );
    }

    @Override
    public int getKills() {
        return FunnyGuilds.getInstance().getRankService().getUserStatValue(
                this.entity,
                UserStatKeys.KILLS
        ).orElseGet(0);
    }

    public void setKills(int kills) {
        FunnyGuilds.getInstance().getRankService().setUserStatValue(
                this.entity,
                UserStatKeys.KILLS,
                Math.max(
                        0,
                        kills
                )
        );
    }

    public void updateKills(IntFunction<Integer> update) {
        FunnyGuilds.getInstance().getRankService().changeUserStatValue(
                this.entity,
                UserStatKeys.KILLS,
                current -> Math.max(
                        0,
                        update.apply(current)
                )
        );
    }

    @Override
    public int getDeaths() {
        return FunnyGuilds.getInstance().getRankService().getUserStatValue(
                this.entity,
                UserStatKeys.DEATHS
        ).orElseGet(0);
    }

    public void setDeaths(int deaths) {
        FunnyGuilds.getInstance().getRankService().setUserStatValue(
                this.entity,
                UserStatKeys.DEATHS,
                Math.max(
                        0,
                        deaths
                )
        );
    }

    public void updateDeaths(IntFunction<Integer> update) {
        FunnyGuilds.getInstance().getRankService().changeUserStatValue(
                this.entity,
                UserStatKeys.DEATHS,
                current -> Math.max(
                        0,
                        update.apply(current)
                )
        );
    }

    @Override
    public int getAssists() {
        return FunnyGuilds.getInstance().getRankService().getUserStatValue(
                this.entity,
                UserStatKeys.ASSISTS
        ).orElseGet(0);
    }

    public void setAssists(int assists) {
        FunnyGuilds.getInstance().getRankService().setUserStatValue(
                this.entity,
                UserStatKeys.ASSISTS,
                Math.max(
                        0,
                        assists
                )
        );
    }

    public void updateAssists(IntFunction<Integer> update) {
        FunnyGuilds.getInstance().getRankService().changeUserStatValue(
                this.entity,
                UserStatKeys.ASSISTS,
                current -> Math.max(
                        0,
                        update.apply(current)
                )
        );
    }

    @Override
    public int getLogouts() {
        return FunnyGuilds.getInstance().getRankService().getUserStatValue(
                this.entity,
                UserStatKeys.LOGOUTS
        ).orElseGet(0);
    }

    public void setLogouts(int logouts) {
        FunnyGuilds.getInstance().getRankService().setUserStatValue(
                this.entity,
                UserStatKeys.LOGOUTS,
                Math.max(
                        0,
                        logouts
                )
        );
    }

    public void updateLogouts(IntFunction<Integer> update) {
        FunnyGuilds.getInstance().getRankService().changeUserStatValue(
                this.entity,
                UserStatKeys.LOGOUTS,
                current -> Math.max(
                        0,
                        update.apply(current)
                )
        );
    }

    @Override
    public float getKDR() {
           int kills = this.getKills();
        int deaths = this.getDeaths();
        return deaths == 0
                ? kills
                : 1.0F * kills / deaths;
    }

    @Override
    public float getKDA() {
        int kills = this.getKills();
        int deaths = this.getDeaths();
        int assists = this.getAssists();
        return deaths == 0
                ? (kills + assists)
                : 1.0F * (kills + assists) / deaths;
    }

    @Override
    public int compareTo(@NotNull UserRank rank) {
        return Integer.compare(rank.getPoints(), this.getPoints());
    }

}
