package q6.platform.http4k

import org.http4k.core.Response

fun Response.location(location: String): Response = this.header("Location", location)
