package q6.core.users.api

import q6.platform.errors.DomainException

class DuplicatedEmail(email: String, cause: Throwable) : DomainException("Duplicated email=$email", cause)