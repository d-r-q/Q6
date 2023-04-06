package q6.infra.http4k

import q6.platform.conf.AppConfig


class TemplatesModule(
    appConfig: AppConfig
) {

    val renderer = ThymeleafWithSelectorTemplates().let {
        if ("dev" in appConfig.get<Array<String>>("q6.profiles")) {
            it.HotReload("src/main/kotlin")
        } else {
            it.CachingClasspath()
        }
    }

}