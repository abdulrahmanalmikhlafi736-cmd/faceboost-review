package com.almekhlafi.materialbook.utils

import android.net.Uri
import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.web.WebViewNavigator

class ExternalRequestInterceptor(
    private val handleExternalUrl: (String) -> Unit,
    private val tryOpenMessenger: (String) -> Boolean = { false },
    private val tryOpenMessagesDesktop: (String) -> Boolean = { false },
    private val onShareLink: (String) -> Unit = {},
    // Our fpro custom scheme (fpro://download / fpro://fullscreen-error) —
    // the golden engine's channel to native, ported from WebTurbo Pro.
    private val onFproUrl: (Uri) -> Unit = {},
) : RequestInterceptor {

    override fun onInterceptUrlRequest(
        request: WebRequest,
        navigator: WebViewNavigator
    ): WebRequestInterceptResult {
        // Sub-resources (images, XHR) must never leave the app or spawn browser tabs.
        if (!request.isForMainFrame) {
            return WebRequestInterceptResult.Allow
        }

        // Our engine's fpro:// navigations: always consumed by the app, never
        // rendered and never sent to an external browser.
        if (request.url.startsWith("fpro://")) {
            onFproUrl(Uri.parse(request.url))
            return WebRequestInterceptResult.Reject
        }

        // "Send as message" in Facebook's share sheet: keep the link and ask where to send it.
        shareLinkOf(request.url)?.let { link ->
            onShareLink(link)
            return WebRequestInterceptResult.Reject
        }
        // Messages/Messenger entry points open in the Messages layer (desktop site in its own
        // WebView) when enabled, so this page stays where it is underneath.
        if (request.isForMainFrame && isMessagesLink(request.url) && tryOpenMessagesDesktop(request.url))
            return WebRequestInterceptResult.Reject

        // Messenger deep links can never render anything useful in-WebView: fire the app,
        // go back so no dead entry stays in history, and Reject the request.
        // (Reject alone leaves the download interstitial behind when backing out of
        // Messenger; Modify to about:blank leaves a stuck black screen instead.)
        if (isMessengerUrl(request.url)) {
            tryOpenMessenger(request.url)
            navigator.navigateBack()
            return WebRequestInterceptResult.Reject
        }

        return if (isWebViewRenderable(request.url)) {
            WebRequestInterceptResult.Allow
        } else {
            handleExternalUrl(fbRedirectSanitizer(request.url))
            WebRequestInterceptResult.Reject
        }
    }
}
