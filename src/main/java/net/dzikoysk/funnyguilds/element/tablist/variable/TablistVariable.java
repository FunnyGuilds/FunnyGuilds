package net.dzikoysk.funnyguilds.element.tablist.variable;

import net.dzikoysk.funnyguilds.basic.User;

public interface TablistVariable {

    String[] names();

    String get(User user);
}
