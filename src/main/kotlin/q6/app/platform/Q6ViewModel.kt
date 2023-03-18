package q6.app.platform

import org.http4k.template.ViewModel

interface Q6ViewModel : ViewModel {

    override fun template(): String {
        val path = this::class.qualifiedName?.replace(".", "/") ?: error("Cannot get page name from ${this::class}")
        return "$path.html"
    }

}

class StaticPage(private val path: String) : ViewModel {

    override fun template(): String = path

}