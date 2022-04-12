package net.dzikoysk.funnyguilds

import org.bukkit.Bukkit
import org.bukkit.World
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
open class BukkitSpec {

    protected lateinit var mockedBukkit: MockedStatic<Bukkit>

    @BeforeEach
    fun openMockedBukkit() {
        mockedBukkit = Mockito.mockStatic(Bukkit::class.java)
    }

    @BeforeEach
    protected open fun prepareBukkit() {
        val world = Mockito.mock(World::class.java)
        mockedBukkit.`when`<Any?> { Bukkit.getPlayer(ArgumentMatchers.any(UUID::class.java)) }.thenReturn(null)
        mockedBukkit.`when`<Any> { Bukkit.getWorlds() }.thenReturn(listOf(world))
    }

    @AfterEach
    fun closeMockedBukkit() {
        mockedBukkit.close()
    }

}