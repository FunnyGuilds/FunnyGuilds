package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class EntityTypeTransformer extends BidirectionalTransformer<String, EntityType> {

    @Override
    public GenericsPair<String, EntityType> getPair() {
        return this.genericsPair(String.class, EntityType.class);
    }

    @Override
    public EntityType leftToRight(@NotNull String data, @NotNull SerdesContext serdesContext) {
        return EntityUtils.parseEntityType(data, true);
    }

    @Override
    public String rightToLeft(EntityType data, @NotNull SerdesContext serdesContext) {
        return data.name();
    }

}
