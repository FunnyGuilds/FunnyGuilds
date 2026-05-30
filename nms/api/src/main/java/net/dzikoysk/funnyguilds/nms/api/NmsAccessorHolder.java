package net.dzikoysk.funnyguilds.nms.api;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import org.jetbrains.annotations.Nullable;
import static java.lang.String.format;

public final class NmsAccessorHolder {

    // Version can be found in server jar in version.json under `world_version` key
    private static final TreeMap<Integer, String> NMS_VERSION_MAPPING = new TreeMap<>(ImmutableMap.<Integer, String>builder()
            .put(3953, "v1_21")
            .put(4080, "v1_21_2")
            .put(4189, "v1_21_4")
            .put(4554, "v1_21_9")
            .put(4786, "v26_1")
            .build());

    static final NmsAccessor INSTANCE = newAccessorInstance();

    private static NmsAccessor newAccessorInstance() {
        String nmsVersion = getNmsVersion();
        if (nmsVersion == null) {
            throw new RuntimeException("No compatible NmsAccessor version found");
        }

        try {
            String className = format("net.dzikoysk.funnyguilds.nms.%s.%sNmsAccessor", nmsVersion, nmsVersion.toUpperCase());
            return (NmsAccessor) Class.forName(className).getConstructor().newInstance();
        } catch (Throwable th) {
            throw new RuntimeException(format("Could not initialize NmsAccessor for version '%s'", nmsVersion), th);
        }
    }

    @Nullable
    private static String getNmsVersion() {
        try {
            Method getDataVersion = UnsafeValues.class.getMethod("getDataVersion");
            int dataVersion = (int) getDataVersion.invoke(Bukkit.getServer().getUnsafe());
            Map.Entry<Integer, String> versionEntry = NMS_VERSION_MAPPING.floorEntry(dataVersion);
            return versionEntry != null ? versionEntry.getValue() : null;
        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException("Could not get minecraft version", ex);
        }
    }

}
