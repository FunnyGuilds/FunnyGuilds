package net.dzikoysk.funnyguilds.feature.tablist.variable;

import java.util.Map;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;

public class VariableParsingResult {
    private final User user;
    private final Map<String, String> values;

    public VariableParsingResult(User user, Map<String, String> values) {
        this.user = user;
        this.values = values;
    }

    public User getUser() {
        return user;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getValue(String name) {
        return this.values.get(name);
    }

    public String replaceInString(String string) {
        for (Map.Entry<String, String> entry : this.values.entrySet()) {
            string = StringUtils.replace(string, "{" + entry.getKey() + "}", entry.getValue());
        }

        return string;
    }
}
