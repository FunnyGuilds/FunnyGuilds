package net.dzikoysk.funnyguilds.listener

import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredListener

// Exercise Bukkit's cancellation gate using the handler's actual annotation settings.
internal fun registeredHandler(
    listener: Listener,
    methodName: String,
    eventType: Class<out Event>,
    plugin: Plugin
): RegisteredListener {
    val method = listener.javaClass.getMethod(methodName, eventType)
    val handler = checkNotNull(method.getAnnotation(EventHandler::class.java))
    return RegisteredListener(
        listener,
        { registeredListener, event -> method.invoke(registeredListener, event) },
        handler.priority,
        plugin,
        handler.ignoreCancelled
    )
}
