package net.dzikoysk.funnyguilds.shared.bukkit

import groovy.transform.CompileStatic
import org.bukkit.Location
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class LocationUtilsTest {

    @Test
    void 'should return flat distance between two locations in 3D space' () {
        Location a = new Location(null, 6, 0, 2)
        Location b = new Location(null, 6, 100, 6)

        assertEquals 4.0D, LocationUtils.flatDistance(a, b)
    }

}
