package net.dzikoysk.funnyguilds

import org.bukkit.Bukkit
import org.bukkit.World
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
open class BukkitSpec {

    companion object {
        @JvmStatic
        protected lateinit var mockedBukkit: MockedStatic<Bukkit>

        @BeforeAll
        @JvmStatic
        fun openMockedBukkit() {
            mockedBukkit = Mockito.mockStatic(Bukkit::class.java)
        }

        @AfterAll
        @JvmStatic
        fun closeMockedBukkit() {
            mockedBukkit.close()
        }
    }

    @BeforeEach
    protected open fun prepareBukkit() {
        val world = Mockito.mock(World::class.java)
        mockedBukkit.`when`<Any?> { Bukkit.getPlayer(ArgumentMatchers.any(UUID::class.java)) }.thenReturn(null)
        mockedBukkit.`when`<Any> { Bukkit.getWorlds() }.thenReturn(listOf(world))
    }

}