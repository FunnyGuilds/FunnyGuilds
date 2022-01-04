package net.dzikoysk.funnyguilds.config.transformer;

import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.ObjectTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import eu.okaeri.configs.serdes.SimpleObjectTransformer;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public class DecolorTransformer extends ObjectTransformer<String, String> {

    @Exclude
    private static final ObjectTransformer<String, String> TRANSFORMER = SimpleObjectTransformer.of(String.class, String.class, ChatUtils::decolor);

    @Override
    public GenericsPair getPair() {
        return TRANSFORMER.getPair();
    }

    @Override
    public String transform(String data, SerdesContext context) {
        return TRANSFORMER.transform(data, context);
    }

}