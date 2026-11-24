package net.dzikoysk.funnyguilds.feature.items;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsSnapshot;
import net.dzikoysk.funnyguilds.feature.items.ItemAllocationGraph.Edge;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemMatcher;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** Coordinates guild creation item allocation, reservation and money withdrawal. Use on the server thread. */
public final class GuildCreationPayment {

    private final Inventory inventory;
    private final List<InventorySlot> slots;
    private final List<RequirementAllocation> allocations;
    private boolean consumed;

    public GuildCreationPayment(Inventory inventory, GuildItemSet set, ItemsSnapshot config) {
        this.inventory = inventory;
        ItemAllocationGraph graph = new ItemAllocationGraph();
        this.slots = readStorageSlots(inventory, graph);
        this.allocations = resolveRequirements(set, config).stream()
                .map(requirement -> connectMatchingSlots(requirement, graph))
                .toList();
        graph.allocateItems();
    }

    private static List<InventorySlot> readStorageSlots(Inventory inventory, ItemAllocationGraph graph) {
        ItemStack[] contents = inventory.getStorageContents();
        return IntStream.range(0, contents.length)
                .filter(index -> contents[index] != null && contents[index].getAmount() > 0)
                .mapToObj(index -> new InventorySlot(index, contents[index], graph.addStock(contents[index].getAmount())))
                .toList();
    }

    private static List<ItemRequirement> resolveRequirements(GuildItemSet set, ItemsSnapshot config) {
        if (!set.requirements.itemsEnabled) {
            return List.of();
        }
        return set.getItems().entrySet().stream()
                .map(entry -> resolveRequirement(entry.getKey(), entry.getValue(), config))
                .toList();
    }

    private static ItemRequirement resolveRequirement(String key, int amount, ItemsSnapshot config) {
        Predicate<ItemStack> matcher = config.getLibraryItem(key)
                .<Predicate<ItemStack>>map(ItemMatcher::new)
                .orElse(item -> false);
        return new ItemRequirement(key, Math.max(0, amount), matcher);
    }

    private RequirementAllocation connectMatchingSlots(ItemRequirement requirement, ItemAllocationGraph graph) {
        Edge demand = graph.addRequirement(requirement.amount());
        List<SlotAllocation> matchingSlots = this.slots.stream()
                .filter(slot -> requirement.matcher().test(slot.item()))
                .map(slot -> new SlotAllocation(slot, graph.connectMatch(demand, slot.stock())))
                .toList();
        return new RequirementAllocation(requirement, demand, matchingSlots);
    }

    public boolean isSatisfied() {
        return this.allocations.stream().allMatch(RequirementAllocation::isSatisfied);
    }

    /** Allocated items plus matching surplus, excluding items reserved for other requirements. */
    public Map<String, Integer> getAvailableCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (RequirementAllocation allocation : this.allocations) {
            counts.put(allocation.requirement().key(), allocation.availableCount());
        }
        return Collections.unmodifiableMap(counts);
    }

    /** Ender chest counts are informational and never contribute to payment. */
    public Map<String, Integer> countMatching(Inventory other) {
        ItemStack[] contents = other.getStorageContents();
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (RequirementAllocation allocation : this.allocations) {
            ItemRequirement requirement = allocation.requirement();
            counts.put(requirement.key(), requirement.countMatching(contents));
        }
        return Collections.unmodifiableMap(counts);
    }

    /** Applies the successful allocation to actual slots, preserving all remaining metadata. */
    public void removeItems() {
        List<SlotRemoval> removals = prepareRemoval()
                .orElseThrow(() -> new IllegalStateException("Guild item requirements are unsatisfied or the inventory has changed"));
        applyRemoval(removals);
    }

    /** Reserves validated items before calling the economy provider; failed charges return the exact items. */
    public PaymentResult pay(BooleanSupplier withdrawMoney, Consumer<List<ItemStack>> refundItems) {
        Optional<List<SlotRemoval>> prepared = prepareRemoval();
        if (prepared.isEmpty()) {
            return PaymentResult.ITEMS_CHANGED;
        }
        List<SlotRemoval> removals = prepared.orElseThrow();
        List<ItemStack> reserved = removals.stream().map(SlotRemoval::removed).toList();
        applyRemoval(removals);

        boolean withdrawn;
        try {
            withdrawn = withdrawMoney.getAsBoolean();
        }
        catch (RuntimeException exception) {
            refundItems.accept(reserved);
            throw exception;
        }
        if (!withdrawn) {
            refundItems.accept(reserved);
            return PaymentResult.WITHDRAWAL_FAILED;
        }
        return PaymentResult.SUCCESS;
    }

    private Optional<List<SlotRemoval>> prepareRemoval() {
        if (this.consumed || !isSatisfied() || !matchesAllocatedRequirements()) {
            return Optional.empty();
        }
        List<SlotRemoval> removals = new ArrayList<>();
        for (InventorySlot slot : this.slots) {
            if (slot.stock().allocatedAmount() == 0) {
                continue;
            }
            Optional<SlotRemoval> removal = slot.prepareRemoval(this.inventory);
            if (removal.isEmpty()) {
                return Optional.empty();
            }
            removals.add(removal.orElseThrow());
        }
        return Optional.of(List.copyOf(removals));
    }

    private boolean matchesAllocatedRequirements() {
        return this.allocations.stream().allMatch(allocation -> allocation.matchingSlots().stream()
                .filter(match -> match.allocation().allocatedAmount() > 0)
                .allMatch(match -> allocation.requirement().matcher().test(this.inventory.getItem(match.slot().index()))));
    }

    private void applyRemoval(List<SlotRemoval> removals) {
        // No callbacks between validation and removal: use only on the server thread.
        removals.forEach(removal -> removal.apply(this.inventory));
        this.consumed = true;
    }

    public enum PaymentResult {
        SUCCESS,
        ITEMS_CHANGED,
        WITHDRAWAL_FAILED
    }

    private record ItemRequirement(String key, int amount, Predicate<ItemStack> matcher) {

        int countMatching(ItemStack[] contents) {
            return Arrays.stream(contents)
                    .filter(this.matcher)
                    .mapToInt(ItemStack::getAmount)
                    .sum();
        }

    }

    private record InventorySlot(int index, ItemStack item, Edge stock) {

        Optional<SlotRemoval> prepareRemoval(Inventory inventory) {
            int amount = this.stock.allocatedAmount();
            ItemStack current = inventory.getItem(this.index);
            if (current == null || current.getAmount() < amount) {
                return Optional.empty();
            }
            ItemStack removed = current.clone();
            removed.setAmount(amount);
            ItemStack remaining = current.clone();
            remaining.setAmount(remaining.getAmount() - amount);
            return Optional.of(new SlotRemoval(this.index, removed, remaining));
        }

    }

    private record SlotRemoval(int index, ItemStack removed, ItemStack remaining) {

        void apply(Inventory inventory) {
            inventory.setItem(this.index, this.remaining.getAmount() == 0 ? null : this.remaining);
        }

    }

    private record SlotAllocation(InventorySlot slot, Edge allocation) {

        int availableCount() {
            return this.allocation.allocatedAmount() + this.slot.stock().remainingCapacity();
        }

    }

    private record RequirementAllocation(ItemRequirement requirement, Edge demand, List<SlotAllocation> matchingSlots) {

        boolean isSatisfied() {
            return this.demand.remainingCapacity() == 0;
        }

        int availableCount() {
            return this.matchingSlots.stream().mapToInt(SlotAllocation::availableCount).sum();
        }

    }

}
