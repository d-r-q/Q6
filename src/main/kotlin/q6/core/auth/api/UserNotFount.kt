package q6.core.auth.api

import q6.platform.errors.DomainException

class UserNotFount(email: String) : DomainException(email)
