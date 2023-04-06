package q6.app.infra.auth

import org.http4k.core.Request
import q6.core.users.api.Role
import q6.core.users.api.UserIdentity

enum class AuthorizationResult {
    AUTHORIZED,
    NOT_AUTHENTICATED,
    NOT_AUTHORIZED
}

interface AuthorizationRules {

    fun isAuthorized(req: Request, userIdentity: UserIdentity?): AuthorizationResult

}

class PathPatternAuthorizationRules(vararg rules: Pair<String, Set<Role>>) : AuthorizationRules {

    private val rules = rules
        .map { it.first.toPattern() to it.second }
        .toList()

    override fun isAuthorized(req: Request, userIdentity: UserIdentity?): AuthorizationResult {
        val userRoles= userIdentity?.roles ?: emptySet()
        val requiredRoles = rules.find { it.first.matcher(req.uri.path).matches() }
            ?.second
            ?: emptySet()

        if (userIdentity == null && requiredRoles.isNotEmpty()) {
            return AuthorizationResult.NOT_AUTHENTICATED
        }

        return if (requiredRoles.isEmpty() || userRoles.intersect(requiredRoles).isNotEmpty()) {
            AuthorizationResult.AUTHORIZED
        } else {
            AuthorizationResult.NOT_AUTHORIZED
        }
    }

}