package net.dzikoysk.funnyguilds.config

import org.bukkit.Material

class MockPluginConfiguration : PluginConfiguration() {
    override fun defaultBuggedBlocksExclude(): Set<Material?> = emptySet()
}
