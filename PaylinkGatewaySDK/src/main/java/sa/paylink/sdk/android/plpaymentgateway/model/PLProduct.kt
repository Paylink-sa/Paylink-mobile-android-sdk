package sa.paylink.sdk.android.plpaymentgateway.model

import java.io.Serializable

class PLProduct : Serializable {
    var title: String
    var price: Double
    var qty: Int
    var description: String?
    var isDigital: Any
    var imageSrc: String?
    var specificVat: Double?
    var productCost: Double?

    constructor(
        title: String,
        price: Double,
        qty: Int,
        description: String?,
        isDigital: Any,
        imageSrc: String?,
        specificVat: Double?,
        productCost: Double?
    ) {
        this.title = title
        this.price = price
        this.qty = qty
        this.description = description
        this.isDigital = isDigital
        this.imageSrc = imageSrc
        this.specificVat = specificVat
        this.productCost = productCost
    }

    constructor(title: String, price: Double, qty: Int) {
        this.title = title
        this.price = price
        this.qty = qty
        description = null
        isDigital = false
        imageSrc = null
        specificVat = null
        productCost = null
    }
}