package sa.paylink.sdk.android.plpaymentgateway.model

import java.io.Serializable
import java.util.ArrayList

class PLGatewayOrderRequest : Serializable {
    var amount = 0.0
    var orderNumber: String? = null
    var callBackUrl: String? = null
    var clientEmail: String? = null
    var clientName: String? = null
    var clientMobile: String? = null
    var note: String? = null
    var cancelUrl: Any? = null
    var products: ArrayList<PLProduct>? = null
    var currency: String? = null
    var smsMessage: String? = null
}