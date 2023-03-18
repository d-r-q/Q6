package q6.app.platform

import org.http4k.core.Response

fun Response.location(location: String): Response = this.header("Location", location)
