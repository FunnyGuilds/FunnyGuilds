package net.dzikoysk.funnyguilds.element.tablist.variable;

import net.dzikoysk.funnyguilds.basic.user.User;

public interface TablistVariable {

    String[] names();

    String get(User user);
    
}
