package q6.infra.http4k

import org.http4k.template.TemplateRenderer
import org.http4k.template.Templates
import org.http4k.template.ViewModel
import org.http4k.template.ViewNotFound
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.TemplateEngine
import org.thymeleaf.cache.StandardCacheManager
import org.thymeleaf.context.Context
import org.thymeleaf.exceptions.TemplateInputException
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.io.FileNotFoundException


class ThymeleafWithSelectorTemplates(
    private val configure: (TemplateEngine) -> TemplateEngine = { it },
    private val classLoader: ClassLoader = ClassLoader.getSystemClassLoader()
) : Templates {
    override fun CachingClasspath(baseClasspathPackage: String): TemplateRenderer =
        ThymeleafTemplateRenderer(configure(TemplateEngine().apply {
            setTemplateResolver(ClassLoaderTemplateResolver(classLoader).apply {
                prefix = if (baseClasspathPackage.isEmpty()) "" else baseClasspathPackage.replace('.', '/') + "/"
                suffix = ".html"
            })
        }))

    override fun Caching(baseTemplateDir: String): TemplateRenderer =
        ThymeleafTemplateRenderer(configure(TemplateEngine().apply {
            setTemplateResolver(FileTemplateResolver().apply {
                prefix = "$baseTemplateDir/"
                suffix = ".html"
            })
        }))

    override fun HotReload(baseTemplateDir: String): TemplateRenderer =
        ThymeleafTemplateRenderer(configure(TemplateEngine().apply {
            cacheManager = StandardCacheManager().apply {
                templateCacheMaxSize = 0
            }
            setTemplateResolver(FileTemplateResolver().apply {
                prefix = "$baseTemplateDir/"
                suffix = ".html"
            })
        }))

    private class ThymeleafTemplateRenderer(private val engine: ITemplateEngine) : TemplateRenderer {
        override fun invoke(viewModel: ViewModel): String = try {
            val parts = viewModel.template().split(" :: ")
            val template = parts[0]
            val selector = if (parts.size > 1) setOf(parts[1]) else emptySet()
            engine.process(template, selector, Context().apply {
                setVariable("model", viewModel)
            })
        } catch (e: TemplateInputException) {
            when (e.cause) {
                is FileNotFoundException -> throw ViewNotFound(viewModel)
                else -> throw e
            }
        }
    }
}
