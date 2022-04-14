package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;

public class SkinTextureSerializer implements ObjectSerializer<SkinTexture> {

    @Override
    public boolean supports(Class<? super SkinTexture> type) {
        return SkinTexture.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(SkinTexture texture, SerializationData data, GenericsDeclaration generics) {
        data.add("value", texture.getValue());
        data.add("signature", texture.getSignature());
    }

    @Override
    public SkinTexture deserialize(DeserializationData data, GenericsDeclaration generics) {
        String value = data.get("value", String.class);
        String signature = data.get("signature", String.class);
        return new SkinTexture(value, signature);
    }

}
