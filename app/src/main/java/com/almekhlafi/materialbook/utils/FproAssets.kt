package com.almekhlafi.materialbook.utils

import android.content.res.Resources

/**
 * FPRO engine assets — ported VERBATIM from our WebTurbo Pro app (v2.18.0)
 * into Faceboost. These are the golden engine's purge payloads (open-app
 * banner / "فتح التطبيق" hiding) plus the asset loader for the full fpro
 * script stack (site-engine.js, reference-engines.js, audio-mode.js,
 * facebook.js with the __CONFIG__ placeholder).
 */
object FproAssets {

    val PURGE_APP_UPSELL_CSS = """
        (function() {
            var existing = document.getElementById('webturbo-purge-upsell-style');
            if (existing) return;
            var style = document.createElement('style');
            style.id = 'webturbo-purge-upsell-style';
            style.innerHTML = `
                /* Specific Facebook upsell banners and "فتح التطبيق" buttons */
                #m-top-of-feed-upsell-banner,
                [data-sigil*="m-upsell-banner"],
                [data-sigil*="watch_and_scroll_upsell"],
                [data-sigil*="upsell"],
                [data-sigil*="app_link"],
                [data-testid*="open_app" i],
                [data-testid*="open-app" i],
                [data-sigil~="m-app-banner"],
                [data-sigil~="app-prompt"],
                .m-app-banner,
                .fb-app-banner,
                .app-download-banner,
                .mobile-app-banner,
                .fixed-app-banner,
                .video-app-upsell,
                div[class*="video_upsell"],
                div[class*="watch_upsell"],
                div[class*="native_banner"],
                /* Play Store and Intent links */
                a[href*="intent://"],
                a[href*="market://"],
                a[href*="play.google.com/store/apps/details?id=com.facebook"],
                a[href*="/mobile/download/"],
                a[href*="/get_app/"],
                [aria-label*="فتح التطبيق"],
                [aria-label*="Open App"],
                [aria-label*="استخدام التطبيق"],
                [aria-label*="شاهد في التطبيق"] {
                    display: none !important;
                    visibility: hidden !important;
                    height: 0px !important;
                    max-height: 0px !important;
                    margin: 0px !important;
                    padding: 0px !important;
                    overflow: hidden !important;
                    opacity: 0 !important;
                    pointer-events: none !important;
                }
            `;
            if (document.head) {
                document.head.appendChild(style);
            } else if (document.documentElement) {
                document.documentElement.appendChild(style);
            }
        })();
    """.trimIndent()

    val PURGE_APP_UPSELL_JS = """
        (function() {
            if (window._webturboUpsellPurged) return;
            window._webturboUpsellPurged = true;

            var bannedWords = [
                'فتح التطبيق',
                'استخدام التطبيق',
                'احصل على فيسبوك',
                'فتح في التطبيق',
                'تنزيل التطبيق',
                'شاهد في التطبيق',
                'تثبيت التطبيق',
                'Open App',
                'Open the App',
                'Use App',
                'Watch in App',
                'Get Facebook',
                'Install App'
            ];

            function scanAndDestroyUpsells() {
                // 1. Target known banner and intent link selectors
                var bannerSelectors = [
                    '#m-top-of-feed-upsell-banner',
                    '[data-sigil*="m-upsell-banner"]',
                    '[data-sigil*="watch_and_scroll_upsell"]',
                    '[data-sigil*="upsell"]',
                    '[data-sigil*="app_link"]',
                    '[data-testid*="open_app" i]',
                    '[data-testid*="open-app" i]',
                    '[data-sigil~="m-app-banner"]',
                    '[data-sigil~="app-prompt"]',
                    'a[href*="intent://"]',
                    'a[href*="market://"]',
                    'a[href*="play.google.com/store/apps/details?id=com.facebook"]',
                    '.m-app-banner',
                    '.fb-app-banner',
                    '.app-download-banner',
                    '.mobile-app-banner',
                    '.video-app-upsell'
                ];

                bannerSelectors.forEach(function(sel) {
                    try {
                        var elements = document.querySelectorAll(sel);
                        elements.forEach(function(el) {
                            if (el && el.id !== 'root' && el.id !== 'viewport' && el.tagName !== 'BODY') {
                                el.style.setProperty('display', 'none', 'important');
                                el.style.setProperty('height', '0px', 'important');
                                el.style.setProperty('visibility', 'hidden', 'important');
                            }
                        });
                    } catch(e) {}
                });

                // 2. Scan buttons and links matching "فتح التطبيق" / "Open App"
                try {
                    var candidates = document.querySelectorAll('a, button, div[role="button"], span, div');
                    candidates.forEach(function(el) {
                        var text = (el.innerText || '').trim();
                        if (!text || text.length > 50) return;

                        for (var i = 0; i < bannedWords.length; i++) {
                            if (text === bannedWords[i]) {
                                var btn = el.closest('a') || el.closest('button') || el.closest('[role="button"]') || el;
                                if (btn && btn.id !== 'root' && btn.id !== 'viewport' && btn.tagName !== 'BODY' && btn.tagName !== 'HTML') {
                                    btn.style.setProperty('display', 'none', 'important');
                                    btn.style.setProperty('visibility', 'hidden', 'important');
                                    btn.style.setProperty('height', '0px', 'important');
                                    btn.style.setProperty('padding', '0px', 'important');
                                    btn.style.setProperty('margin', '0px', 'important');

                                    // If button is inside an isolated wrapper container, hide the wrapper too
                                    var parent = btn.parentElement;
                                    if (parent && parent.children.length === 1 && parent.id !== 'root' && parent.id !== 'viewport' && parent.tagName !== 'BODY') {
                                        parent.style.setProperty('display', 'none', 'important');
                                        parent.style.setProperty('height', '0px', 'important');
                                    }
                                }
                                break;
                            }
                        }
                    });
                } catch(e) {}
            }

            scanAndDestroyUpsells();

            if (window.MutationObserver && document.body) {
                var observer = new MutationObserver(function() {
                    scanAndDestroyUpsells();
                });
                observer.observe(document.body, { childList: true, subtree: true });
            }
        })();
    """.trimIndent()

    /** Reads a bundled asset file (app/src/main/assets/<name>). */
    fun readAsset(resources: Resources, name: String): String =
        resources.assets.open(name).bufferedReader().use { it.readText() }

    /**
     * The full fpro engine bundle for a Facebook page:
     * purge CSS + purge JS + site-engine + reference-engines + audio-mode +
     * facebook.js with __CONFIG__ replaced by the live JSON config.
     * Guarded so it only executes on facebook.com / messenger.com pages,
     * exactly like the original app's Navigation.isFacebook gate.
     */
    fun engineBundle(resources: Resources, configJson: String): String {
        val facebookJs = readAsset(resources, "facebook.js").replace("__CONFIG__", configJson)
        val parts = listOf(
            PURGE_APP_UPSELL_CSS,
            PURGE_APP_UPSELL_JS,
            readAsset(resources, "site-engine.js"),
            readAsset(resources, "reference-engines.js"),
            readAsset(resources, "audio-mode.js"),
            facebookJs
        )
        // Host gate: our engine only ever ran on Facebook/Messenger origins
        // in the original app (Navigation.isFacebook); keep that contract.
        return "if(/(^|\\.)facebook\\.com$|(^|\\.)messenger\\.com$|(^|\\.)fb\\.com$/i.test((location&&location.hostname)||'')){\n" +
            parts.joinToString("\n") +
            "\n}\n"
    }

    /** Native viewport payload the engine expects (window.__fproViewport). */
    fun viewportScript(widthPx: Int, heightPx: Int): String =
        "window.__fproViewport={width:$widthPx,height:$heightPx};" +
            "window.dispatchEvent(new Event('fproviewportchange'));"
}
