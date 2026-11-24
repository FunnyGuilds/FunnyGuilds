package pl.funnyskaddon.skript.condition
import ch.njol.skript.doc.Name
import ch.njol.skript.doc.Since
import ch.njol.skript.lang.Condition
import org.bukkit.event.Event
abstract class FunnyCondition : Condition() {

    override fun toString(event: Event?, debug: Boolean): String {
        return javaClass.getAnnotation(Name::class.java)?.value?.lowercase() ?: "funny condition"
    }

}
