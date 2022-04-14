package net.dzikoysk.funnyguilds.nms.api.playerlist;

public class SkinTexture {

    private final String value;
    private final String signature;

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

}
