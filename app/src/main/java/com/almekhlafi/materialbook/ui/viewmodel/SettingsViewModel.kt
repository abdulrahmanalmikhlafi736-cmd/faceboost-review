package com.almekhlafi.materialbook.ui.viewmodel

import android.app.Application
import android.util.Rational
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.almekhlafi.materialbook.data.local.SettingsDataStore
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.AMOLED_BLACK
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.DESKTOP_LAYOUT
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.ENABLE_COPY_TO_CLIPBOARD
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.BRAVE_BLOCK_LIST
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.HIDE_GROUPS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.MESSAGES_DESKTOP
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.HIDE_PEOPLE_YOU_MAY_KNOW
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.HIDE_REELS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.HIDE_STORIES
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.HIDE_AI_POSTS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.HIDE_SUGGESTED
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.IMMERSIVE_MODE
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.NOTIFY_POLL
import com.almekhlafi.materialbook.utils.scheduleNotificationPoll
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.HIDE_NAV_BAR
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.MATERIAL_YOU
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.PINCH_TO_ZOOM
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.PIP_ENABLED
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.PIP_LOCKSCREEN_AUDIO
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.PIP_PORTRAIT_RATIO
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.REMOVE_ADS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.AUTO_SCROLL_REELS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.COLLAPSING_TOOLBAR
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.REEL_BACKGROUND_AUDIO
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.REEL_CONTROLS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.REEL_GESTURES
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.KEEP_SCREEN_ON
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.APP_LOCK
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.OPEN_LINKS_IN_TAB
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.TEXT_ZOOM
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.REEL_FIT
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.REEL_HOLD_RATE
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.REEL_SKIP_SECONDS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.AUTO_UPDATE_CHECK
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.STICKY_NAVBAR
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.MESSENGER_PACKAGE
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_ENHANCE
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_RAINBOW
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_DOWNLOADS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_TEXT_SELECTION
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_DOUBLE_TAP
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_APP_BANNER
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_HOME_STORIES
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_REVERSE
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_DARK
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_LIVE_COLOR
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_EXPERIMENTAL_COLORS
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_REFERENCE_ENGINE
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_DYNAMIC
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_COLOR
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_PACKAGE
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_BROWSER_AGENT
import com.almekhlafi.materialbook.data.local.SettingsDataStore.Companion.FPRO_THIRD_PARTY_COOKIES
import com.almekhlafi.materialbook.utils.DEFAULT_MESSENGER_PACKAGE
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val dataStore: SettingsDataStore = SettingsDataStore(application)

    // Migrate before the first read so desktopLayout's initial value already
    // reflects the unstuck state on installs hit by the old auto-desktop logic.
    private val initialPrefs = runBlocking {
        dataStore.migrateLegacyAutoDesktop()
        dataStore.prefs.first()
    }

    val removeAds = dataStore.removeAds.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[REMOVE_ADS] ?: true,
        started = SharingStarted.WhileSubscribed()
    )
    val enableCopyToClipboard = dataStore.enableCopyToClipboard.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[ENABLE_COPY_TO_CLIPBOARD] ?: true,
        started = SharingStarted.WhileSubscribed()
    )
    val desktopLayout = dataStore.desktopLayout.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[DESKTOP_LAYOUT],
        started = SharingStarted.WhileSubscribed()
    )
    val hideNavBar = dataStore.hideNavBar.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[HIDE_NAV_BAR] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val notifyPoll = dataStore.notifyPoll.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[NOTIFY_POLL] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val immersiveMode = dataStore.immersiveMode.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[IMMERSIVE_MODE] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val stickyNavbar = dataStore.stickyNavbar.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[STICKY_NAVBAR] ?: true,
        started = SharingStarted.WhileSubscribed()
    )
    val autoScrollReels = dataStore.autoScrollReels.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[AUTO_SCROLL_REELS] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val reelBackgroundAudio = dataStore.reelBackgroundAudio.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[REEL_BACKGROUND_AUDIO] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val autoUpdateCheck = dataStore.autoUpdateCheck.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[AUTO_UPDATE_CHECK] ?: true,
        started = SharingStarted.WhileSubscribed()
    )
    val reelControls = dataStore.reelControls.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[REEL_CONTROLS] ?: true,
        started = SharingStarted.WhileSubscribed()
    )
    val reelGestures = dataStore.reelGestures.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[REEL_GESTURES] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val reelFit = dataStore.reelFit.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[REEL_FIT] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val textZoom = dataStore.textZoom.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[TEXT_ZOOM] ?: 100,
        started = SharingStarted.Eagerly
    )
    val appLock = dataStore.appLock.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[APP_LOCK] ?: false,
        started = SharingStarted.Eagerly
    )
    val openLinksInTab = dataStore.openLinksInTab.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[OPEN_LINKS_IN_TAB] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val keepScreenOn = dataStore.keepScreenOn.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[KEEP_SCREEN_ON] ?: "off",
        started = SharingStarted.Eagerly
    )
    val reelHoldRate = dataStore.reelHoldRate.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[REEL_HOLD_RATE] ?: 2f,
        started = SharingStarted.WhileSubscribed()
    )
    val reelSkipSeconds = dataStore.reelSkipSeconds.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[REEL_SKIP_SECONDS] ?: 10,
        started = SharingStarted.WhileSubscribed()
    )
    val collapsingToolbar = dataStore.collapsingToolbar.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[COLLAPSING_TOOLBAR] ?: true,
        started = SharingStarted.WhileSubscribed()
    )
    val pinchToZoom = dataStore.pinchToZoom.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[PINCH_TO_ZOOM] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val materialYou = dataStore.materialYou.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[MATERIAL_YOU] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val amoledBlack = dataStore.amoledBlack.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[AMOLED_BLACK] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val hideAiPosts = dataStore.hideAiPosts.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[HIDE_AI_POSTS] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val hideSuggested = dataStore.hideSuggested.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[HIDE_SUGGESTED] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val hideReels = dataStore.hideReels.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[HIDE_REELS] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val hideStories = dataStore.hideStories.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[HIDE_STORIES] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val hidePeopleYouMayKnow = dataStore.hidePeopleYouMayKnow.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[HIDE_PEOPLE_YOU_MAY_KNOW] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val messagesDesktop = dataStore.messagesDesktop.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[MESSAGES_DESKTOP] ?: true,
        started = SharingStarted.WhileSubscribed()
    )
    val hideGroups = dataStore.hideGroups.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[HIDE_GROUPS] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val braveBlockList = dataStore.braveBlockList.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[BRAVE_BLOCK_LIST] ?: true,
        started = SharingStarted.WhileSubscribed()
    )
    val messengerPackage = dataStore.messengerPackage.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[MESSENGER_PACKAGE] ?: DEFAULT_MESSENGER_PACKAGE,
        started = SharingStarted.WhileSubscribed()
    )
    val pipEnabled = dataStore.pipEnabled.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[PIP_ENABLED] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val pipLockscreenAudio = dataStore.pipLockscreenAudio.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[PIP_LOCKSCREEN_AUDIO] ?: false,
        started = SharingStarted.WhileSubscribed()
    )
    val pipPortraitRatio = dataStore.pipPortraitRatio.stateIn(
        scope = viewModelScope,
        initialValue = initialPrefs[PIP_PORTRAIT_RATIO] ?: "4:7",
        started = SharingStarted.WhileSubscribed()
    )

    fun setRemoveAds(removeAds: Boolean) {
        viewModelScope.launch {
            dataStore.setRemoveAds(removeAds)
        }
    }

    fun setEnableCopyToClipboard(enableCopyToClipboard: Boolean) {
        viewModelScope.launch {
            dataStore.setEnableCopyToClipboard(enableCopyToClipboard)
        }
    }

    fun setDesktopLayout(desktopLayout: Boolean) {
        viewModelScope.launch {
            dataStore.setDesktopLayout(desktopLayout)
        }
    }

    fun setHideNavBar(hideNavBar: Boolean) {
        viewModelScope.launch {
            dataStore.setHideNavBar(hideNavBar)
        }
    }

    fun setNotifyPoll(notifyPoll: Boolean) {
        viewModelScope.launch {
            dataStore.setNotifyPoll(notifyPoll)
            scheduleNotificationPoll(getApplication(), notifyPoll)
        }
    }

    fun setImmersiveMode(immersiveMode: Boolean) {
        viewModelScope.launch {
            dataStore.setImmersiveMode(immersiveMode)
        }
    }

    fun setAutoScrollReels(autoScrollReels: Boolean) {
        viewModelScope.launch {
            dataStore.setAutoScrollReels(autoScrollReels)
        }
    }

    fun setReelBackgroundAudio(reelBackgroundAudio: Boolean) {
        viewModelScope.launch {
            dataStore.setReelBackgroundAudio(reelBackgroundAudio)
        }
    }

    fun setAutoUpdateCheck(autoUpdateCheck: Boolean) {
        viewModelScope.launch {
            dataStore.setAutoUpdateCheck(autoUpdateCheck)
        }
    }

    suspend fun takeFirstNotificationAsk() = dataStore.takeFirstNotificationAsk()

    fun setReelControls(reelControls: Boolean) {
        viewModelScope.launch {
            dataStore.setReelControls(reelControls)
        }
    }

    fun setReelGestures(v: Boolean) { viewModelScope.launch { dataStore.setReelGestures(v) } }
    fun setAppLock(v: Boolean) { viewModelScope.launch { dataStore.setAppLock(v) } }
    fun setTextZoom(v: Int) { viewModelScope.launch { dataStore.setTextZoom(v) } }
    fun setOpenLinksInTab(v: Boolean) { viewModelScope.launch { dataStore.setOpenLinksInTab(v) } }
    fun setKeepScreenOn(v: String) { viewModelScope.launch { dataStore.setKeepScreenOn(v) } }
    suspend fun exportSettings(): String = dataStore.exportSettings()
    /** Returns the number of settings applied, or null when the text isn't a Faceboost settings file. */
    suspend fun importSettings(json: String): Int? =
        runCatching { dataStore.importSettings(json) }.getOrNull()

    fun setReelFit(v: Boolean) { viewModelScope.launch { dataStore.setReelFit(v) } }
    fun setReelHoldRate(v: Float) { viewModelScope.launch { dataStore.setReelHoldRate(v) } }
    fun setReelSkipSeconds(v: Int) { viewModelScope.launch { dataStore.setReelSkipSeconds(v) } }

    fun setCollapsingToolbar(collapsingToolbar: Boolean) {
        viewModelScope.launch {
            dataStore.setCollapsingToolbar(collapsingToolbar)
        }
    }

    fun setStickyNavbar(stickyNavbar: Boolean) {
        viewModelScope.launch {
            dataStore.setStickyNavbar(stickyNavbar)
        }
    }

    fun setPinchToZoom(pinchToZoom: Boolean) {
        viewModelScope.launch {
            dataStore.setPinchToZoom(pinchToZoom)
        }
    }

    fun setMaterialYou(materialYou: Boolean) {
        viewModelScope.launch {
            dataStore.setMaterialYou(materialYou)
        }
    }

    fun setAmoledBlack(amoledBlack: Boolean) {
        viewModelScope.launch {
            dataStore.setAmoledBlack(amoledBlack)
        }
    }

    fun setHideAiPosts(hideAiPosts: Boolean) {
        viewModelScope.launch {
            dataStore.setHideAiPosts(hideAiPosts)
        }
    }

    fun setHideSuggested(hideSuggested: Boolean) {
        viewModelScope.launch {
            dataStore.setHideSuggested(hideSuggested)
        }
    }

    fun setHideReels(hideReels: Boolean) {
        viewModelScope.launch {
            dataStore.setHideReels(hideReels)
        }
    }

    fun setHideStories(hideStories: Boolean) {
        viewModelScope.launch {
            dataStore.setHideStories(hideStories)
        }
    }

    fun setHidePeopleYouMayKnow(hidePeopleYouMayKnow: Boolean) {
        viewModelScope.launch {
            dataStore.setHidePeopleYouMayKnow(hidePeopleYouMayKnow)
        }
    }

    fun setMessagesDesktop(messagesDesktop: Boolean) {
        viewModelScope.launch {
            dataStore.setMessagesDesktop(messagesDesktop)
        }
    }

    fun setHideGroups(hideGroups: Boolean) {
        viewModelScope.launch {
            dataStore.setHideGroups(hideGroups)
        }
    }

    fun setMessengerPackage(messengerPackage: String) {
        viewModelScope.launch {
            dataStore.setMessengerPackage(messengerPackage.ifBlank { DEFAULT_MESSENGER_PACKAGE })
        }
    }

    // ─── Our golden fpro engine ───
    val fproEnhance = dataStore.fproEnhance.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_ENHANCE] ?: true, started = SharingStarted.WhileSubscribed())
    val fproRainbow = dataStore.fproRainbow.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_RAINBOW] ?: true, started = SharingStarted.WhileSubscribed())
    val fproDownloads = dataStore.fproDownloads.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_DOWNLOADS] ?: true, started = SharingStarted.WhileSubscribed())
    val fproTextSelection = dataStore.fproTextSelection.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_TEXT_SELECTION] ?: true, started = SharingStarted.WhileSubscribed())
    val fproDoubleTap = dataStore.fproDoubleTap.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_DOUBLE_TAP] ?: true, started = SharingStarted.WhileSubscribed())
    val fproAppBanner = dataStore.fproAppBanner.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_APP_BANNER] ?: true, started = SharingStarted.WhileSubscribed())
    val fproHomeStories = dataStore.fproHomeStories.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_HOME_STORIES] ?: false, started = SharingStarted.WhileSubscribed())
    val fproReverse = dataStore.fproReverse.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_REVERSE] ?: false, started = SharingStarted.WhileSubscribed())
    val fproDark = dataStore.fproDark.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_DARK] ?: false, started = SharingStarted.WhileSubscribed())
    val fproLiveColor = dataStore.fproLiveColor.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_LIVE_COLOR] ?: false, started = SharingStarted.WhileSubscribed())
    val fproExperimentalColors = dataStore.fproExperimentalColors.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_EXPERIMENTAL_COLORS] ?: false, started = SharingStarted.WhileSubscribed())
    val fproReferenceEngine = dataStore.fproReferenceEngine.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_REFERENCE_ENGINE] ?: true, started = SharingStarted.WhileSubscribed())
    val fproDynamic = dataStore.fproDynamic.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_DYNAMIC] ?: false, started = SharingStarted.WhileSubscribed())
    val fproColor = dataStore.fproColor.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_COLOR] ?: "تلقائي", started = SharingStarted.WhileSubscribed())
    val fproPackage = dataStore.fproPackage.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_PACKAGE] ?: "com.junkfood.seal", started = SharingStarted.WhileSubscribed())
    val fproBrowserAgent = dataStore.fproBrowserAgent.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_BROWSER_AGENT] ?: false, started = SharingStarted.WhileSubscribed())
    val fproThirdPartyCookies = dataStore.fproThirdPartyCookies.stateIn(scope = viewModelScope, initialValue = initialPrefs[FPRO_THIRD_PARTY_COOKIES] ?: true, started = SharingStarted.WhileSubscribed())

    fun setFproEnhance(v: Boolean) { viewModelScope.launch { dataStore.setFproEnhance(v) } }
    fun setFproRainbow(v: Boolean) { viewModelScope.launch { dataStore.setFproRainbow(v) } }
    fun setFproDownloads(v: Boolean) { viewModelScope.launch { dataStore.setFproDownloads(v) } }
    fun setFproTextSelection(v: Boolean) { viewModelScope.launch { dataStore.setFproTextSelection(v) } }
    fun setFproDoubleTap(v: Boolean) { viewModelScope.launch { dataStore.setFproDoubleTap(v) } }
    fun setFproAppBanner(v: Boolean) { viewModelScope.launch { dataStore.setFproAppBanner(v) } }
    fun setFproHomeStories(v: Boolean) { viewModelScope.launch { dataStore.setFproHomeStories(v) } }
    fun setFproReverse(v: Boolean) { viewModelScope.launch { dataStore.setFproReverse(v) } }
    fun setFproDark(v: Boolean) { viewModelScope.launch { dataStore.setFproDark(v) } }
    fun setFproLiveColor(v: Boolean) { viewModelScope.launch { dataStore.setFproLiveColor(v) } }
    fun setFproExperimentalColors(v: Boolean) { viewModelScope.launch { dataStore.setFproExperimentalColors(v) } }
    fun setFproReferenceEngine(v: Boolean) { viewModelScope.launch { dataStore.setFproReferenceEngine(v) } }
    fun setFproDynamic(v: Boolean) { viewModelScope.launch { dataStore.setFproDynamic(v) } }
    fun setFproColor(v: String) { viewModelScope.launch { dataStore.setFproColor(v) } }
    fun setFproPackage(v: String) { viewModelScope.launch { dataStore.setFproPackage(v) } }
    fun setFproBrowserAgent(v: Boolean) { viewModelScope.launch { dataStore.setFproBrowserAgent(v) } }
    fun setFproThirdPartyCookies(v: Boolean) { viewModelScope.launch { dataStore.setFproThirdPartyCookies(v) } }

    fun setBraveBlockList(braveBlockList: Boolean) {
        viewModelScope.launch {
            dataStore.setBraveBlockList(braveBlockList)
        }
    }

    fun setPipEnabled(pipEnabled: Boolean) {
        viewModelScope.launch {
            dataStore.setPipEnabled(pipEnabled)
        }
    }

    fun setPipLockscreenAudio(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setPipLockscreenAudio(enabled)
        }
    }

    fun setPipPortraitRatio(ratio: String) {
        viewModelScope.launch {
            dataStore.setPipPortraitRatio(ratio)
        }
    }

    fun parsedPipRational(): Rational = parsedPipRational(pipPortraitRatio.value)

    fun pipRationalForVideo(videoWidth: Int, videoHeight: Int): Rational =
        calculatePipRational(videoWidth, videoHeight, parsedPipRational())

    companion object {
        private val MIN_PIP_ASPECT_RATIO = 100f / 239f
        private val MAX_PIP_ASPECT_RATIO = 239f / 100f

        /**
         * Converts a stored "W:H" string to a [Rational] suitable for
         * [android.app.PictureInPictureParams.Builder.setAspectRatio].
         * Falls back to Rational(4, 7) on any parse error so a corrupt pref
         * can never crash the app.
         */
        fun parsedPipRational(stored: String): Rational {
            return try {
                val parts = stored.split(":")
                Rational(parts[0].trim().toInt(), parts[1].trim().toInt())
            } catch (_: Exception) {
                Rational(4, 7)
            }
        }

        /**
         * Uses the selected ratio for portrait video and the detected source
         * ratio for landscape video. Extreme landscape ratios are clamped to
         * Android's documented PiP range; unknown dimensions use 16:9.
         */
        fun calculatePipRational(
            videoWidth: Int,
            videoHeight: Int,
            portraitRatio: Rational,
        ): Rational {
            if (videoWidth <= 0 || videoHeight <= 0) {
                return Rational(16, 9)
            }
            if (videoHeight > videoWidth) {
                return portraitRatio
            }

            val sourceRatio = videoWidth.toFloat() / videoHeight.toFloat()
            return when {
                sourceRatio < MIN_PIP_ASPECT_RATIO -> Rational(100, 239)
                sourceRatio > MAX_PIP_ASPECT_RATIO -> Rational(239, 100)
                else -> reducedRational(videoWidth, videoHeight)
            }
        }

        private fun reducedRational(numerator: Int, denominator: Int): Rational {
            var a = numerator
            var b = denominator
            while (b != 0) {
                val remainder = a % b
                a = b
                b = remainder
            }
            return Rational(numerator / a, denominator / a)
        }
    }
}