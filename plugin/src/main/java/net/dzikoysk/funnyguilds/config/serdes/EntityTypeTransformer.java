package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import org.bukkit.entity.EntityType;

public class EntityTypeTransformer extends BidirectionalTransformer<String, EntityType> {

    @Override
    public GenericsPair<String, EntityType> getPair() {
        return this.genericsPair(String.class, EntityType.class);
    }

    @Override
    public EntityType leftToRight(String data, SerdesContext serdesContext) {
        return EntityUtils.parseEntityType(data, true);
    }

    @Override
    public String rightToLeft(EntityType data, SerdesContext serdesContext) {
        return data.name();
    }

}
