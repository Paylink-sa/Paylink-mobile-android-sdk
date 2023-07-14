package sa.paylink.sdk.android.plpaymentgateway.model

import java.io.Serializable

class PLPaylinkCallbackData(private var orderNumber: String, private var transactionNo: String) :
    Serializable {

    override fun toString(): String {
        return "PLPaylinkCallbackData{" +
                "orderNumber='" + orderNumber + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                '}'
    }
}