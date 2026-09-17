package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class BuyLifeConfiguration extends OkaeriConfig {

    @Comment("Czy mozliwosc wykupowania zyc (/g kupzycie) jest wlaczona")
    public boolean enabled = true;

    @Comment("Typ platnosci: ITEMS (materialy), MONEY (Vault) lub BOTH (materialy + Vault)")
    @CustomKey("payment-type")
    public BuyLifePaymentType paymentType = BuyLifePaymentType.BOTH;

    @Comment("Koszt bazowy w przedmiotach")
    public List<ItemStack> items = ItemUtils.parseItems("16 diamond_block", "8 emerald_block");

    @PositiveOrZero
    @Comment("Koszt bazowy w walucie serwerowej (Vault)")
    public double money = 10000.0;

    @Comment("Skalowanie kosztu w zaleznosci od aktualnie posiadanych zyc")
    @CustomKey("scaling-cost")
    public ScalingCostConfiguration scalingCost = new ScalingCostConfiguration();

    @Comment("Czy mozna wykupowac zycia powyzej bazowego limitu 'war.lives' (az do 'war.max-lives')")
    @CustomKey("allow-buy-up-to-max-lives")
    public boolean allowBuyUpToMaxLives = true;

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("Minimalny odstep czasowy miedzy kolejnymi wykupami zycia przez dana gildie")
    public Duration cooldown = Duration.ofHours(6);

    public enum BuyLifePaymentType {
        ITEMS,
        MONEY,
        BOTH
    }

    public static class ScalingCostConfiguration extends OkaeriConfig {
        @Comment("Czy skalowanie kosztu ma byc wlaczone")
        public boolean enabled = true;

        @PositiveOrZero
        @Comment("Mnoznik zwiekszajacy koszt za kazde posiadane zycie powyzej bazowego poziomu war.lives")
        @Comment("Wzor: KosztKoncowy = KosztBazowy * (1 + (aktualneZycia - bazoweZycia) * mnoznik)")
        @CustomKey("cost-multiplier-per-life")
        public double costMultiplierPerLife = 0.5;
    }
}
