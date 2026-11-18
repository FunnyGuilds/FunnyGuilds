package net.dzikoysk.funnyguilds.feature.purchase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.inventory.ItemStack;

public record PurchasePrice(List<ItemStack> items, BigDecimal money) {

    static final PurchasePrice FREE = new PurchasePrice(List.of(), BigDecimal.ZERO);

    public PurchasePrice {
        items = List.copyOf(items);
    }

    static PurchasePrice scale(List<ItemStack> items, double money, Duration period, Duration unit, int moneyScale) {
        BigDecimal numerator = seconds(period);
        BigDecimal denominator = seconds(unit);
        List<ItemStack> scaled = new ArrayList<>();
        for (ItemStack item : items) {
            scaled.stream().filter(item::isSimilar).findFirst().ifPresentOrElse(
                    existing -> existing.setAmount(Math.addExact(existing.getAmount(), item.getAmount())),
                    () -> scaled.add(item.clone()));
        }
        scaled.forEach(item -> item.setAmount(BigDecimal.valueOf(item.getAmount()).multiply(numerator)
                .divide(denominator, 0, RoundingMode.CEILING).intValueExact()));
        BigDecimal scaledMoney = BigDecimal.valueOf(money).multiply(numerator).divide(denominator, moneyScale, RoundingMode.CEILING);
        if (!Double.isFinite(scaledMoney.doubleValue())) {
            throw new ArithmeticException("Guild purchase price exceeds Vault's numeric range");
        }
        return new PurchasePrice(scaled, scaledMoney);
    }

    private static BigDecimal seconds(Duration duration) {
        return BigDecimal.valueOf(duration.getSeconds()).add(BigDecimal.valueOf(duration.getNano(), 9));
    }

    public static String formatItems(List<ItemStack> items) {
        return items.stream().map(item -> item.getAmount() + " × " + item.getType().name().toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(", "));
    }

    public String describe() {
        String cost = Stream.of(formatItems(this.items), this.money.signum() == 0 ? "" : this.money.toPlainString())
                .filter(part -> !part.isEmpty()).collect(Collectors.joining(" + "));
        return cost.isEmpty() ? "0" : cost;
    }
}
