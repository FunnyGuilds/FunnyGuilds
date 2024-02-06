package net.dzikoysk.funnyguilds.nms.api;

import org.bukkit.Bukkit;

import static java.lang.String.format;

public class NmsAccessorHolder {
    static final NmsAccessor INSTANCE = newAccessorInstance();

    private static NmsAccessor newAccessorInstance() {
        String nmsVersion = getNmsVersion();
        String className = format("net.dzikoysk.funnyguilds.nms.%s.%sNmsAccessor", nmsVersion, nmsVersion.toUpperCase());

        try {
            return (NmsAccessor) Class.forName(className).newInstance();
        } catch (Throwable th) {
            throw new RuntimeException(format("could not initialize NmsAccessor for version '%s'", nmsVersion), th);
        }
    }

    private static String getNmsVersion() {
        StringBuilder nmsVersion = new StringBuilder(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);

        int revPosition = nmsVersion.lastIndexOf("_");
        nmsVersion.deleteCharAt(revPosition);

        return nmsVersion.toString();
    }
}
