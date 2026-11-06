package com.almekhlafi.materialbook.ui.viewmodel

import android.content.res.Resources
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almekhlafi.materialbook.R
import com.almekhlafi.materialbook.utils.Script
import com.almekhlafi.materialbook.utils.bundleScripts
import com.almekhlafi.materialbook.utils.FproAssets
import kotlinx.coroutines.launch


class MainViewModel(
    resources: Resources,
    settings: SettingsViewModel
): ViewModel() {

    private val _themeColor = mutableStateOf(Color.Transparent)
    val themeColor: State<Color> = _themeColor

    private val _scripts = mutableStateOf<String?>(null)
    val scripts: State<String?> = _scripts

    init {
        loadScripts(
            resources,
            settings
        )
    }

    fun setThemeColor(color: Color) {
        _themeColor.value = color
    }

    private fun loadScripts(
        resources: Resources,
        settings: SettingsViewModel
    ) {
        val scripts = listOf(
            Script(true, R.raw.scripts, "scripts.js"), // always apply
            Script(settings.messagesDesktop.value, R.raw.messages_tab, "messages_tab.js"),
            Script(settings.removeAds.value, R.raw.adblock, "adblock.js"),
            Script(settings.enableCopyToClipboard.value, R.raw.copy_to_clipboard, "copy_to_clipboard.js"),
            Script(true, R.raw.copy_post_text, "copy_post_text.js"),
            Script(settings.stickyNavbar.value, R.raw.sticky_navbar, "sticky_navbar.js"),
            Script(settings.reelControls.value, R.raw.reel_controls, "reel_controls.js"),
            Script(settings.reelControls.value && settings.reelGestures.value, R.raw.reel_gestures, "reel_gestures.js"),
            Script(settings.reelBackgroundAudio.value, R.raw.reel_bg_audio, "reel_bg_audio.js"),
            Script(settings.autoScrollReels.value, R.raw.autoscroll_reels, "autoscroll_reels.js"),
            // Needs the bars pinned by the sticky navbar script.
            Script(settings.stickyNavbar.value && settings.collapsingToolbar.value, R.raw.collapsing_toolbar, "collapsing_toolbar.js"),
            Script(!settings.pinchToZoom.value, R.raw.pinch_to_zoom, "pinch_to_zoom.js"),
            Script(settings.materialYou.value, R.raw.material_you, "material_you.js"),
            Script(settings.amoledBlack.value, R.raw.amoled_black, "amoled_black.js"),
            Script(settings.hideSuggested.value, R.raw.hide_suggested, "hide_suggested.js"),
            Script(settings.hideReels.value, R.raw.hide_reels, "hide_reels.js"),
            Script(settings.hideStories.value, R.raw.hide_stories, "hide_stories.js"),
            Script(settings.hidePeopleYouMayKnow.value, R.raw.hide_pymk, "hide_pymk.js"),
            Script(settings.hideGroups.value, R.raw.hide_groups, "hide_groups.js"),
            Script(settings.hideAiPosts.value, R.raw.hide_ai_content, "hide_ai_content.js")
            // pip_video_detector.js deliberately NOT included here - it's core
            // PiP infrastructure, not a cosmetic feature, and needs to run as
            // early as possible. Bundling it with the rest means it doesn't
            // start running any sooner than the slowest script in this list,
            // they're all evaluated together as one combined blob. Loaded separately, directly from the bundled
            // resource, in MaterialbookWV.kt instead.
        )

        viewModelScope.launch {
            val cfg = "window.__mbReel={rate:${settings.reelHoldRate.value},skip:${settings.reelSkipSeconds.value},fit:${settings.reelFit.value && settings.reelControls.value}};\n"
            // ─── Our golden fpro engine (ported from WebTurbo Pro v2.18.0) ───
            // Replaces Faceboost's built-in download system: the full stack is
            // facebook.js (rainbow bar, colors, hiding, text selection, floating
            // download button, double-tap fullscreen) + site-engine.js +
            // reference-engines.js + audio-mode.js, driven by the __CONFIG__
            // JSON exactly like the original app's Preferences.scriptConfig().
            val fproConfig = buildString {
                append("{")
                append("\"siteEngine\":\"upstream\",")
                append("\"homeStories\":${settings.fproHomeStories.value},")
                append("\"ads\":${settings.removeAds.value},")
                append("\"suggested\":${settings.hideSuggested.value},")
                append("\"reels\":${settings.hideReels.value},")
                append("\"stories\":${settings.hideStories.value},")
                append("\"people\":${settings.hidePeopleYouMayKnow.value},")
                append("\"groups\":${settings.hideGroups.value},")
                append("\"rainbow\":${settings.fproRainbow.value},")
                append("\"sticky\":${settings.stickyNavbar.value},")
                append("\"reverse\":${settings.fproReverse.value},")
                append("\"downloads\":${settings.fproDownloads.value},")
                append("\"enhance\":${settings.fproEnhance.value},")
                append("\"doubleTapFullscreen\":${settings.fproDoubleTap.value},")
                append("\"textSelection\":${settings.fproTextSelection.value},")
                append("\"referenceEngine\":${settings.fproReferenceEngine.value},")
                append("\"appBanner\":${settings.fproAppBanner.value},")
                append("\"color\":\"${settings.fproColor.value}\",")
                append("\"dynamic\":${settings.fproDynamic.value},")
                append("\"experimentalColorVars\":${settings.fproExperimentalColors.value},")
                append("\"dark\":${settings.fproDark.value || settings.amoledBlack.value},")
                append("\"amoled\":${settings.amoledBlack.value}")
                append("}")
            }
            val fproEngine = runCatching { FproAssets.engineBundle(resources, fproConfig) }
                .getOrElse { "" }
            _scripts.value = cfg +
                bundleScripts(scripts) { resId ->
                    resources.openRawResource(resId).bufferedReader().use { it.readText() }
                } + fproEngine
        }
    }

    fun refresh(
        resources: Resources,
        settings: SettingsViewModel
    ) {
        clearScripts()
        loadScripts(resources, settings)
    }

    private fun clearScripts() {
        _scripts.value = null
    }
}