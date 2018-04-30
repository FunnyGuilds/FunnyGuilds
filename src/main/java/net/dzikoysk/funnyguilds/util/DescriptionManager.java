package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Field;

public final class DescriptionManager {

    private final PluginDescriptionFile desc;

    public DescriptionManager(PluginDescriptionFile desc) {
        this.desc = desc;
    }

    public void rename(String s) {
        if (s == null || s.isEmpty()) {
            return;
        }
        
        try {
            Field field = Reflections.getPrivateField(desc.getClass(), "name");

            if (field == null) {
                return;
            }

            field.set(desc, s);
        } catch (Exception e) {
            FunnyLogger.exception(e.getCause());
        }
    }

    public String extractVersion() {
        String version = desc.getVersion();
        String[] array = version.split("-");

        if (array.length != 2) {
            return version;
        }

        return array[0];
    }

}
