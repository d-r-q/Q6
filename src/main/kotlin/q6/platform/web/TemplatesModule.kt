package q6.platform.web

import org.http4k.template.ThymeleafTemplates
import q6.platform.conf.AppConfig


class TemplatesModule(
    val appConfig: AppConfig
) {

    val renderer = ThymeleafTemplates().let {
        if ("dev" in appConfig.get<Array<String>>("q6.profiles")) {
            it.HotReload("src/main/kotlin")
        } else {
            it.CachingClasspath()
        }
    }

}