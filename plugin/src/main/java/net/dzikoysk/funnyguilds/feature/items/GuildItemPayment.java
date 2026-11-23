package net.dzikoysk.funnyguilds.feature.items;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.feature.items.ItemAllocationGraph.Edge;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemMatcher;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** Allocates storage items to requirements without spending any item twice. Use synchronously. */
public final class GuildItemPayment {

    private final Inventory inventory;
    private final List<InventorySlot> slots;
    private final List<RequirementAllocation> allocations;

    public GuildItemPayment(Inventory inventory, GuildItemSet set, ItemsConfiguration config) {
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

    private static List<ItemRequirement> resolveRequirements(GuildItemSet set, ItemsConfiguration config) {
        if (!set.requirements.itemsEnabled) {
            return List.of();
        }
        return set.getItems().entrySet().stream()
                .map(entry -> resolveRequirement(entry.getKey(), entry.getValue(), config))
                .toList();
    }

    private static ItemRequirement resolveRequirement(String key, int amount, ItemsConfiguration config) {
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
        if (!isSatisfied()) {
            throw new IllegalStateException("Cannot remove items for unsatisfied guild requirements");
        }
        this.slots.forEach(slot -> slot.removeAllocatedItems(this.inventory));
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

        void removeAllocatedItems(Inventory inventory) {
            int amount = this.stock.allocatedAmount();
            if (amount == 0) {
                return;
            }
            ItemStack remaining = inventory.getItem(this.index).clone();
            remaining.setAmount(remaining.getAmount() - amount);
            inventory.setItem(this.index, remaining.getAmount() == 0 ? null : remaining);
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
