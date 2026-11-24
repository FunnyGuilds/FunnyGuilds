package net.dzikoysk.funnyguilds.shared.bukkit

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`

internal fun withEnchantmentRegistry(action: (Registry<Enchantment>) -> Unit) {
    mockStatic(RegistryAccess::class.java).use { registryMock ->
        // Paper initializes legacy Registry and Enchantment constants while these classes load.
        val access = mock(RegistryAccess::class.java) { invocation ->
            if (invocation.method.name == "getRegistry") {
                mock(Registry::class.java) { lookup ->
                    if (lookup.method.name == "get" && invocation.arguments[0] == Enchantment::class.java) {
                        mock(Enchantment::class.java)
                    }
                    else RETURNS_DEFAULTS.answer(lookup)
                }
            }
            else RETURNS_DEFAULTS.answer(invocation)
        }
        registryMock.`when`<RegistryAccess> { RegistryAccess.registryAccess() }.thenReturn(access)
        @Suppress("UNCHECKED_CAST")
        val registry = mock(Registry::class.java) as Registry<Enchantment>
        `when`(access.getRegistry(RegistryKey.ENCHANTMENT)).thenReturn(registry)
        action(registry)
    }
}
