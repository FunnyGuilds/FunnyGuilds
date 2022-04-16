package net.dzikoysk.funnyguilds.nms.api.playerlist;

import com.mojang.authlib.properties.Property;

public class SkinTexture {

    private final String value;
    private final String signature;

    private final Property property;

    public SkinTexture(String value, String signature) {
        this.value = value;
        this.signature = signature;

        this.property = new Property("textures", this.value, this.signature);
    }

    public String getValue() {
        return this.value;
    }

    public String getSignature() {
        return this.signature;
    }

    public Property getProperty() {
        return this.property;
    }
}
