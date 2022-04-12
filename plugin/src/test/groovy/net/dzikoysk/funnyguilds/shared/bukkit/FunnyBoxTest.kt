package net.dzikoysk.funnyguilds.shared.bukkit

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.BukkitSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.Region
import org.bukkit.Location
import org.bukkit.util.Vector
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@CompileStatic
class FunnyBoxTest : BukkitSpec() {

    @Test
    fun test_FunnyBox() {
        val createMinDistanceFromBorder = 50.0

        val radius = 10000.0 / 2
        val centerOfWorld = Vector(0, 0, 0)

        val centerOfGuild = Location(null, 2500.0, 100.0, 2500.0)
        val guild = Guild("guild", "TEST")
        val region = Region(guild, centerOfGuild, 100)

        val bbox = FunnyBox.of(centerOfWorld, radius - createMinDistanceFromBorder, 256.0, radius - createMinDistanceFromBorder)
        val gbox = FunnyBox.of(region.firstCorner, region.secondCorner)

        assertTrue(bbox.contains(gbox))
    }

}