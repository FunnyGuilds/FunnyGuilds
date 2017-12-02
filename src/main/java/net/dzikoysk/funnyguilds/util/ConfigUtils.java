package net.dzikoysk.funnyguilds.util;

import org.apache.commons.lang.Validate;
import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;

import java.io.File;
import java.io.IOException;

public final class ConfigUtils {

    private ConfigUtils() {
    }

    public static <T> T loadConfig(File file, Class<T> implementationFile) {

        Template<T> template = TemplateCreator.getTemplate(implementationFile);
        T config;

        if (!file.exists()) {
            try {
                try {
                    config = template.fillDefaults(implementationFile.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("Couldn't get access to " + implementationFile.getName() + "  constructor", e);
                }

                Validate.isTrue(file.createNewFile(), "Couldn't create " + file.getAbsolutePath() + " config file");

            } catch (IOException e) {
                throw new RuntimeException("IO exception when creating config file: " + file.getAbsolutePath(), e);
            }
        } else {
            try {
                try {
                    config = template.load(file);
                    if (config == null) {
                        config = template.fillDefaults(implementationFile.newInstance());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("IO exception when loading config file: " + file.getAbsolutePath(), e);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't get access to " + implementationFile.getName() + "  constructor", e);
            }
        }

        try {
            template.dump(file, config, false);
        } catch (IOException e) {
            throw new RuntimeException("Can't dump configuration file!", e);
        }

        return config;
    }
}
