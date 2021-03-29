package net.dzikoysk.funnyguilds.element.tablist.variable;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.TimeFormattedVariable;

import java.time.LocalDateTime;
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
        LocalDateTime currentTime = LocalDateTime.now();

        for (TablistVariable tablistVariable : this.tablistVariables) {
            if (tablistVariable instanceof TimeFormattedVariable) {
                ((TimeFormattedVariable) tablistVariable).provideCurrentTime(currentTime);
            }
            String value = tablistVariable.get(user);
            for (String name : tablistVariable.names()) {
                if (values.containsKey(name)) {
                    FunnyGuilds.getPluginLogger().warning("Conflicting variable name: " + name);
                }

                values.put(name, value);
            }
        }

        return new VariableParsingResult(user, values);
    }
}
