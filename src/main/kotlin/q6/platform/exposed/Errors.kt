package q6.platform.exposed

import org.jetbrains.exposed.exceptions.ExposedSQLException

fun isDuplicatedUniqueKey(ex: Throwable): Boolean = (ex as? ExposedSQLException)?.sqlState == "23505"