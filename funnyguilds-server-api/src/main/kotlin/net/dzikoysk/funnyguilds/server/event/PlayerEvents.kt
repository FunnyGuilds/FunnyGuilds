package net.dzikoysk.funnyguilds.server.event

import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer

data class FunnyJoinEvent(val player: FunnyPlayer) : FunnyEvent