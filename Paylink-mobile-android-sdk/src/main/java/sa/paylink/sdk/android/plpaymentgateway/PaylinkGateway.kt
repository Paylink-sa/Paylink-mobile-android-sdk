package sa.paylink.sdk.android.plpaymentgateway

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import sa.paylink.sdk.android.plpaymentgateway.model.PLOrder
import sa.paylink.sdk.android.plpaymentgateway.model.PLPaylinkCallbackData
import sa.paylink.sdk.android.plpaymentgateway.model.PLProduct

class PaylinkGateway : Callback<PLPaylinkCallbackData, APIError> {

    private val apiId: String?
    private val secretKey: String?
    private val environment: Environment
    private val context: Context?
    private val paymentFormUrl: String?
    private var platform: String?

    constructor() {
        this.apiId = ""
        this.secretKey = ""
        this.environment = Environment.PRODUCTION
        this.context = null
        this.paymentFormUrl = ""
        this.platform = ""
    }

    constructor(paymentFormUrl: String = "", platform: String = "") {
        this.apiId = "";
        this.secretKey = ""
        this.environment = Environment.PRODUCTION
        this.context = null
        this.paymentFormUrl = paymentFormUrl
        this.platform = platform
    }

    constructor(environment: Environment, paymentFormUrl: String = "", platform: String = "") {
        this.apiId = "";
        this.secretKey = ""
        this.environment = environment
        this.context = null
        this.paymentFormUrl = paymentFormUrl
        this.platform = platform
    }

    constructor(environment: Environment) {
        this.apiId = "";
        this.secretKey = ""
        this.environment = environment
        this.context = null
        this.paymentFormUrl = ""
        this.platform = ""
    }

    // Not recommended
    constructor(apiId: String, secretKey: String, environment: Environment, context: Context) {
        this.apiId = apiId;
        this.secretKey = secretKey
        this.environment = environment
        this.context = context
        this.paymentFormUrl = ""
        this.platform = ""
    }

    companion object {
        var openInvoiceUrlCallback: Callback<PLPaylinkCallbackData, APIError>? = null
    }

    private fun getBaseUrl(): String {
        return when (environment) {
            Environment.TEST -> "https://restpilot.paylink.sa"
            Environment.DEV -> "https://paylinkapi.eu.ngrok.io"
            else -> "https://restapi.paylink.sa"
        }
    }

    private fun getPayBaseUrl(): String {
        return when (environment) {
            Environment.TEST -> "https://paymentpilot.paylink.sa"
            Environment.DEV -> "https://paylinkpay.eu.ngrok.io"
            else -> "https://payment.paylink.sa"
        }
    }

    override fun onSuccess(response: PLPaylinkCallbackData) {
        openInvoiceUrlCallback?.onSuccess(response)
    }

    override fun onError(error: APIError) {
        openInvoiceUrlCallback?.onError(error)
    }

    @JvmName(name = "openPaymentForm")
    fun openPaymentForm(
        transactionNo: String,
        context: Context,
        callback: Callback<PLPaylinkCallbackData, APIError>
    ) {
        openPaymentForm(
            transactionNo, context,
            width = null, lang = "en", bgColor = null, topSpace = null, textColor = null, buttonTextColor = null,
            mainColor = null, callback
        )
    }

    @JvmName(name = "openPaymentForm")
    fun openPaymentForm(
        transactionNo: String,
        context: Context,
        width: Int?, lang: String? = "en",
        bgColor: String?, topSpace: Int?, textColor: String?,
        buttonTextColor: String?, mainColor: String?,
        callback: Callback<PLPaylinkCallbackData, APIError>
    ) {

        // Implementation for openInvoiceURL method
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val screenWidth: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            windowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }

        val w = width ?: (screenWidth - 30)

        var u = paymentFormUrl ?: "${getPayBaseUrl()}/pay/info/$transactionNo"

        if (platform.isNullOrEmpty()) {
            platform = "ios"
        }
        u += if (paymentFormUrl.isNullOrEmpty()) {
            "${getPayBaseUrl()}/pay/frame/$transactionNo?lang=$lang&platform=${platform}&transactionNo=$transactionNo&w=$w&pc=$bgColor&ts=$topSpace&tc=$textColor&bt=$buttonTextColor&mc=$mainColor"
        } else {
            "?platform=${platform}&transactionNo=$transactionNo"
        }

        openInvoiceUrlCallback = callback

        println("URL: $u");
        val intent = Intent(context, PLInvoiceWebviewActivity::class.java)
        intent.putExtra(PLInvoiceWebviewActivity.EXTRA_INVOICE_URL, u)
        intent.putExtra(PLInvoiceWebviewActivity.EXTRA_CALLBACK_CLASS, this::class.java)
        context.startActivity(intent)
    }

//    @JvmName(name = "auth")
//    @Deprecated("This function is deprecated. This method should be called from server side", ReplaceWith("newAuth(completion)"))
//    fun auth(completion: Callback<String, APIError>) {
//        // Implementation for auth method, Build the URL for the auth endpoint
//        val urlString = getBaseUrl() + "/api/auth"
//
//        // Create a JSON object with the API credentials
//        val json = JSONObject().apply {
//            put("apiId", apiId)
//            put("secretKey", secretKey)
//            put("persistToken", false)
//        }
//
//        val request = object : JsonObjectRequest(
//            Method.POST,
//            urlString,
//            json,
//            Response.Listener { response ->
//                val idToken = response.getString("id_token")
//                completion.onSuccess(idToken)
//            },
//            Response.ErrorListener { error ->
//                completion.onError(APIError(APIError.Type.JSON_ERROR, "Error paring auth response. " + error.message))
//            }) {
//            override fun getHeaders(): MutableMap<String, String> {
//                return HashMap<String, String>().apply { put("Content-Type", "application/json") }
//            }
//        }
//
//        if (context == null) {
//            completion.onError(APIError(APIError.Type.INVALID_PARAMETERS, "Context is null"))
//        } else {
//            // Add the request to the queue to be executed
//            Volley.newRequestQueue(context).add(request)
//        }
//    }

//    @JvmName("addInvoice")
//    @Deprecated("This function is deprecated. Adding invoice should be done in the server side.")
//    fun addInvoice(
//        idToken: String, customerName: String, customerMobile: String, amount: Double, orderNumber: String, products: List<PLProduct>, completion: Callback<PLOrder, APIError>
//    ) {
//        addInvoice(idToken, customerName, customerMobile, null, amount, null, null, orderNumber, products, completion)
//    }

//    @Deprecated("This function is deprecated. Adding invoice should be done in the server side.")
//    @JvmName("addInvoice")
//    fun addInvoice(
//        idToken: String, customerName: String, customerMobile: String, customerEmail: String?, amount: Double, currency: String?,
//        note: String?, orderNumber: String, products: List<PLProduct>, completion: Callback<PLOrder, APIError>
//    ) {
//        // Implementation for addInvoice method
//
//        // Check if required parameters are not null
//        if (customerName.isEmpty() || customerMobile.isEmpty() || amount <= 0) {
//            completion.onError(
//                APIError(
//                    APIError.Type.INVALID_PARAMETERS,
//                    "Invalid or missing parameters"
//                )
//            )
//            return
//        }
//
//        val currencyValue = currency ?: "SAR"
//        val productsValue = products.takeIf { it.isNotEmpty() } ?: emptyList()
//
//        // Implementation for auth method
//        val urlString = getBaseUrl() + "/api/addInvoice"
//
//        // Create a JSON object with the API credentials
//        val json = JSONObject().apply {
//            put("amount", amount)
//            put("callBackUrl", "https://paylink.sa?s=plpaymentgateway")
//            put("clientName", customerName)
//            put("orderNumber", orderNumber)
//            put("note", note)
//            put("clientEmail", customerEmail)
//            put("clientMobile", customerMobile)
//            put("currency", currencyValue)
//
//            val productsArray = JSONArray()
//            productsValue.forEach { product ->
//                val productObject = JSONObject()
//                productObject.put("title", product.title)
//                productObject.put("price", product.price)
//                productObject.put("imageSrc", null)
//                productObject.put("qty", product.qty)
//                productsArray.put(productObject)
//            }
//            put("products", productsArray)
//        }
//
//        val request = object : JsonObjectRequest(Method.POST, urlString, json,
//            Response.Listener { response ->
//                // Extract the id_token from the response
//                val order = Gson().fromJson(response.toString(), PLOrder::class.java)
//                completion.onSuccess(order)
//            }, Response.ErrorListener { error ->
//                completion.onError(APIError(APIError.Type.JSON_ERROR, "Error adding new invoice. " + error.message))
//            }) {
//            override fun getHeaders(): MutableMap<String, String> {
//                return HashMap<String, String>().apply {
//                    put("Content-Type", "application/json")
//                    put("Authorization", "Bearer $idToken")
//                }
//            }
//        }
//
//        if (context == null) {
//            completion.onError(APIError(APIError.Type.INVALID_PARAMETERS, "Context is null"))
//        } else {
//            // Add the request to the queue to be executed
//            Volley.newRequestQueue(context).add(request)
//        }
//    }

//    @Deprecated("This function is deprecated.", ReplaceWith("openPaymentForm(completion)"))
//    @JvmName(name = "openInvoiceURL")
//    fun openInvoiceURL(
//        context: Context, url: String,
//        clientName: String, clientMobile: String,
//        callback: Callback<PLPaylinkCallbackData, APIError>
//    ) {
//        openInvoiceURL(context, url, clientName, clientMobile, "en", -1, "FFF", 25, "000", "FFF", "3087de", callback)
//    }

//    @Deprecated("This function is deprecated.", ReplaceWith("openPaymentForm(completion)"))
//    @JvmName(name = "openInvoiceURL")
//    fun openInvoiceURL(
//        context: Context, url: String,
//        clientName: String, clientMobile: String, lang: String?,
//        width: Int?, bgColor: String?, topSpace: Int?, textColor: String?,
//        buttonTextColor: String?, mainColor: String?,
//        callback: Callback<PLPaylinkCallbackData, APIError>
//    ) {
//        // Implementation for openInvoiceURL method
//        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//
//        val screenWidth: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            val windowMetrics = windowManager.currentWindowMetrics
//            windowMetrics.bounds.width()
//        } else {
//            val displayMetrics = DisplayMetrics()
//            windowManager.defaultDisplay.getMetrics(displayMetrics)
//            displayMetrics.widthPixels
//        }
//
//        val w = width ?: (screenWidth - 30)
//        val u = "$url?lang=$lang&w=$w&pc=$bgColor&n=$clientName&m=$clientMobile&ts=$topSpace&tc=$textColor&bt=$buttonTextColor&mc=$mainColor&lang=$lang"
//
//        openInvoiceUrlCallback = callback
//
//        val intent = Intent(context, PLInvoiceWebviewActivity::class.java)
//        intent.putExtra(PLInvoiceWebviewActivity.EXTRA_INVOICE_URL, u)
//        intent.putExtra(PLInvoiceWebviewActivity.EXTRA_CALLBACK_CLASS, this::class.java)
//        context.startActivity(intent)
//    }
}