package q6.app.platform

import org.http4k.core.*


class UserIdentityAuthorizer(
    private val authRules: AuthorizationRules,
    private val forbiddenResponse: (Request) -> Response,
    private val notAuthenticatedResponse: (Request) -> Response,
) : Filter {

    override fun invoke(next: HttpHandler): HttpHandler = { req ->
        val userIdentity = req.userIdentity()
        when (authRules.isAuthorized(req, userIdentity)) {
            AuthorizationResult.AUTHORIZED -> next(req)
            AuthorizationResult.NOT_AUTHORIZED -> forbiddenResponse(req)
            AuthorizationResult.NOT_AUTHENTICATED -> notAuthenticatedResponse(req)
        }
    }

}
