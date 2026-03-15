package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.dzikoysk.funnyguilds.config.sections.items.SetRequirements;
import org.jetbrains.annotations.NotNull;

public class SetRequirementsSerializer implements ObjectSerializer<SetRequirements> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return SetRequirements.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull SetRequirements req, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("items-enabled", req.itemsEnabled);
        data.add("experience-enabled", req.experienceEnabled);
        data.add("money-enabled", req.moneyEnabled);
        data.add("rank-enabled", req.rankEnabled);
    }

    @Override
    public SetRequirements deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        SetRequirements req = new SetRequirements();
        req.itemsEnabled = data.containsKey("items-enabled") ? data.get("items-enabled", boolean.class) : true;
        req.experienceEnabled = data.containsKey("experience-enabled") ? data.get("experience-enabled", boolean.class) : true;
        req.moneyEnabled = data.containsKey("money-enabled") ? data.get("money-enabled", boolean.class) : true;
        req.rankEnabled = data.containsKey("rank-enabled") ? data.get("rank-enabled", boolean.class) : true;
        return req;
    }

}

