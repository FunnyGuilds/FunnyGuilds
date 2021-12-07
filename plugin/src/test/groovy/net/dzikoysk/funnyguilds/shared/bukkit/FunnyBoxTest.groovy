package net.dzikoysk.funnyguilds.shared.bukkit

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.BukkitSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.Region
import org.bukkit.Location
import org.bukkit.util.Vector
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class FunnyBoxTest extends BukkitSpec {

    @Test
    void 'should work pls' () {
        double createMinDistanceFromBorder = 50

        double radius = 10_000 / 2
        Vector centerOfWorld = new Vector(0, 0, 0);

        Location centerOfGuild = new Location(null, 2500, 100, 2500);
        Guild guild = new Guild("Name of guild")
        Region region = new Region(guild, centerOfGuild, 100);

        FunnyBox bbox = FunnyBox.of(centerOfWorld, radius - createMinDistanceFromBorder, 256, radius - createMinDistanceFromBorder)
        FunnyBox gbox = FunnyBox.of(region.getFirstCorner(), region.getSecondCorner());

        assertTrue bbox.contains(gbox)
    }

}
