package net.dzikoysk.funnyguilds.feature.items

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Each stock edge represents an inventory stack. Each requirement edge represents an item cost.
 * A match connects them only when the stack satisfies the configured properties.
 * Allocated amounts describe the final payment; remaining requirement capacity describes a shortage.
 */
class ItemAllocationGraphTest {

    @Test
    fun `ordinary diamond pays the diamond cost so the named diamond can pay the key cost`() {
        val graph = ItemAllocationGraph()
        val diamondCost = graph.addRequirement(1)
        val keyCost = graph.addRequirement(1)
        val namedDiamond = graph.addStock(1)
        val ordinaryDiamond = graph.addStock(1)

        // Requirements: one diamond of any name, and one item named "Key" of any material.
        // Put the named diamond first: initially it pays the diamond cost, blocking the key cost.
        val namedDiamondForDiamond = graph.connectMatch(diamondCost, namedDiamond)
        val ordinaryDiamondForDiamond = graph.connectMatch(diamondCost, ordinaryDiamond)
        val namedDiamondForKey = graph.connectMatch(keyCost, namedDiamond)

        graph.allocateItems()

        // Reassign the diamond cost to the ordinary diamond, freeing the named diamond for the key.
        assertEquals(0, namedDiamondForDiamond.allocatedAmount())
        assertEquals(1, ordinaryDiamondForDiamond.allocatedAmount())
        assertEquals(1, namedDiamondForKey.allocatedAmount())
        assertEquals(0, diamondCost.remainingCapacity())
        assertEquals(0, keyCost.remainingCapacity())
    }

    @Test
    fun `paying the fee requires moving both the diamond and key payments`() {
        val graph = ItemAllocationGraph()
        val diamondCost = graph.addRequirement(1)
        val keyCost = graph.addRequirement(1)
        val feeCost = graph.addRequirement(1)
        val diamondWithFeeLore = graph.addStock(1)
        val diamondNamedKey = graph.addStock(1)
        val emeraldNamedKey = graph.addStock(1)

        // Requirements: material DIAMOND, name "Key", and lore ["Fee"], each with quantity one.
        // Available matches:
        //                  diamond cost    key cost    fee cost
        // Diamond + Fee         yes                       yes
        // Diamond + Key         yes           yes
        // Emerald + Key                       yes
        val feeDiamondForDiamond = graph.connectMatch(diamondCost, diamondWithFeeLore)
        val keyDiamondForDiamond = graph.connectMatch(diamondCost, diamondNamedKey)
        val keyDiamondForKey = graph.connectMatch(keyCost, diamondNamedKey)
        val keyEmeraldForKey = graph.connectMatch(keyCost, emeraldNamedKey)
        val feeDiamondForFee = graph.connectMatch(feeCost, diamondWithFeeLore)

        graph.allocateItems()

        // Initially: diamond cost <- Diamond + Fee, key cost <- Diamond + Key; fee cost is blocked.
        // To pay the fee, move the key cost to Emerald + Key and the diamond cost to Diamond + Key.
        // This frees Diamond + Fee for the fee cost. Both earlier assignments must change.
        assertEquals(0, feeDiamondForDiamond.allocatedAmount())
        assertEquals(0, keyDiamondForKey.allocatedAmount())
        assertEquals(1, keyDiamondForDiamond.allocatedAmount())
        assertEquals(1, keyEmeraldForKey.allocatedAmount())
        assertEquals(1, feeDiamondForFee.allocatedAmount())
        assertEquals(0, diamondCost.remainingCapacity())
        assertEquals(0, keyCost.remainingCapacity())
        assertEquals(0, feeCost.remainingCapacity())
    }

    @Test
    fun `a named stack can be split between costs while leaving surplus untouched`() {
        val graph = ItemAllocationGraph()
        val diamondCost = graph.addRequirement(15)
        val keyCost = graph.addRequirement(5)
        val namedDiamonds = graph.addStock(10)
        val ordinaryDiamonds = graph.addStock(12)
        val namedForDiamonds = graph.connectMatch(diamondCost, namedDiamonds)
        val ordinaryForDiamonds = graph.connectMatch(diamondCost, ordinaryDiamonds)
        val namedForKeys = graph.connectMatch(keyCost, namedDiamonds)

        graph.allocateItems()

        // Reserve five named diamonds for keys. The other five and ten ordinary diamonds pay
        // the diamond cost. Two ordinary diamonds remain, and no item pays both costs.
        assertEquals(5, namedForDiamonds.allocatedAmount())
        assertEquals(10, ordinaryForDiamonds.allocatedAmount())
        assertEquals(5, namedForKeys.allocatedAmount())
        assertEquals(10, namedDiamonds.allocatedAmount())
        assertEquals(10, ordinaryDiamonds.allocatedAmount())
        assertEquals(2, ordinaryDiamonds.remainingCapacity())
        assertEquals(0, diamondCost.remainingCapacity())
        assertEquals(0, keyCost.remainingCapacity())
    }

    @Test
    fun `one named diamond cannot pay both the diamond and key costs`() {
        val graph = ItemAllocationGraph()
        val diamondCost = graph.addRequirement(1)
        val keyCost = graph.addRequirement(1)
        val namedDiamond = graph.addStock(1)
        val namedForDiamond = graph.connectMatch(diamondCost, namedDiamond)
        val namedForKey = graph.connectMatch(keyCost, namedDiamond)

        graph.allocateItems()

        // Both costs accept the item, but total demand is two and stock is only one.
        assertEquals(1, namedForDiamond.allocatedAmount() + namedForKey.allocatedAmount())
        assertEquals(1, namedDiamond.allocatedAmount())
        assertEquals(0, namedDiamond.remainingCapacity())
        assertEquals(1, diamondCost.remainingCapacity() + keyCost.remainingCapacity())
    }
}
