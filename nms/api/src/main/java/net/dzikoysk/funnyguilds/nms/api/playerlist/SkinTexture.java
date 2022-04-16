package net.dzikoysk.funnyguilds.nms.api.playerlist;

import com.mojang.authlib.properties.Property;

public class SkinTexture {

    private final String value;
    private final String signature;

    private Property property;

    public SkinTexture(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return this.value;
    }

    public String getSignature() {
        return this.signature;
    }

    public Property getProperty() {
        if (this.property == null) {
            this.property = new Property("textures", this.value, this.signature);
        }
        return this.property;
    }
}
