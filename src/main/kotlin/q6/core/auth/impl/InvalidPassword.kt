package q6.core.auth.impl

import q6.platform.errors.DomainException

class InvalidPassword(email: String) : DomainException(email)
