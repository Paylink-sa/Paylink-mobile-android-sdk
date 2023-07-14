package sa.paylink.sdk.android.plpaymentgateway.model

class PLResult<T, E>(private val value: T?, private val error: E?) {
    fun isSuccess(): Boolean {
        return value != null
    }

    fun isFailure(): Boolean {
        return error != null
    }

    fun getValue(): T? {
        return value
    }

    fun getError(): E? {
        return error
    }

    companion object {
        fun <T, E> success(value: T): PLResult<T, E> {
            return PLResult(value, null)
        }

        fun <T, E> failure(error: E): PLResult<T, E> {
            return PLResult(null, error)
        }
    }
}
