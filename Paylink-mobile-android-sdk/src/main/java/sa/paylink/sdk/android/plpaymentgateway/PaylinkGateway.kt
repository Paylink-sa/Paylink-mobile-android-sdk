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

class PaylinkGateway(
    private val apiId: String?,
    private val secretKey: String?,
    private val environment: Environment,
    private val context: Context?
) : Callback<PLPaylinkCallbackData, APIError> {

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

    @JvmName(name = "auth")
    @Deprecated(message = "Use the auth from the server side")
    fun auth(completion: Callback<String, APIError>) {
        // Implementation for auth method, Build the URL for the auth endpoint
        val urlString = getBaseUrl() + "/api/auth"

        // Create a JSON object with the API credentials
        val json = JSONObject().apply {
            put("apiId", apiId)
            put("secretKey", secretKey)
            put("persistToken", false)
        }

        val request = object : JsonObjectRequest(
            Method.POST,
            urlString,
            json,
            Response.Listener { response ->
                val idToken = response.getString("id_token")
                completion.onSuccess(idToken)
            },
            Response.ErrorListener { error ->
                completion.onError(APIError(APIError.Type.JSON_ERROR, "Error paring auth response. " + error.message))
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply { put("Content-Type", "application/json") }
            }
        }

        if (context == null) {
            completion.onError(APIError(APIError.Type.INVALID_PARAMETERS, "Context is null"))
        } else {
            // Add the request to the queue to be executed
            Volley.newRequestQueue(context).add(request)
        }
    }

    fun submitInvoice() {}

    @JvmName("addInvoice")
    @Deprecated(message = "use AddInvoice from the server side, get the transactionNo and pass it here to method submitInvoice", replaceWith = ReplaceWith("submitInvoice"))
    fun addInvoice(
        idToken: String, customerName: String, customerMobile: String, amount: Double, orderNumber: String, products: List<PLProduct>, completion: Callback<PLOrder, APIError>
    ) {
        addInvoice(idToken, customerName, customerMobile, null, amount, null, null, orderNumber, products, completion)
    }

    @JvmName("addInvoice")
    @Deprecated(message = "use AddInvoice from the server side, get the transactionNo and pass it here to method submitInvoice", replaceWith = ReplaceWith("submitInvoice"))
    fun addInvoice(
        idToken: String, customerName: String, customerMobile: String, customerEmail: String?, amount: Double, currency: String?,
        note: String?, orderNumber: String, products: List<PLProduct>, completion: Callback<PLOrder, APIError>
    ) {
        // Implementation for addInvoice method

        // Check if required parameters are not null
        if (customerName.isEmpty() || customerMobile.isEmpty() || amount <= 0) {
            completion.onError(
                APIError(
                    APIError.Type.INVALID_PARAMETERS,
                    "Invalid or missing parameters"
                )
            )
            return
        }

        val currencyValue = currency ?: "SAR"
        val productsValue = products.takeIf { it.isNotEmpty() } ?: emptyList()

        // Implementation for auth method
        val urlString = getBaseUrl() + "/api/addInvoice"

        // Create a JSON object with the API credentials
        val json = JSONObject().apply {
            put("amount", amount)
            put("callBackUrl", "https://paylink.sa?s=plpaymentgateway")
            put("clientName", customerName)
            put("orderNumber", orderNumber)
            put("note", note)
            put("clientEmail", customerEmail)
            put("clientMobile", customerMobile)
            put("currency", currencyValue)

            val productsArray = JSONArray()
            productsValue.forEach { product ->
                val productObject = JSONObject()
                productObject.put("title", product.title)
                productObject.put("price", product.price)
                productObject.put("imageSrc", null)
                productObject.put("qty", product.qty)
                productsArray.put(productObject)
            }
            put("products", productsArray)
        }

        val request = object : JsonObjectRequest(Method.POST, urlString, json,
            Response.Listener { response ->
                // Extract the id_token from the response
                val order = Gson().fromJson(response.toString(), PLOrder::class.java)
                completion.onSuccess(order)
            }, Response.ErrorListener { error ->
                completion.onError(APIError(APIError.Type.JSON_ERROR, "Error adding new invoice. " + error.message))
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put("Content-Type", "application/json")
                    put("Authorization", "Bearer $idToken")
                }
            }
        }

        if (context == null) {
            completion.onError(APIError(APIError.Type.INVALID_PARAMETERS, "Context is null"))
        } else {
            // Add the request to the queue to be executed
            Volley.newRequestQueue(context).add(request)
        }
    }

    @JvmName(name = "openInvoiceURL")
    fun openInvoiceURL(
        context: Context, url: String,
        clientName: String, clientMobile: String,
        callback: Callback<PLPaylinkCallbackData, APIError>
    ) {
        openInvoiceURL(context, url, clientName, clientMobile, "en", -1, "FFF", 25, "000", "FFF", "3087de", callback)
    }

    @JvmName(name = "openInvoiceURL")
    fun openInvoiceURL(
        context: Context, url: String,
        clientName: String, clientMobile: String, lang: String?,
        width: Int?, bgColor: String?, topSpace: Int?, textColor: String?,
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
        val u = "$url?lang=$lang&w=$w&pc=$bgColor&n=$clientName&m=$clientMobile&ts=$topSpace&tc=$textColor&bt=$buttonTextColor&mc=$mainColor&lang=$lang"

        openInvoiceUrlCallback = callback

        val intent = Intent(context, PLInvoiceWebviewActivity::class.java)
        intent.putExtra(PLInvoiceWebviewActivity.EXTRA_INVOICE_URL, u)
        intent.putExtra(PLInvoiceWebviewActivity.EXTRA_CALLBACK_CLASS, this::class.java)
        context.startActivity(intent)
    }

    override fun onSuccess(response: PLPaylinkCallbackData) {
        openInvoiceUrlCallback?.onSuccess(response)
    }

    override fun onError(error: APIError) {
        openInvoiceUrlCallback?.onError(error)
    }
}