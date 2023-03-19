package net.dzikoysk.funnyguilds.nms.v1_16R3.entity;

import org.bukkit.entity.EntityType;

public enum ObjectType {

    BOAT(1),
    AREA_EFFECT_CLOUD(3),
    MINECART(10),
    PRIMED_TNT(50),
    ENDER_CRYSTAL(51),
    TIPPED_ARROW(60),
    SNOWBALL(61),
    EGG(62),
    FIREBALL(63),
    SMALL_FIREBALL(64),
    ENDER_PEARL(65),
    WITHER_SKULL(66),
    SHULKER_BULLET(67),
    LLAMA_SPIT(68),
    FALLING_BLOCK(70),
    ITEM_FRAME(71),
    ENDER_SIGNAL(72),
    SPLASH_POTION(73),
    THROWN_EXP_BOTTLE(75),
    FIREWORK(76),
    LEASH_HITCH(77),
    ARMOR_STAND(78),
    EVOKER_FANGS(79),
    FISHING_HOOK(90),
    SPECTRAL_ARROW(91),
    DRAGON_FIREBALL(93);

    private final int id;

    ObjectType(int id) {
        this.id = id;
    }

    public static int getIdFor(EntityType type) {
        for (ObjectType ot : values()) {
            if (ot.toString().equals(type.toString())) {
                return ot.id;
            }
        }

        return ENDER_CRYSTAL.id;
    }

}
