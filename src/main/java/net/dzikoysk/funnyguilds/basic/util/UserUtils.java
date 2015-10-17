package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

import java.util.ArrayList;
import java.util.Collection;

public class UserUtils {

    private static final Collection<User> users = new ArrayList<User>();

    public static boolean playedBefore(String s) {
        for (User u : users)
            if (u.getName() != null && u.getName().equalsIgnoreCase(s))
                return true;
        return false;
    }

    public static void removeGuild(Collection<User> users) {
        for (User u : users)
            u.removeGuild();
    }

    public static void setGuild(Collection<User> users, Guild guild) {
        for (User u : users)
            u.setGuild(guild);
    }

    public static Collection<String> getOnlineNames(Collection<User> users) {
        Collection<String> list = new ArrayList<>();
        for (User u : users) {
            if (u.isOnline())
                list.add("<online>" + u.getName() + "</online>");
            else
                list.add(u.getName());
        }
        return list;
    }

    public static Collection<User> getUsers(Collection<String> names) {
        Collection<User> list = new ArrayList<>();
        for (String s : names)
            list.add(User.get(s));
        return list;
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static void removeUser(User user) {
        users.remove(user);
    }

    public static Collection<User> getUsers() {
        return new ArrayList<User>(users);
    }
}
