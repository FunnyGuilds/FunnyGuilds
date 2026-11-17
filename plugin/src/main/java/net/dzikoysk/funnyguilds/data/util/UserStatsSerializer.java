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

    /**
     * Serializes every non-built-in statistic of the user into a JSON object: each entry is keyed by the
     * fully qualified key name and carries a {@code type} ({@code integer} or {@code double}) and a
     * {@code value}. Built-ins are omitted - their legacy columns remain authoritative.
     */
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

    /**
     * Registers and applies serialized statistics to the user. Bare key names are normalized into the
     * {@code funnyguilds} namespace; built-in keys are skipped in favor of the authoritative legacy
     * columns. Keys unknown to this session are registered rather than dropped, so stored custom
     * statistics survive a session that does not declare them; such keys surface only as single-entity
     * tops. Malformed entries are logged and skipped; a null or blank payload is ignored.
     */
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
        for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
            try {
                JsonObject stat = entry.getValue().getAsJsonObject();
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
