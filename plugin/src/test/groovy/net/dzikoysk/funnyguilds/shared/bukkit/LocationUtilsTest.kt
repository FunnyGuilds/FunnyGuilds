package net.dzikoysk.funnyguilds.shared.bukkit

import groovy.transform.CompileStatic
import org.bukkit.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@CompileStatic
class LocationUtilsTest {

    @Test
    fun should_return_flat_distance_between_two_locations_in_3D_space() {
        val a = Location(null, 6.0, 0.0, 2.0)
        val b = Location(null, 6.0, 100.0, 6.0)

        assertEquals(4.0, LocationUtils.flatDistance(a, b))
    }

}