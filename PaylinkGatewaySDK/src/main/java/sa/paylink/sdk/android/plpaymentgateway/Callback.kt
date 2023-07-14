package sa.paylink.sdk.android.plpaymentgateway

interface Callback<T, E> {
    fun onSuccess(response: T)
    fun onError(error: E)
}