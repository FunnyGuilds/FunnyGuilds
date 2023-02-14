package net.dzikoysk.funnyguilds.server.event

import kotlin.reflect.KClass

enum class StandardizedEvent(type: KClass<out FunnyEvent>) {
    PLAYER_JOIN(FunnyJoinEvent::class)
}