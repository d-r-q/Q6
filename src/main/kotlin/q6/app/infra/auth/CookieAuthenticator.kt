package q6.app.infra.auth

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.cookie.cookie
import q6.core.users.api.Role
import q6.core.users.api.UserId
import q6.core.users.api.UserIdentity

const val AUTH_TOKEN_COOKIE = "AUTH_TOKEN"
private const val USER_IDENTITY_QUERY_PARAM = "pro.azhidkov.platform.user_identity"

class CookieAuthenticator(
    private val exchange: (String) -> UserIdentity?
) : Filter {

    override fun invoke(next: HttpHandler): HttpHandler = { req ->
        val authToken = req.cookie(AUTH_TOKEN_COOKIE)?.value
        val authenticatedReq = if (authToken != null) {
            val userIdentity = exchange(authToken)
            if (userIdentity != null) {
                req.authenticate(userIdentity)
            } else {
                req
            }
        } else {
            req
        }
        next(authenticatedReq)
    }
}

private fun Request.authenticate(userIdentity: UserIdentity): Request = this.query(
    USER_IDENTITY_QUERY_PARAM,
    "${userIdentity.id.id};${userIdentity.identity};${userIdentity.roles.joinToString(",")}"
)

fun Request.userIdentity(): UserIdentity? {
    val userIdentityStr = this.query(USER_IDENTITY_QUERY_PARAM) ?: return null
    val (id, identity, rolesStr) = userIdentityStr.split(";")
    val roles = rolesStr.split(",")
        .filter { it.isNotBlank() }
        .map { Role.valueOf(it) }
        .toSet()
    return UserIdentity(UserId(id.toLong()), identity, roles)
}