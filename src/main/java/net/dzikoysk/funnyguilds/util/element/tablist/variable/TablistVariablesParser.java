package net.dzikoysk.funnyguilds.util.element.tablist.variable;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.FunnyLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TablistVariablesParser {

    private Collection<TablistVariable> tablistVariables = new ArrayList<>();

    public void add(TablistVariable variable) {
        this.tablistVariables.add(variable);
    }

    public VariableParsingResult createResultFor(User user) {
        Map<String, String> values = new HashMap<>();

        for (TablistVariable tablistVariable : this.tablistVariables) {
            String value = tablistVariable.get(user);
            for (String name : tablistVariable.names()) {
                if (values.containsKey(name)) {
                    FunnyLogger.warning("Conflicting variable name: " + name);
                }

                values.put(name, value);
            }
        }

        return new VariableParsingResult(user, values);
    }
}
