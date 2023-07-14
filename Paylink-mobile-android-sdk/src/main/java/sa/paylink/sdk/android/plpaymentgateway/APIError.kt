package sa.paylink.sdk.android.plpaymentgateway

class APIError(val errorCode: Type, val errorMessage: String) :
    Exception() {

    enum class Type {
        JSON_ERROR, NETWORK_ERROR, INVALID_PARAMETERS
    }
}