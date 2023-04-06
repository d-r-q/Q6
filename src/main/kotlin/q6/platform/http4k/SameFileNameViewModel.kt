package q6.platform.http4k

import org.http4k.template.ViewModel

interface SameFileNameViewModel : ViewModel {

    val selector: String?
        get() = null

    override fun template(): String {
        val template = this::class.qualifiedName?.replace(".", "/") ?: error("Cannot get page name from ${this::class}")
        return buildString {
            append(template)
            if (selector != null) {
                append(" :: ")
                append(selector)
            }
        }
    }

}

class StaticPage(private val path: String) : ViewModel {

    override fun template(): String = path

}