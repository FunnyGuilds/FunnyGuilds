package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.commons.ConfigUtils;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class Messages {

    private static MessagesConfig instance;
    private static File messages = new File(FunnyGuilds.getInstance().getDataFolder(), "messages.yml");

    @SuppressWarnings("unchecked")
    public Messages() {
        instance = ConfigUtils.loadConfig(messages, MessagesConfig.class);

        try {
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (field.getType().equals(String.class)) {
                    field.set(instance, ChatUtils.colored((String) field.get(instance)));
                }
                
                if (field.getType().equals(List.class)) {
                    List<String> list = (List<String>) field.get(instance);
                    
                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, ChatUtils.colored(list.get(i)));
                    }
                }
            }
        } catch (Exception e) {
            FunnyGuildsLogger.exception(e);
        }
    }

    public static MessagesConfig getInstance() {
        if (instance == null) {
            new Messages();
        }
        
        return instance;
    }

}
