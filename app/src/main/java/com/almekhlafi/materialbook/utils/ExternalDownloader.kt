package com.almekhlafi.materialbook.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * FPRO external download handoff — ported VERBATIM from our WebTurbo Pro
 * app (v2.18.0) into Faceboost, replacing Faceboost's built-in downloader.
 *
 * A package-targeted handoff: no internal downloader and no chooser. The
 * receiving app (e.g. Seal) owns its window. We send an available media URL,
 * otherwise let the downloader parse the Facebook permalink. Never exports
 * browser cookies or login credentials.
 */
object FproNavigation {
    // Prefer the live media source, never a stale SPA entry permalink.
    fun downloadSource(media: String, permalink: String): String? {
        val direct = try { java.net.URI(media).let { it.scheme == "https" && !it.host.isNullOrBlank() } } catch (_: Exception) { false }
        if (direct) return media
        return if (isFacebook(permalink) && Regex("/stories/[^/]+/[^/?]+|/story\\.php\\?|/reels?/|/videos/|/watch(?:/|\\?)|/video\\.php|[?&](?:story_fbid|fbid|v)=").containsMatchIn(permalink)) permalink else null
    }

    fun browserAgent(agent: String): String {
        val major = Regex("Chrome/(\\d+)").find(agent)?.groupValues?.get(1)
        if (major == null || !agent.startsWith("Mozilla/")) return agent.replace("; wv", "").replace(" Version/4.0", "")
        // Chrome's reduced Android UA format; preserve the real engine major.
        return "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/$major.0.0.0 Mobile Safari/537.36"
    }

    fun isFacebook(url: String): Boolean = try {
        val uri = java.net.URI(url)
        val host = uri.host?.lowercase() ?: ""
        uri.scheme == "https" && (host == "facebook.com" || host.endsWith(".facebook.com") || host == "messenger.com" || host.endsWith(".messenger.com"))
    } catch (_: Exception) { false }
}

object ExternalDownloader {

    fun toast(context: Context, message: String) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * The fpro:// custom-scheme router, ported from the original route().
     * Returns true when the scheme was ours (request must be rejected).
     */
    fun route(
        activity: Activity,
        uri: Uri,
        currentPageUrl: String,
        packageName: String,
        openSettings: () -> Unit
    ): Boolean {
        if (uri.scheme != "fpro") return false
        if (FproNavigation.isFacebook(currentPageUrl) && uri.host == "fullscreen-error") {
            toast(activity, "تعذّر فتح ملء الشاشة؛ جرّب زر ملء الشاشة الأصلي للفيديو")
        }
        if (uri.host == "download") {
            // The live page URL travels WITH the request now — the Facebook
            // context check inside uses the page the button was pressed on,
            // never a stale or never-set global (the 2.18.0 bug: lastKnownPageUrl
            // was never remembered, so every download said "open in Facebook").
            sendToDownloader(
                activity,
                uri.getQueryParameter("media").orEmpty(),
                uri.getQueryParameter("link").orEmpty(),
                currentPageUrl,
                packageName,
                openSettings
            )
        }
        return true
    }

    fun sendToDownloader(
        activity: Activity,
        mediaUrl: String,
        linkUrl: String,
        pageUrl: String,
        packageName: String,
        openSettings: () -> Unit
    ) {
        // Prefer the page the button was actually pressed on; the remembered
        // global is only a fallback for callers without a URL at hand.
        val page = pageUrl.ifBlank { currentPage(activity) }
        if (!FproNavigation.isFacebook(page)) { toast(activity, "يلزم فتح الفيديو في فيسبوك"); return }
        val pkg = packageName.trim()
        if (pkg.isBlank()) {
            toast(activity, "حدّد حزمة تطبيق التنزيل أولاً في الإعدادات")
            openSettings()
            return
        }
        if (!Regex("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)+").matches(pkg)) {
            toast(activity, "اسم حزمة تطبيق التنزيل غير صحيح")
            return
        }
        // A package-targeted handoff: no internal downloader and no chooser.
        // The receiving app owns its window; this does not embed its UI here.
        val source = FproNavigation.downloadSource(mediaUrl, linkUrl)
        if (source == null) {
            toast(activity, "لم يتوفر رابط موثوق للفيديو الحالي بعد؛ لن نستخدم رابط الفيديو الأول")
            return
        }
        val intent = Intent(Intent.ACTION_SEND).setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, source).setPackage(pkg)
            .addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION) // The quick dialog is not a Home/PiP request.
        // Seal exports both MainActivity and the floating quick-download UI.
        // Resolve its documented quick Activity explicitly, never its launcher.
        val receiver = if (pkg == "com.junkfood.seal") {
            val quick = ComponentName(pkg, "com.junkfood.seal.QuickDownloadActivity")
            try {
                val info = activity.packageManager.getActivityInfo(quick, 0)
                if (info.exported && info.enabled && (info.applicationInfo?.enabled == true)) quick else null
            } catch (_: android.content.pm.PackageManager.NameNotFoundException) { null }
        } else intent.resolveActivity(activity.packageManager)
        if (receiver == null || receiver.packageName != pkg) {
            toast(activity, "لا توجد واجهة تنزيل محددة لهذه الحزمة؛ يلزم التحقق من التطبيق وطريقة تكامله")
            return
        }
        intent.component = receiver // Never fall through to Android's share chooser.
        try { activity.startActivity(intent) }
        catch (_: ActivityNotFoundException) { toast(activity, "التطبيق المحدد غير مثبت أو لا يدعم استقبال روابط التنزيل") }
        catch (_: SecurityException) { toast(activity, "تطبيق التنزيل لا يسمح بفتح هذه الواجهة") }
    }

    /** Best-effort current page: Faceboost's WebView url when reachable. */
    private var lastKnownPageUrl: String = ""
    fun rememberPage(url: String?) { if (!url.isNullOrBlank()) lastKnownPageUrl = url }
    fun currentPage(activity: Activity): String = lastKnownPageUrl
}
