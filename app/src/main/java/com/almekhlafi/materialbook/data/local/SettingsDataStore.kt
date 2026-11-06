package com.almekhlafi.materialbook.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.almekhlafi.materialbook.utils.DEFAULT_MESSENGER_PACKAGE

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "materialbook_prefs")

class SettingsDataStore(private val context: Context) {
    companion object {
        val REMOVE_ADS = booleanPreferencesKey("remove_ads")
        val ENABLE_COPY_TO_CLIPBOARD = booleanPreferencesKey("enable_copy_to_clipboard")
        val DESKTOP_LAYOUT = booleanPreferencesKey("desktop_layout")
        val HIDE_NAV_BAR = booleanPreferencesKey("hide_nav_bar")
        val NOTIFY_POLL = booleanPreferencesKey("notify_poll")
        val IMMERSIVE_MODE = booleanPreferencesKey("immersive_mode")
        val STICKY_NAVBAR = booleanPreferencesKey("sticky_navbar")
        val AUTO_SCROLL_REELS = booleanPreferencesKey("auto_scroll_reels")
        val REEL_BACKGROUND_AUDIO = booleanPreferencesKey("reel_background_audio")
        val AUTO_UPDATE_CHECK = booleanPreferencesKey("auto_update_check")
        val NOTIFICATIONS_ASKED = booleanPreferencesKey("notifications_asked")
        val REEL_CONTROLS = booleanPreferencesKey("reel_controls")
        val REEL_GESTURES = booleanPreferencesKey("reel_gestures")
        val REEL_FIT = booleanPreferencesKey("reel_fit")
        val REEL_HOLD_RATE = floatPreferencesKey("reel_hold_rate")
        val REEL_SKIP_SECONDS = intPreferencesKey("reel_skip_seconds")
        val COLLAPSING_TOOLBAR = booleanPreferencesKey("collapsing_toolbar")
        val PINCH_TO_ZOOM = booleanPreferencesKey("pinch_to_zoom")
        val MATERIAL_YOU = booleanPreferencesKey("material_you")
        val AMOLED_BLACK = booleanPreferencesKey("amoled_black")
        val HIDE_SUGGESTED = booleanPreferencesKey("hide_suggestion")
        val HIDE_REELS = booleanPreferencesKey("hide_reels")
        val HIDE_STORIES = booleanPreferencesKey("hide_stories")
        val HIDE_PEOPLE_YOU_MAY_KNOW = booleanPreferencesKey("hide_people_you_may_know")
        val HIDE_GROUPS = booleanPreferencesKey("hide_groups")
        val HIDE_AI_POSTS = booleanPreferencesKey("hide_ai_posts")
        val APP_LOCK = booleanPreferencesKey("app_lock")
        val TEXT_ZOOM = intPreferencesKey("text_zoom")
        val OPEN_LINKS_IN_TAB = booleanPreferencesKey("open_links_in_tab")
        val KEEP_SCREEN_ON = stringPreferencesKey("keep_screen_on")
        val BRAVE_BLOCK_LIST = booleanPreferencesKey("brave_block_list")
        val MESSAGES_DESKTOP = booleanPreferencesKey("messages_desktop")
        val PIP_ENABLED = booleanPreferencesKey("pip_enabled")
        val PIP_PORTRAIT_RATIO = stringPreferencesKey("pip_portrait_ratio")
        val PIP_LOCKSCREEN_AUDIO = booleanPreferencesKey("pip_lockscreen_audio")

        // Legacy: set by the old auto-desktop logic when it wrote desktop_layout
        // on the user's behalf. Only read by the migration below.
        val LEGACY_REVERT_DESKTOP = booleanPreferencesKey("is_revert_desktop")

        /**
         * If the legacy revert flag is set, desktop_layout was written by the
         * old auto logic, not the user: reset it and drop the flag. A
         * desktop_layout without the flag is the user's own choice and stays.
         */
        fun migrateLegacyAutoDesktop(prefs: MutablePreferences) {
            if (prefs[LEGACY_REVERT_DESKTOP] == true) {
                prefs[DESKTOP_LAYOUT] = false
            }
            prefs.remove(LEGACY_REVERT_DESKTOP)
        }
        val MESSENGER_PACKAGE = stringPreferencesKey("messenger_package")

        // ─── Our golden fpro engine keys (ported from WebTurbo Pro) ───
        // Master switch for page enhancements (fpro "pageEnhancements").
        val FPRO_ENHANCE = booleanPreferencesKey("fpro_enhance")
        // Draggable rainbow progress bar on every video/Reel (2.18 native-weld engine).
        val FPRO_RAINBOW = booleanPreferencesKey("fpro_rainbow")
        // The floating 44px download button over videos.
        val FPRO_DOWNLOADS = booleanPreferencesKey("fpro_downloads")
        // Native Chromium text selection in posts (SlimSocial + AstryxBook port).
        val FPRO_TEXT_SELECTION = booleanPreferencesKey("fpro_text_selection")
        // Double-tap a video to fill the screen.
        val FPRO_DOUBLE_TAP = booleanPreferencesKey("fpro_double_tap")
        // Hide the "فتح التطبيق" open-app banner (purge engine, default ON like fpro).
        val FPRO_APP_BANNER = booleanPreferencesKey("fpro_app_banner")
        // Story tray hidden on the home feed only.
        val FPRO_HOME_STORIES = booleanPreferencesKey("fpro_home_stories")
        // Reverse page direction (body ltr).
        val FPRO_REVERSE = booleanPreferencesKey("fpro_reverse")
        // Dark page via our CSS variables (independent of the app theme).
        val FPRO_DARK = booleanPreferencesKey("fpro_dark")
        // Match the native progress bar tint with the page color.
        val FPRO_LIVE_COLOR = booleanPreferencesKey("fpro_live_color")
        // Extra color variable experiments (--accent, --primary-blue, ...).
        val FPRO_EXPERIMENTAL_COLORS = booleanPreferencesKey("fpro_experimental_colors")
        // Reference color engine (FproColor stylesheet repaint).
        val FPRO_REFERENCE_ENGINE = booleanPreferencesKey("fpro_reference_engine")
        // Materialbook accent from the system when color = تلقائي.
        val FPRO_DYNAMIC = booleanPreferencesKey("fpro_dynamic")
        // Facebook accent color name (تلقائي/طبيعي/ذهبي/أحمر/...).
        val FPRO_COLOR = stringPreferencesKey("fpro_color")
        // External downloader package for our download handoff (Seal default).
        val FPRO_PACKAGE = stringPreferencesKey("fpro_package")
        // User-agent compatibility (Chrome non-wv UA, real engine major kept).
        val FPRO_BROWSER_AGENT = booleanPreferencesKey("fpro_browser_agent")
        // Third-party cookies for embedded content (login continuity).
        val FPRO_THIRD_PARTY_COOKIES = booleanPreferencesKey("fpro_third_party_cookies")
    }

    val prefs = context.dataStore.data

    suspend fun migrateLegacyAutoDesktop() {
        if (prefs.first().contains(LEGACY_REVERT_DESKTOP)) {
            context.dataStore.edit { migrateLegacyAutoDesktop(it) }
        }
    }

    val removeAds = context.dataStore.data.map { it[REMOVE_ADS] ?: true }
    suspend fun setRemoveAds(removeAds: Boolean) {
        context.dataStore.edit { it[REMOVE_ADS] = removeAds }
    }

    val enableCopyToClipboard = context.dataStore.data.map { it[ENABLE_COPY_TO_CLIPBOARD] ?: true }
    suspend fun setEnableCopyToClipboard(enableCopyToClipboard: Boolean) {
        context.dataStore.edit { it[ENABLE_COPY_TO_CLIPBOARD] = enableCopyToClipboard }
    }

    val desktopLayout = context.dataStore.data.map { it[DESKTOP_LAYOUT] }
    suspend fun setDesktopLayout(desktopLayout: Boolean) {
        context.dataStore.edit { it[DESKTOP_LAYOUT] = desktopLayout }
    }

    val hideNavBar = context.dataStore.data.map { it[HIDE_NAV_BAR] ?: false }
    suspend fun setHideNavBar(hideNavBar: Boolean) {
        context.dataStore.edit { it[HIDE_NAV_BAR] = hideNavBar }
    }

    val notifyPoll = context.dataStore.data.map { it[NOTIFY_POLL] ?: false }
    suspend fun setNotifyPoll(notifyPoll: Boolean) {
        context.dataStore.edit { it[NOTIFY_POLL] = notifyPoll }
    }

    val immersiveMode = context.dataStore.data.map { it[IMMERSIVE_MODE] ?: false}
    suspend fun setImmersiveMode(immersiveMode: Boolean) {
        context.dataStore.edit { it[IMMERSIVE_MODE] = immersiveMode }
    }

    val stickyNavbar = context.dataStore.data.map { it[STICKY_NAVBAR] ?: true }
    suspend fun setStickyNavbar(stickyNavbar: Boolean) {
        context.dataStore.edit { it[STICKY_NAVBAR] = stickyNavbar }
    }

    val autoScrollReels = context.dataStore.data.map { it[AUTO_SCROLL_REELS] ?: false }
    suspend fun setAutoScrollReels(autoScrollReels: Boolean) {
        context.dataStore.edit { it[AUTO_SCROLL_REELS] = autoScrollReels }
    }

    val reelBackgroundAudio = context.dataStore.data.map { it[REEL_BACKGROUND_AUDIO] ?: false }
    suspend fun setReelBackgroundAudio(reelBackgroundAudio: Boolean) {
        context.dataStore.edit { it[REEL_BACKGROUND_AUDIO] = reelBackgroundAudio }
    }

    val autoUpdateCheck = context.dataStore.data.map { it[AUTO_UPDATE_CHECK] ?: true }
    suspend fun setAutoUpdateCheck(autoUpdateCheck: Boolean) {
        context.dataStore.edit { it[AUTO_UPDATE_CHECK] = autoUpdateCheck }
    }

    /** True the first time it is called, false afterwards: the permission prompt is asked once. */
    suspend fun takeFirstNotificationAsk(): Boolean {
        var first = false
        context.dataStore.edit {
            first = it[NOTIFICATIONS_ASKED] != true
            it[NOTIFICATIONS_ASKED] = true
        }
        return first
    }

    val reelControls = context.dataStore.data.map { it[REEL_CONTROLS] ?: true }
    suspend fun setReelControls(reelControls: Boolean) {
        context.dataStore.edit { it[REEL_CONTROLS] = reelControls }
    }

    val reelGestures = context.dataStore.data.map { it[REEL_GESTURES] ?: false }
    suspend fun setReelGestures(v: Boolean) { context.dataStore.edit { it[REEL_GESTURES] = v } }
    val reelFit = context.dataStore.data.map { it[REEL_FIT] ?: false }
    suspend fun setReelFit(v: Boolean) { context.dataStore.edit { it[REEL_FIT] = v } }
    val reelHoldRate = context.dataStore.data.map { it[REEL_HOLD_RATE] ?: 2f }
    suspend fun setReelHoldRate(v: Float) { context.dataStore.edit { it[REEL_HOLD_RATE] = v } }
    val reelSkipSeconds = context.dataStore.data.map { it[REEL_SKIP_SECONDS] ?: 10 }
    suspend fun setReelSkipSeconds(v: Int) { context.dataStore.edit { it[REEL_SKIP_SECONDS] = v } }

    // Page text size in percent (85..150, 100 = as Facebook draws it).
    val textZoom = context.dataStore.data.map { it[TEXT_ZOOM] ?: 100 }
    suspend fun setTextZoom(v: Int) { context.dataStore.edit { it[TEXT_ZOOM] = v } }
    val appLock = context.dataStore.data.map { it[APP_LOCK] ?: false }
    suspend fun setAppLock(v: Boolean) { context.dataStore.edit { it[APP_LOCK] = v } }
    val openLinksInTab = context.dataStore.data.map { it[OPEN_LINKS_IN_TAB] ?: false }
    suspend fun setOpenLinksInTab(v: Boolean) { context.dataStore.edit { it[OPEN_LINKS_IN_TAB] = v } }
    // "off", "video" (while a video plays) or "always" (while the app is on screen)
    val keepScreenOn = context.dataStore.data.map { it[KEEP_SCREEN_ON] ?: "off" }
    suspend fun setKeepScreenOn(v: String) { context.dataStore.edit { it[KEEP_SCREEN_ON] = v } }

    suspend fun exportSettings(): String = exportSettingsJson(context.dataStore.data.first())
    suspend fun importSettings(json: String): Int {
        var applied = 0
        context.dataStore.edit { applied = importSettingsJson(json, it) }
        return applied
    }

    val collapsingToolbar = context.dataStore.data.map { it[COLLAPSING_TOOLBAR] ?: true }
    suspend fun setCollapsingToolbar(collapsingToolbar: Boolean) {
        context.dataStore.edit { it[COLLAPSING_TOOLBAR] = collapsingToolbar }
    }

    val pinchToZoom = context.dataStore.data.map { it[PINCH_TO_ZOOM] ?: false }
    suspend fun setPinchToZoom(pinchToZoom: Boolean) {
        context.dataStore.edit { it[PINCH_TO_ZOOM] = pinchToZoom }
    }

    val materialYou = context.dataStore.data.map { it[MATERIAL_YOU] ?: false }
    suspend fun setMaterialYou(materialYou: Boolean) {
        context.dataStore.edit { it[MATERIAL_YOU] = materialYou }
    }

    val amoledBlack = context.dataStore.data.map { it[AMOLED_BLACK] ?: false }
    suspend fun setAmoledBlack(amoledBlack: Boolean) {
        context.dataStore.edit { it[AMOLED_BLACK] = amoledBlack }
    }

    val hideAiPosts = context.dataStore.data.map { it[HIDE_AI_POSTS] ?: false }
    suspend fun setHideAiPosts(hideAiPosts: Boolean) {
        context.dataStore.edit { it[HIDE_AI_POSTS] = hideAiPosts }
    }

    val hideSuggested = context.dataStore.data.map { it[HIDE_SUGGESTED] ?: false }
    suspend fun setHideSuggested(hideSuggestion: Boolean) {
        context.dataStore.edit { it[HIDE_SUGGESTED] = hideSuggestion }
    }

    val hideReels = context.dataStore.data.map { it[HIDE_REELS] ?: false }
    suspend fun setHideReels(hideReels: Boolean) {
        context.dataStore.edit { it[HIDE_REELS] = hideReels }
    }

    val hideStories = context.dataStore.data.map { it[HIDE_STORIES] ?: false }
    suspend fun setHideStories(hideStories: Boolean) {
        context.dataStore.edit { it[HIDE_STORIES] = hideStories }
    }

    val hidePeopleYouMayKnow = context.dataStore.data.map { it[HIDE_PEOPLE_YOU_MAY_KNOW] ?: false }
    suspend fun setHidePeopleYouMayKnow(hidePeopleYouMayKnow: Boolean) {
        context.dataStore.edit { it[HIDE_PEOPLE_YOU_MAY_KNOW] = hidePeopleYouMayKnow }
    }

    val messagesDesktop = context.dataStore.data.map { it[MESSAGES_DESKTOP] ?: true }
    suspend fun setMessagesDesktop(messagesDesktop: Boolean) {
        context.dataStore.edit { it[MESSAGES_DESKTOP] = messagesDesktop }
    }

    val hideGroups = context.dataStore.data.map { it[HIDE_GROUPS] ?: false }
    suspend fun setHideGroups(hideGroups: Boolean) {
        context.dataStore.edit { it[HIDE_GROUPS] = hideGroups }
    }

    // Off by default: entering a floating window unexpectedly is surprising
    // behavior, same reasoning as materialYou/amoledBlack defaulting off.
    val pipEnabled = context.dataStore.data.map { it[PIP_ENABLED] ?: false }
    suspend fun setPipEnabled(pipEnabled: Boolean) {
        context.dataStore.edit { it[PIP_ENABLED] = pipEnabled }
    }

    // Off by default, like PiP itself: opt-in to keep playing audio natively
    // (with a media notification) after the screen locks during PiP.
    val pipLockscreenAudio = context.dataStore.data.map { it[PIP_LOCKSCREEN_AUDIO] ?: false }
    suspend fun setPipLockscreenAudio(enabled: Boolean) {
        context.dataStore.edit { it[PIP_LOCKSCREEN_AUDIO] = enabled }
    }

    // Default "4:7" is the empirically safe portrait ratio on Samsung A56 (and similar OEMs)
    // where requesting true 9:16 causes the PiP window to overflow past the screen edge.
    val pipPortraitRatio = context.dataStore.data.map { it[PIP_PORTRAIT_RATIO] ?: "4:7" }
    suspend fun setPipPortraitRatio(ratio: String) {
        context.dataStore.edit { it[PIP_PORTRAIT_RATIO] = ratio }
    }

    val braveBlockList = context.dataStore.data.map { it[BRAVE_BLOCK_LIST] ?: true }
    suspend fun setBraveBlockList(braveBlockList: Boolean) {
        context.dataStore.edit { it[BRAVE_BLOCK_LIST] = braveBlockList }
    }

    val messengerPackage = context.dataStore.data.map { it[MESSENGER_PACKAGE] ?: DEFAULT_MESSENGER_PACKAGE }
    suspend fun setMessengerPackage(messengerPackage: String) {
        context.dataStore.edit { it[MESSENGER_PACKAGE] = messengerPackage }
    }

    // ─── Our golden fpro engine settings ───
    val fproEnhance = context.dataStore.data.map { it[FPRO_ENHANCE] ?: true }
    suspend fun setFproEnhance(v: Boolean) { context.dataStore.edit { it[FPRO_ENHANCE] = v } }

    val fproRainbow = context.dataStore.data.map { it[FPRO_RAINBOW] ?: true }
    suspend fun setFproRainbow(v: Boolean) { context.dataStore.edit { it[FPRO_RAINBOW] = v } }

    val fproDownloads = context.dataStore.data.map { it[FPRO_DOWNLOADS] ?: true }
    suspend fun setFproDownloads(v: Boolean) { context.dataStore.edit { it[FPRO_DOWNLOADS] = v } }

    val fproTextSelection = context.dataStore.data.map { it[FPRO_TEXT_SELECTION] ?: true }
    suspend fun setFproTextSelection(v: Boolean) { context.dataStore.edit { it[FPRO_TEXT_SELECTION] = v } }

    val fproDoubleTap = context.dataStore.data.map { it[FPRO_DOUBLE_TAP] ?: true }
    suspend fun setFproDoubleTap(v: Boolean) { context.dataStore.edit { it[FPRO_DOUBLE_TAP] = v } }

    val fproAppBanner = context.dataStore.data.map { it[FPRO_APP_BANNER] ?: true }
    suspend fun setFproAppBanner(v: Boolean) { context.dataStore.edit { it[FPRO_APP_BANNER] = v } }

    val fproHomeStories = context.dataStore.data.map { it[FPRO_HOME_STORIES] ?: false }
    suspend fun setFproHomeStories(v: Boolean) { context.dataStore.edit { it[FPRO_HOME_STORIES] = v } }

    val fproReverse = context.dataStore.data.map { it[FPRO_REVERSE] ?: false }
    suspend fun setFproReverse(v: Boolean) { context.dataStore.edit { it[FPRO_REVERSE] = v } }

    val fproDark = context.dataStore.data.map { it[FPRO_DARK] ?: false }
    suspend fun setFproDark(v: Boolean) { context.dataStore.edit { it[FPRO_DARK] = v } }

    val fproLiveColor = context.dataStore.data.map { it[FPRO_LIVE_COLOR] ?: false }
    suspend fun setFproLiveColor(v: Boolean) { context.dataStore.edit { it[FPRO_LIVE_COLOR] = v } }

    val fproExperimentalColors = context.dataStore.data.map { it[FPRO_EXPERIMENTAL_COLORS] ?: false }
    suspend fun setFproExperimentalColors(v: Boolean) { context.dataStore.edit { it[FPRO_EXPERIMENTAL_COLORS] = v } }

    val fproReferenceEngine = context.dataStore.data.map { it[FPRO_REFERENCE_ENGINE] ?: true }
    suspend fun setFproReferenceEngine(v: Boolean) { context.dataStore.edit { it[FPRO_REFERENCE_ENGINE] = v } }

    val fproDynamic = context.dataStore.data.map { it[FPRO_DYNAMIC] ?: false }
    suspend fun setFproDynamic(v: Boolean) { context.dataStore.edit { it[FPRO_DYNAMIC] = v } }

    val fproColor = context.dataStore.data.map { it[FPRO_COLOR] ?: "تلقائي" }
    suspend fun setFproColor(v: String) { context.dataStore.edit { it[FPRO_COLOR] = v } }

    val fproPackage = context.dataStore.data.map { it[FPRO_PACKAGE] ?: "com.junkfood.seal" }
    suspend fun setFproPackage(v: String) { context.dataStore.edit { it[FPRO_PACKAGE] = v } }

    val fproBrowserAgent = context.dataStore.data.map { it[FPRO_BROWSER_AGENT] ?: false }
    suspend fun setFproBrowserAgent(v: Boolean) { context.dataStore.edit { it[FPRO_BROWSER_AGENT] = v } }

    // ON by default, exactly like our fpro app: embedded cookies keep logins alive.
    val fproThirdPartyCookies = context.dataStore.data.map { it[FPRO_THIRD_PARTY_COOKIES] ?: true }
    suspend fun setFproThirdPartyCookies(v: Boolean) { context.dataStore.edit { it[FPRO_THIRD_PARTY_COOKIES] = v } }
}