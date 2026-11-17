package net.dzikoysk.funnyguilds.data.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.rank.StatKey;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserStatKeys;
import net.kyori.adventure.key.Key;

/** Shared, typed storage for additional statistics. Legacy columns remain authoritative for built-in counters. */
public final class UserStatsSerializer {

    private UserStatsSerializer() {
    }

    public static String serialize(User user) {
        JsonObject result = new JsonObject();
        FunnyGuilds.getInstance().getRankService().getUserStats(user).forEach((key, value) -> {
            if (UserStatKeys.VALUES.contains(key)) {
                return;
            }
            JsonObject stat = new JsonObject();
            stat.addProperty("type", key.getValueType() == Integer.class ? "integer" : "double");
            stat.addProperty("value", value);
            result.add(key.key().asString(), stat);
        });
        return result.toString();
    }

    public static void deserialize(User user, String serialized) {
        if (serialized == null || serialized.trim().isEmpty()) {
            return;
        }
        JsonObject values;
        try {
            values = new JsonParser().parse(serialized).getAsJsonObject();
        }
        catch (RuntimeException failure) {
            FunnyGuilds.getPluginLogger().warning("Invalid custom statistics for user " + user.getUUID() + ": " + failure.getMessage());
            return;
        }
        RankService service = FunnyGuilds.getInstance().getRankService();
        // Keys unknown to this session (custom statistics or hand-edited entries) are intentionally
        // registered: dropping them would silently lose stored data of custom statistics. Unknown
        // entries produce a warning below and, if kept, only surface as single-entity tops.
        for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
            try {
                JsonObject stat = entry.getValue().getAsJsonObject();
                // Serialized keys are always namespaced; normalize bare keys from hand-edited
                // payloads into the plugin namespace instead of Adventure's minecraft default.
                String name = entry.getKey();
                Key identity = name.indexOf(':') >= 0 ? Key.key(name) : Key.key(FunnyGuilds.NAMESPACE, name);
                String type = stat.get("type").getAsString();
                if ("integer".equals(type)) {
                    StatKey<Integer> key = StatKey.integerStatKey(identity);
                    if (!UserStatKeys.VALUES.contains(key)) {
                        service.setUserStatValue(user, key, stat.get("value").getAsBigDecimal().intValueExact());
                    }
                }
                else if ("double".equals(type)) {
                    StatKey<Double> key = StatKey.doubleStatKey(identity);
                    if (!UserStatKeys.VALUES.contains(key)) {
                        service.setUserStatValue(user, key, stat.get("value").getAsDouble());
                    }
                }
                else {
                    throw new IllegalArgumentException("Unsupported statistic type " + type);
                }
            }
            catch (RuntimeException failure) {
                FunnyGuilds.getPluginLogger().warning("Invalid statistic " + entry.getKey() + " for user " + user.getUUID() + ": " + failure.getMessage());
            }
        }
    }
}
