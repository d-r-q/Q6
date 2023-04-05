package q6.platform.http4k

import org.http4k.template.ViewModel

interface SameFileNameViewModel : ViewModel {

    override fun template(): String {
        val path = this::class.qualifiedName?.replace(".", "/") ?: error("Cannot get page name from ${this::class}")
        return "$path.html"
    }

}

class StaticPage(private val path: String) : ViewModel {

    override fun template(): String = path

}