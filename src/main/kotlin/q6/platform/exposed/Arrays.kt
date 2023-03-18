package q6.platform.exposed


import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.CustomStringFunction
import org.jetbrains.exposed.sql.EqOp
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.statements.jdbc.JdbcPreparedStatementImpl
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.io.Serializable
import kotlin.Array
import kotlin.reflect.KClass
import java.sql.Array as SQLArray

/**
 * Creates a text array column with [name].
 *
 * @param size an optional size of the array
 */
fun Table.textArray(name: String, size: Int? = null): Column<Array<String>> =
    array(name, currentDialect.dataTypeProvider.textType(), size, null, null)

/**
 * Creates a text array column with [name] and mapping to enum.
 *
 * @param size an optional size of the array
 */
fun <T : Enum<*>> Table.enumArray(name: String, size: Int? = null, toDb: ((T) -> Any), fromDb: ((Any) -> T)): Column<Array<T>> =
    array(name, currentDialect.dataTypeProvider.textType(), size, toDb, fromDb)

private fun <T : Serializable> Table.array(
    name: String,
    underlyingType: String,
    size: Int?,
    toDb: ((T) -> Any)?,
    fromDb: ((Any) -> T)?
) =
    registerColumn<Array<T>>(name, ArrayColumnType(underlyingType, size, toDb, fromDb))

/**
 * Checks whether this string is in the [other] expression.
 *
 * Example:
 * ```kotlin
 * productService.find { "tag" eqAny ProductsTable.tags }
 * ```
 *
 * @see any
 */
public infix fun String.equalsAny(other: Expression<Array<String>>): EqOp = stringLiteral(this) eqAny other

/**
 * Invokes the `ANY` function on [expression].
 */
public fun <T : Serializable> any(
    expression: Expression<Array<T>>,
): ExpressionWithColumnType<String?> = CustomStringFunction("ANY", expression)

private infix fun <T : Serializable> Expression<T>.eqAny(other: Expression<Array<T>>): EqOp = EqOp(this, any(other))

/**
 * Implementation of [ColumnType] for the SQL `ARRAY` type.
 *
 * @property underlyingType the type of the array
 * @property size an optional size of the array
 */
@Suppress("UNCHECKED_CAST")
public class ArrayColumnType<T : Serializable>(
    private val underlyingType: String,
    private val size: Int?,
    private val toDb: ((T) -> Any)?,
    private val fromDb: ((Any) -> T)?
) : ColumnType() {

    override fun sqlType(): String = "$underlyingType ARRAY${size?.let { "[$it]" } ?: ""}"

    override fun notNullValueToDB(value: Any): Any = when (value) {
        is Array<*> -> mapToDb(value)
        is Collection<*> -> mapToDb(value.toTypedArray())
        else -> error("Got unexpected array value of type: ${value::class.qualifiedName} ($value)")
    }

    override fun valueFromDB(value: Any): Any = when (value) {
        is SQLArray -> mapFromDb(value.array as Array<*>)
        is Array<*> -> mapFromDb(value)
        is Collection<*> -> mapFromDb(value.toTypedArray())
        else -> error("Got unexpected array value of type: ${value::class.qualifiedName} ($value)")
    }

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        if (value == null) {
            stmt.setNull(index, this)
        } else {
            val preparedStatement = stmt as? JdbcPreparedStatementImpl ?: error("Currently only JDBC is supported")
            val array = preparedStatement.statement.connection.createArrayOf(underlyingType, value as Array<*>)
            stmt[index] = array
        }
    }

    private fun mapToDb(arr: Array<*>): Array<*> =
        if (toDb != null) {
            val toDb = toDb!!
            arr.map { toDb(it as T) }.toTypedArray()
        } else {
            arr
        }

    private fun mapFromDb(arr: Array<*>): Array<*> =
        if (fromDb != null) {
            val fromDb = fromDb!!
            arr.map { fromDb(it as T) }.toTypedArray<Any>()
        } else {
            arr
        }

}