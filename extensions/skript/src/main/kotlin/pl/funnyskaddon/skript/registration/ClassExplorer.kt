package pl.funnyskaddon.skript.registration

import java.io.File
import java.net.URLDecoder

/**
 * Triggers initialization (and therefore syntax registration) of every class
 * in the given packages of the plugin's jar. Replaces Skript's deprecated
 * loadClasses mechanism.
 */
object ClassExplorer {

    fun loadClasses(classLoader: ClassLoader, basePackage: String, vararg subPackages: String) {
        for (subPackage in subPackages) {
            val packageName = "$basePackage.$subPackage"
            for (className in findClassNames(classLoader, packageName)) {
                try {
                    Class.forName(className, true, classLoader)
                } catch (throwable: Throwable) {
                    if (throwable !is ClassNotFoundException && throwable.cause !is ClassNotFoundException) {
                        throw throwable
                    }
                }
            }
        }
    }

    private fun findClassNames(classLoader: ClassLoader, packageName: String): List<String> {
        val packagePath = packageName.replace('.', '/')
        return classLoader.getResources(packagePath)
            .asSequence()
            .flatMap { url -> entriesOf(url, packagePath).asSequence() }
            .filter { it.endsWith(".class") && !it.contains('$') }
            .map { it.removeSuffix(".class").replace('/', '.') }
            .toSortedSet()
            .toList()
    }

    private fun entriesOf(url: java.net.URL, packagePath: String): List<String> {
        val path = URLDecoder.decode(url.file, Charsets.UTF_8.name())
        if (!path.contains(".jar!")) {
            val dir = File(path)
            return if (dir.isDirectory) {
                dir.walkTopDown().filter { it.isFile }.map { "$packagePath/${it.relativeTo(dir).path.replace(File.separatorChar, '/')}" }.toList()
            } else {
                emptyList()
            }
        }
        val jarPath = path.substringBefore(".jar!").substringAfter("file:") + ".jar"
        val jar = java.util.jar.JarFile(jarPath)
        return jar.use { it.entries().asSequence().map { entry -> entry.name }.toList() }
    }

}
