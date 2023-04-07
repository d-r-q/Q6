package q6.platform.kotlin

fun <T> Result<T>.throwIt(): Nothing {
    checkNotNull(this.exceptionOrNull())
    throw this.exceptionOrNull()!!
}

inline fun <T> Result<T>.ifFailure(pred: (Throwable) -> Boolean, body: (Throwable) -> Nothing): Result<T> {
    return if (this.isFailure && pred(this.exceptionOrNull()!!)) {
        body(this.exceptionOrNull()!!)
    } else {
        this
    }
}