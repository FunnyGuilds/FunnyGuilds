package net.dzikoysk.funnyguilds.user;

import java.util.function.Function;

public final class UserRankTrackabilityChecker implements Function<User, Boolean> {
    
    private final boolean skipPrivilegedPlayersInRankPositions;

    public UserRankTrackabilityChecker(boolean skipPrivilegedPlayersInRankPositions) {
        this.skipPrivilegedPlayersInRankPositions = skipPrivilegedPlayersInRankPositions;
    }

    @Override
    public Boolean apply(User user) {
        return !this.skipPrivilegedPlayersInRankPositions || !user.hasPermission("funnyguilds.ranking.exempt");
    }
}
