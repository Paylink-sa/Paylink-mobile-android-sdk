package sa.paylink.sdk.android.plpaymentgateway

import sa.paylink.sdk.android.plpaymentgateway.model.PLPaylinkCallbackData

interface Callback<T, E> {
    fun onSuccess(response: PLPaylinkCallbackData)
    fun onError(error: E)
}