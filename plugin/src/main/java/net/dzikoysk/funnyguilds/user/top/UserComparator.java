package net.dzikoysk.funnyguilds.user.top;

import java.util.Comparator;
import net.dzikoysk.funnyguilds.user.UserRank;

public final class UserComparator {

    public static final Comparator<UserRank> POINTS_COMPARATOR = new Points();

    private static class Points implements Comparator<UserRank> {

        @Override
        public int compare(UserRank o1, UserRank o2) {
            int result = Integer.compare(o1.getPoints(), o2.getPoints());
            if (result == 0) {
                result = compareName(o1, o2);
            }
            return result;
        }

    }

    private static int compareName(UserRank o1, UserRank o2) {
        if (o1.getIdentityName() == null) {
            return -1;
        }

        if (o2.getIdentityName() == null) {
            return 1;
        }

        return o1.getIdentityName().compareTo(o2.getIdentityName());
    }

    private UserComparator() {}

}
