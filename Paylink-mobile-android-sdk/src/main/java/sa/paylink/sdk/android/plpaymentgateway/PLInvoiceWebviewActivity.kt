package sa.paylink.sdk.android.plpaymentgateway

import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.google.common.base.Splitter
import sa.paylink.sdk.android.plpaymentgateway.model.PLPaylinkCallbackData

class PLInvoiceWebviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_INVOICE_URL = "URL"
        const val EXTRA_CALLBACK_CLASS = "CALLBACK_CLASS"
        private const val CLOSE_URL = "plpaymentgateway"
    }

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_webview)

        webView = findViewById(R.id.webView)

        if (intent?.hasExtra(EXTRA_INVOICE_URL) == true && intent.hasExtra(EXTRA_CALLBACK_CLASS)) {
            val url = intent.getStringExtra(EXTRA_INVOICE_URL)
            webView.webViewClient = InvoiceWebViewClient()
            webView.settings.javaScriptEnabled = true
            url?.let { webView.loadUrl(it) }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.clearCache(true)
        webView.clearHistory()
    }

    inner class InvoiceWebChromeClient : WebChromeClient() {
        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            // Implementation for handling JavaScript alerts
            return false
        }

        override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
            // Implementation for handling JavaScript prompts
            return false
        }
    }

    inner class InvoiceWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, urlString: String?) {
            super.onPageFinished(view, urlString)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

            val currentUrl = request!!.url.toString()

            // Do something with the URL
            if (currentUrl.contains(CLOSE_URL)) {
                val query = currentUrl.split("\\?")[1]
                val map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query)

                val transactionNo = map["transactionNo"]
                val orderNumber = map["orderNumber"]

                if (intent?.hasExtra(EXTRA_CALLBACK_CLASS) == true) {
                    val callbackClass = intent.getSerializableExtra(EXTRA_CALLBACK_CLASS) as Class<*>

                    try {
                        val constructor = callbackClass.getConstructor()
                        val callbackInstance = constructor.newInstance()
                        val completion = callbackInstance as Callback<PLPaylinkCallbackData, APIError>
                        // Invoke the completion block with the result
                        completion.onSuccess(PLPaylinkCallbackData(orderNumber ?: "", transactionNo ?: ""))
                        finish()
                        return true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return true
                        // Handle any errors that occur while creating or invoking the callback instance
                    }
                }
            }

            // Load the URL in the WebView
            view?.loadUrl(currentUrl)
            return false
        }
    }
}
