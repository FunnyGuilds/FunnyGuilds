package net.dzikoysk.funnyguilds.config.transformer;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import org.bukkit.Material;

public class MaterialTransformer extends BidirectionalTransformer<String, Material> {

    @Override
    public GenericsPair<String, Material> getPair() {
        return this.genericsPair(String.class, Material.class);
    }

    @Override
    public Material leftToRight(String data, SerdesContext serdesContext) {
        return MaterialUtils.parseMaterial(data, false);
    }

    @Override
    public String rightToLeft(Material data, SerdesContext serdesContext) {
        return data.name();
    }

}
