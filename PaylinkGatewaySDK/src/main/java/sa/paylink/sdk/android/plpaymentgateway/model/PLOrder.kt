package sa.paylink.sdk.android.plpaymentgateway.model
class PLOrder     constructor () : java.io.Serializable{
    var gatewayOrderRequest: PLGatewayOrderRequest? = null
    var amount: kotlin.Double = 0.0
    var transactionNo: kotlin.String? = null
    var orderStatus: kotlin.String? = null
    var paymentErrors: kotlin.Any? = null
    var url: kotlin.String? = null
    var qrUrl: kotlin.String? = null
    var mobileUrl: kotlin.String? = null
    var checkUrl: kotlin.String? = null
    val isSuccess: kotlin.Boolean = false
    val isDigitalOrder: kotlin.Boolean = false
    var foreignCurrencyRate: kotlin.Any? = null
}