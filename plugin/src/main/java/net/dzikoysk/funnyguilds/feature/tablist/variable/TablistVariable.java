package net.dzikoysk.funnyguilds.feature.tablist.variable;

import net.dzikoysk.funnyguilds.user.User;

public interface TablistVariable {

    String[] names();

    String get(User user);

}
