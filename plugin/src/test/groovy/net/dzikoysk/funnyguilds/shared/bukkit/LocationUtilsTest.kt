package net.dzikoysk.funnyguilds.shared.bukkit

import org.bukkit.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LocationUtilsTest {

    @Test
    fun `should return flat distance between two locations in 3D space`() {
        val a = Location(null, 6.0, 0.0, 2.0)
        val b = Location(null, 6.0, 100.0, 6.0)

        assertEquals(4.0, LocationUtils.flatDistance(a, b))
    }

}