package net.dzikoysk.funnyguilds.util.nms;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Field;

public final class DescriptionChanger {

    private final PluginDescriptionFile descriptionFile;

    public DescriptionChanger(PluginDescriptionFile descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public void rename(String pluginName) {
        if (pluginName == null || pluginName.isEmpty()) {
            return;
        }
        
        try {
            Field field = Reflections.getPrivateField(descriptionFile.getClass(), "name");

            if (field == null) {
                return;
            }

            field.set(descriptionFile, pluginName);
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not change description file", ex);
        }
    }

    public Pair<String, String> extractVersion() {
        String version = descriptionFile.getVersion();

        return Pair.of(version, version.substring(0, version.lastIndexOf('-')));
    }

}
