package com.almekhlafi.materialbook.ui.components.settings

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.BurstMode
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Padding
import androidx.compose.material.icons.filled.Try
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Recommend
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.SwipeVertical
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PanoramaWideAngle
import androidx.compose.material.icons.outlined.PictureInPictureAlt
import androidx.compose.material.icons.outlined.Pinch
import androidx.compose.material.icons.outlined.ScreenLockRotation
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Intent
import android.provider.Settings
import android.os.PowerManager
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.core.net.toUri
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.automirrored.outlined.Logout
import com.almekhlafi.materialbook.utils.isWebViewRenderable
import com.almekhlafi.materialbook.utils.stripTrackingParams
import androidx.compose.material.icons.outlined.Lock
import com.almekhlafi.materialbook.utils.canUseAppLock
import androidx.fragment.app.FragmentActivity
import com.almekhlafi.materialbook.utils.authenticateUser
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.OpenInBrowser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.almekhlafi.materialbook.BuildConfig
import androidx.core.graphics.drawable.toBitmap
import com.almekhlafi.materialbook.R
import com.almekhlafi.materialbook.utils.installedMessengerApps
import androidx.compose.ui.graphics.asImageBitmap
import com.almekhlafi.materialbook.utils.Release
import com.almekhlafi.materialbook.utils.UpdateDialog
import com.almekhlafi.materialbook.utils.checkForUpdate
import com.almekhlafi.materialbook.ui.viewmodel.SettingsViewModel
import com.almekhlafi.materialbook.utils.effectiveDesktop
import com.almekhlafi.materialbook.utils.rememberAutoDesktop
import com.almekhlafi.materialbook.utils.openExternalUrl
import com.almekhlafi.materialbook.utils.openMessenger

@Composable
fun SettingsContent(
    modifier: Modifier,
    viewModel: SettingsViewModel = viewModel(),
    pageUrl: String? = null,
    onLogout: () -> Unit = {},
    onAudioOnly: () -> Unit = {}
) {
    val context = LocalContext.current
    val messengerRedirectToast = stringResource(R.string.messenger_redirect_toast)
    var isLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var isPipRatioDialog by rememberSaveable { mutableStateOf(false) }
    var isMessengerDialog by rememberSaveable { mutableStateOf(false) }
    val messengerApps = remember { installedMessengerApps(context) }

    val removeAds = viewModel.removeAds.collectAsState()
    val braveBlockList = viewModel.braveBlockList.collectAsState()
    // Our golden fpro engine settings.
    val fproEnhance = viewModel.fproEnhance.collectAsState()
    val fproRainbow = viewModel.fproRainbow.collectAsState()
    val fproDownloads = viewModel.fproDownloads.collectAsState()
    val fproTextSelection = viewModel.fproTextSelection.collectAsState()
    val fproDoubleTap = viewModel.fproDoubleTap.collectAsState()
    val fproAppBanner = viewModel.fproAppBanner.collectAsState()
    val fproHomeStories = viewModel.fproHomeStories.collectAsState()
    val fproReverse = viewModel.fproReverse.collectAsState()
    val fproDark = viewModel.fproDark.collectAsState()
    val fproLiveColor = viewModel.fproLiveColor.collectAsState()
    val fproExperimentalColors = viewModel.fproExperimentalColors.collectAsState()
    val fproReferenceEngine = viewModel.fproReferenceEngine.collectAsState()
    val fproDynamic = viewModel.fproDynamic.collectAsState()
    val fproColor = viewModel.fproColor.collectAsState()
    val fproPackage = viewModel.fproPackage.collectAsState()
    val fproBrowserAgent = viewModel.fproBrowserAgent.collectAsState()
    val fproThirdPartyCookies = viewModel.fproThirdPartyCookies.collectAsState()
    var isFproColorDialog by rememberSaveable { mutableStateOf(false) }
    var isFproPackageDialog by rememberSaveable { mutableStateOf(false) }
    val enableCopyToClipboard = viewModel.enableCopyToClipboard.collectAsState()
    val desktopLayout = viewModel.desktopLayout.collectAsState()
    val immersiveMode = viewModel.immersiveMode.collectAsState()
    val notifyPoll = viewModel.notifyPoll.collectAsState()
    val hideNavBar = viewModel.hideNavBar.collectAsState()
    val stickyNavbar = viewModel.stickyNavbar.collectAsState()
    val collapsingToolbar = viewModel.collapsingToolbar.collectAsState()
    val reelControls = viewModel.reelControls.collectAsState()
    val reelGestures = viewModel.reelGestures.collectAsState()
    val reelFit = viewModel.reelFit.collectAsState()
    val reelHoldRate = viewModel.reelHoldRate.collectAsState()
    val reelSkipSeconds = viewModel.reelSkipSeconds.collectAsState()
    val autoUpdateCheck = viewModel.autoUpdateCheck.collectAsState()
    // Re-read when coming back from the system settings screen.
    var notificationsOn by remember { mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled()) }
    var batteryFree by remember { mutableStateOf(isBatteryFree(context)) }
    val owner = LocalActivity.current as? LifecycleOwner
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsOn = NotificationManagerCompat.from(context).areNotificationsEnabled()
                batteryFree = isBatteryFree(context)
            }
        }
        owner?.lifecycle?.addObserver(observer)
        onDispose { owner?.lifecycle?.removeObserver(observer) }
    }
    val scope = rememberCoroutineScope()
    val openLinksInTab = viewModel.openLinksInTab.collectAsState()
    val textZoom = viewModel.textZoom.collectAsState()
    val appLock = viewModel.appLock.collectAsState()
    val appLockUnavailableToast = stringResource(R.string.app_lock_unavailable)
    val appLockVerifyTitle = stringResource(R.string.app_lock_verify)
    val activity = LocalActivity.current
    val keepScreenOn = viewModel.keepScreenOn.collectAsState()
    val settingsSavedToast = stringResource(R.string.settings_exported_toast)
    val settingsBadFileToast = stringResource(R.string.settings_import_bad_file_toast)
    val settingsImportedToast = stringResource(R.string.settings_imported_toast)
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) scope.launch {
            val ok = runCatching {
                val json = viewModel.exportSettings()
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) } ?: error("no stream")
                }
            }.isSuccess
            Toast.makeText(context, if (ok) settingsSavedToast else settingsBadFileToast, Toast.LENGTH_SHORT).show()
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) scope.launch {
            val text = runCatching {
                withContext(Dispatchers.IO) { context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) } }
            }.getOrNull()
            val applied = text?.let { viewModel.importSettings(it) }
            Toast.makeText(context, if (applied != null) settingsImportedToast else settingsBadFileToast, Toast.LENGTH_LONG).show()
        }
    }
    var update by remember { mutableStateOf<Release?>(null) }
    val upToDateToast = stringResource(R.string.update_up_to_date)
    val autoScrollReels = viewModel.autoScrollReels.collectAsState()
    val reelBackgroundAudio = viewModel.reelBackgroundAudio.collectAsState()
    val pinchToZoom = viewModel.pinchToZoom.collectAsState()
    val materialYou = viewModel.materialYou.collectAsState()
    val amoledBlack = viewModel.amoledBlack.collectAsState()
    val pipEnabled = viewModel.pipEnabled.collectAsState()
    val pipLockscreenAudio = viewModel.pipLockscreenAudio.collectAsState()
    val pipPortraitRatio = viewModel.pipPortraitRatio.collectAsState()
    val messengerPackage = viewModel.messengerPackage.collectAsState()
    val messagesDesktop = viewModel.messagesDesktop.collectAsState()

    val isAutoDesktop = rememberAutoDesktop()

    val deviceLabel = run {
        val mfr = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        if (model.startsWith(mfr, ignoreCase = true)) model else "$mfr $model"
    }
    val hideSuggested = viewModel.hideSuggested.collectAsState()
    val hideReels = viewModel.hideReels.collectAsState()
    val hideStories = viewModel.hideStories.collectAsState()
    val hidePeopleYouMayKnow = viewModel.hidePeopleYouMayKnow.collectAsState()
    val hideGroups = viewModel.hideGroups.collectAsState()
    val hideAiPosts = viewModel.hideAiPosts.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsSection(
            title = stringResource(R.string.settings_cat_general),
            icon = Icons.Outlined.Tune,
            items = listOfNotNull(
                SettingsItem(
                    icon = Icons.Outlined.Notifications,
                    title = stringResource(R.string.notifications_title),
                    supportingText = stringResource(
                        if (notificationsOn) R.string.notifications_on else R.string.notifications_off
                    ),
                    isActive = null,
                    onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        )
                    }
                ),
                SettingsItem(
                    icon = Icons.Outlined.Notifications,
                    title = stringResource(R.string.notify_poll_title),
                    supportingText = stringResource(R.string.notify_poll_desc),
                    isActive = notifyPoll.value,
                    onClick = { viewModel.setNotifyPoll(!notifyPoll.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.BatteryChargingFull,
                    title = stringResource(R.string.keep_in_memory_title),
                    supportingText = stringResource(
                        if (batteryFree) R.string.keep_in_memory_on else R.string.keep_in_memory_off
                    ),
                    isActive = null,
                    onClick = {
                        context.startActivity(
                            if (batteryFree) Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                            else Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, "package:${context.packageName}".toUri())
                        )
                    }
                ),
                SettingsItem(
                    icon = Icons.Outlined.LightMode,
                    title = stringResource(R.string.keep_screen_on_title),
                    supportingText = stringResource(
                        when (keepScreenOn.value) {
                            "always" -> R.string.keep_screen_on_always
                            "video" -> R.string.keep_screen_on_video
                            else -> R.string.keep_screen_on_off
                        }
                    ),
                    onClick = {
                        val l = listOf("off", "video", "always")
                        viewModel.setKeepScreenOn(l[(l.indexOf(keepScreenOn.value) + 1) % l.size])
                    }
                ),
                SettingsItem(
                    icon = Icons.Outlined.FormatSize,
                    title = stringResource(R.string.text_size_title),
                    supportingText = stringResource(R.string.text_size_desc, textZoom.value),
                    onClick = {
                        val l = listOf(85, 100, 115, 130, 150)
                        viewModel.setTextZoom(l[(l.indexOf(textZoom.value) + 1) % l.size])
                    }
                ),
                SettingsItem(
                    icon = Icons.Outlined.OpenInBrowser,
                    title = stringResource(R.string.open_links_in_tab_title),
                    supportingText = stringResource(R.string.open_links_in_tab_desc),
                    isActive = openLinksInTab.value,
                    onClick = { viewModel.setOpenLinksInTab(!openLinksInTab.value) }
                ),
                // Android 12+ opens Facebook links in the browser until the user allows them for this app.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) SettingsItem(
                    icon = Icons.Outlined.Link,
                    title = stringResource(R.string.open_links_title),
                    supportingText = stringResource(R.string.open_links_summary),
                    onClick = {
                        runCatching {
                            context.startActivity(
                                Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS, "package:${context.packageName}".toUri())
                            )
                        }
                    }
                ) else null,
                if (pageUrl != null && isWebViewRenderable(pageUrl)) SettingsItem(
                    icon = Icons.Outlined.Share,
                    title = stringResource(R.string.share_page_title),
                    supportingText = stringResource(R.string.share_page_desc),
                    onClick = {
                        // The page on screen as a clean link: www host, no tracking parameters.
                        val link = stripTrackingParams(pageUrl.replaceFirst(Regex("^https://(m|mobile|touch|web|mbasic)\\.facebook\\.com"), "https://www.facebook.com"))
                        context.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, link),
                                null
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                ) else null,
            )
        )

        SettingsSection(
            title = stringResource(R.string.settings_cat_appearance),
            icon = Icons.Outlined.Palette,
            items = listOfNotNull(
                SettingsItem(
                    icon = Icons.Outlined.Palette,
                    title = stringResource(R.string.material_you_title),
                    supportingText = stringResource(R.string.enable_material_you_theming_for_facebook),
                    isActive = materialYou.value,
                    onClick = { viewModel.setMaterialYou(!materialYou.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.Circle,
                    title = stringResource(R.string.amoled_black_title),
                    supportingText = stringResource(R.string.enable_pure_black_theme_for_amoled_displays),
                    isActive = amoledBlack.value,
                    onClick = { viewModel.setAmoledBlack(!amoledBlack.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.DesktopWindows,
                    title = stringResource(R.string.desktop_layout_title),
                    supportingText = stringResource(R.string.force_desktop_layout_may_not_be_suitable_for_smaller_display),
                    // Large screens start on desktop, but it can be turned off.
                    isActive = effectiveDesktop(desktopLayout.value, isAutoDesktop),
                    onClick = { viewModel.setDesktopLayout(!effectiveDesktop(desktopLayout.value, isAutoDesktop)) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.PanoramaWideAngle,
                    title = stringResource(R.string.immersive_mode_title),
                    supportingText = stringResource(R.string.hide_system_bars_for_a_fullscreen_experience),
                    isActive = immersiveMode.value,
                    onClick = { viewModel.setImmersiveMode(!immersiveMode.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.PanoramaWideAngle,
                    title = stringResource(R.string.hide_nav_bar_title),
                    supportingText = stringResource(R.string.hide_nav_bar_desc),
                    isActive = hideNavBar.value,
                    onClick = { viewModel.setHideNavBar(!hideNavBar.value) }
                ),
                SettingsItem(
                    icon = Icons.Default.Padding,
                    title = stringResource(R.string.sticky_navbar_title),
                    supportingText = stringResource(R.string.keep_the_navigation_bar_visible_while_scrolling),
                    isActive = stickyNavbar.value,
                    onClick = { viewModel.setStickyNavbar(!stickyNavbar.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.SwipeVertical,
                    title = stringResource(R.string.collapsing_toolbar_title),
                    supportingText = stringResource(R.string.collapsing_toolbar_desc),
                    isActive = collapsingToolbar.value,
                    onClick = { viewModel.setCollapsingToolbar(!collapsingToolbar.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.Pinch,
                    title = stringResource(R.string.pinch_to_zoom_title),
                    supportingText = stringResource(R.string.use_two_fingers_to_zoom_in_or_out),
                    isActive = pinchToZoom.value,
                    onClick = { viewModel.setPinchToZoom(!pinchToZoom.value) }
                ),
            )
        )

        SettingsSection(
            title = stringResource(R.string.settings_cat_feed),
            icon = Icons.Outlined.DynamicFeed,
            items = listOfNotNull(
                SettingsItem(
                    icon = Icons.Filled.Try,
                    title = stringResource(R.string.suggested_posts),
                    isActive = hideSuggested.value,
                    onClick = { viewModel.setHideSuggested(!hideSuggested.value) }
                ),
                SettingsItem(
                    icon = Icons.Filled.Camera,
                    title = stringResource(R.string.reels),
                    isActive = hideReels.value,
                    onClick = { viewModel.setHideReels(!hideReels.value) }
                ),
                SettingsItem(
                    icon = Icons.Filled.BurstMode,
                    title = stringResource(R.string.stories),
                    isActive = hideStories.value,
                    onClick = { viewModel.setHideStories(!hideStories.value) }
                ),
                // These two only exist on the mobile site.
                if (!effectiveDesktop(desktopLayout.value, isAutoDesktop)) SettingsItem(
                    icon = Icons.Filled.EmojiPeople,
                    title = stringResource(R.string.people_you_may_know),
                    isActive = hidePeopleYouMayKnow.value,
                    onClick = { viewModel.setHidePeopleYouMayKnow(!hidePeopleYouMayKnow.value) }
                ) else null,
                if (!effectiveDesktop(desktopLayout.value, isAutoDesktop)) SettingsItem(
                    icon = Icons.Filled.Diversity1,
                    title = stringResource(R.string.groups),
                    isActive = hideGroups.value,
                    onClick = { viewModel.setHideGroups(!hideGroups.value) }
                ) else null,
                if (!effectiveDesktop(desktopLayout.value, isAutoDesktop)) SettingsItem(
                    icon = Icons.Outlined.AutoAwesome,
                    title = stringResource(R.string.hide_ai_posts_title),
                    isActive = hideAiPosts.value,
                    onClick = { viewModel.setHideAiPosts(!hideAiPosts.value) }
                ) else null,
            )
        )

        SettingsSection(
            title = stringResource(R.string.settings_cat_reels),
            icon = Icons.Outlined.SlowMotionVideo,
            items = listOfNotNull(
                SettingsItem(
                    icon = Icons.Outlined.SkipNext,
                    title = stringResource(R.string.auto_scroll_reels_title),
                    supportingText = stringResource(R.string.auto_scroll_reels_desc),
                    isActive = autoScrollReels.value,
                    onClick = { viewModel.setAutoScrollReels(!autoScrollReels.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.Headset,
                    title = stringResource(R.string.reel_background_audio_title),
                    supportingText = stringResource(R.string.reel_background_audio_desc),
                    isActive = reelBackgroundAudio.value,
                    onClick = { viewModel.setReelBackgroundAudio(!reelBackgroundAudio.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.PlayCircle,
                    title = stringResource(R.string.reel_controls_title),
                    supportingText = stringResource(R.string.reel_controls_desc),
                    isActive = reelControls.value,
                    onClick = { viewModel.setReelControls(!reelControls.value) }
                ),
                if (reelControls.value) SettingsItem(
                    icon = Icons.Outlined.AspectRatio,
                    title = stringResource(R.string.reel_fit_title),
                    supportingText = stringResource(R.string.reel_fit_desc),
                    isActive = reelFit.value,
                    onClick = { viewModel.setReelFit(!reelFit.value) }
                ) else null,
                if (reelControls.value) SettingsItem(
                    icon = Icons.Outlined.Speed,
                    title = stringResource(R.string.reel_gestures_title),
                    supportingText = stringResource(R.string.reel_gestures_desc),
                    isActive = reelGestures.value,
                    onClick = { viewModel.setReelGestures(!reelGestures.value) }
                ) else null,
                if (reelControls.value && reelGestures.value) SettingsItem(
                    icon = Icons.Outlined.Speed,
                    title = stringResource(R.string.reel_hold_speed_title),
                    supportingText = stringResource(R.string.reel_value_tap_to_change, "${reelHoldRate.value}x"),
                    onClick = {
                        val l = listOf(1.5f, 2f, 2.5f, 3f, 4f)
                        viewModel.setReelHoldRate(l[(l.indexOf(reelHoldRate.value) + 1) % l.size])
                    }
                ) else null,
                if (reelControls.value && reelGestures.value) SettingsItem(
                    icon = Icons.Outlined.FastForward,
                    title = stringResource(R.string.reel_skip_title),
                    supportingText = stringResource(R.string.reel_value_tap_to_change, "${reelSkipSeconds.value} s"),
                    onClick = {
                        val l = listOf(5, 10, 15, 30)
                        viewModel.setReelSkipSeconds(l[(l.indexOf(reelSkipSeconds.value) + 1) % l.size])
                    }
                ) else null,
                SettingsItem(
                    icon = Icons.Outlined.ContentCopy,
                    title = stringResource(R.string.copy_to_clipboard_title),
                    supportingText = stringResource(R.string.enable_copy_to_clipboard_button_on_media_view),
                    isActive = enableCopyToClipboard.value,
                    onClick = { viewModel.setEnableCopyToClipboard(!enableCopyToClipboard.value) },
                ),
            )
        )

        // ─── Our golden fpro engine — ported from WebTurbo Pro v2.18.0 ───
        SettingsSection(
            title = stringResource(R.string.fpro_cat_engine),
            icon = Icons.Outlined.AutoAwesome,
            items = listOfNotNull(
                SettingsItem(
                    icon = Icons.Outlined.AutoAwesome,
                    title = stringResource(R.string.fpro_enhance_title),
                    supportingText = stringResource(R.string.fpro_enhance_desc),
                    isActive = fproEnhance.value,
                    onClick = { viewModel.setFproEnhance(!fproEnhance.value) }
                ),
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.AllInclusive,
                    title = stringResource(R.string.fpro_rainbow_title),
                    supportingText = stringResource(R.string.fpro_rainbow_desc),
                    isActive = fproRainbow.value,
                    onClick = { viewModel.setFproRainbow(!fproRainbow.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.FileDownload,
                    title = stringResource(R.string.fpro_downloads_title),
                    supportingText = stringResource(R.string.fpro_downloads_desc),
                    isActive = fproDownloads.value,
                    onClick = { viewModel.setFproDownloads(!fproDownloads.value) }
                ) else null,
                if (fproEnhance.value && fproDownloads.value) SettingsItem(
                    icon = Icons.Outlined.Apps,
                    title = stringResource(R.string.fpro_package_title),
                    supportingText = fproPackage.value,
                    isActive = null,
                    onClick = { isFproPackageDialog = true }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.TextFields,
                    title = stringResource(R.string.fpro_text_selection_title),
                    supportingText = stringResource(R.string.fpro_text_selection_desc),
                    isActive = fproTextSelection.value,
                    onClick = { viewModel.setFproTextSelection(!fproTextSelection.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.TouchApp,
                    title = stringResource(R.string.fpro_double_tap_title),
                    supportingText = stringResource(R.string.fpro_double_tap_desc),
                    isActive = fproDoubleTap.value,
                    onClick = { viewModel.setFproDoubleTap(!fproDoubleTap.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.Palette,
                    title = stringResource(R.string.fpro_color_title),
                    supportingText = fproColor.value,
                    isActive = null,
                    onClick = { isFproColorDialog = true }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.Recommend,
                    title = stringResource(R.string.fpro_reference_engine_title),
                    supportingText = stringResource(R.string.fpro_reference_engine_desc),
                    isActive = fproReferenceEngine.value,
                    onClick = { viewModel.setFproReferenceEngine(!fproReferenceEngine.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.BubbleChart,
                    title = stringResource(R.string.fpro_experimental_colors_title),
                    supportingText = stringResource(R.string.fpro_experimental_colors_desc),
                    isActive = fproExperimentalColors.value,
                    onClick = { viewModel.setFproExperimentalColors(!fproExperimentalColors.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.Colorize,
                    title = stringResource(R.string.fpro_live_color_title),
                    supportingText = stringResource(R.string.fpro_live_color_desc),
                    isActive = fproLiveColor.value,
                    onClick = { viewModel.setFproLiveColor(!fproLiveColor.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.DarkMode,
                    title = stringResource(R.string.fpro_dark_title),
                    supportingText = stringResource(R.string.fpro_dark_desc),
                    isActive = fproDark.value,
                    onClick = { viewModel.setFproDark(!fproDark.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.Block,
                    title = stringResource(R.string.fpro_app_banner_title),
                    supportingText = stringResource(R.string.fpro_app_banner_desc),
                    isActive = fproAppBanner.value,
                    onClick = { viewModel.setFproAppBanner(!fproAppBanner.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.AutoStories,
                    title = stringResource(R.string.fpro_home_stories_title),
                    supportingText = stringResource(R.string.fpro_home_stories_desc),
                    isActive = fproHomeStories.value,
                    onClick = { viewModel.setFproHomeStories(!fproHomeStories.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.SwapHoriz,
                    title = stringResource(R.string.fpro_reverse_title),
                    supportingText = stringResource(R.string.fpro_reverse_desc),
                    isActive = fproReverse.value,
                    onClick = { viewModel.setFproReverse(!fproReverse.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.Devices,
                    title = stringResource(R.string.fpro_browser_agent_title),
                    supportingText = stringResource(R.string.fpro_browser_agent_desc),
                    isActive = fproBrowserAgent.value,
                    onClick = { viewModel.setFproBrowserAgent(!fproBrowserAgent.value) }
                ) else null,
                if (fproEnhance.value) SettingsItem(
                    icon = Icons.Outlined.Cookie,
                    title = stringResource(R.string.fpro_cookies_title),
                    supportingText = stringResource(R.string.fpro_cookies_desc),
                    isActive = fproThirdPartyCookies.value,
                    onClick = { viewModel.setFproThirdPartyCookies(!fproThirdPartyCookies.value) }
                ) else null,
                SettingsItem(
                    icon = Icons.Outlined.Headphones,
                    title = stringResource(R.string.fpro_audio_only_title),
                    supportingText = stringResource(R.string.fpro_audio_only_desc),
                    isActive = null,
                    onClick = onAudioOnly
                ),
            )
        )

        SettingsSection(
            title = stringResource(R.string.settings_cat_pip),
            icon = Icons.Outlined.PictureInPictureAlt,
            items = listOfNotNull(
                SettingsItem(
                    icon = Icons.Outlined.PictureInPictureAlt,
                    title = stringResource(R.string.pip_title),
                    supportingText = stringResource(R.string.shrink_into_a_floating_window_when_leaving_while_a_video_plays),
                    isActive = pipEnabled.value,
                    onClick = { viewModel.setPipEnabled(!pipEnabled.value) }
                ),
                if (pipEnabled.value) SettingsItem(
                    icon = Icons.Outlined.AspectRatio,
                    title = stringResource(R.string.pip_ratio_title),
                    supportingText = "$deviceLabel · ${pipPortraitRatio.value}",
                    isActive = null,
                    onClick = { isPipRatioDialog = true }
                ) else null,
                // No POST_NOTIFICATIONS prompt here: notifications for a
                // media session are exempt from that permission on API 33+
                // (Android notification-permission docs), so the media
                // notification and lock-screen controls show without it.
                if (pipEnabled.value) SettingsItem(
                    icon = Icons.Outlined.Headphones,
                    title = stringResource(R.string.pip_lockscreen_audio_title),
                    supportingText = stringResource(R.string.pip_lockscreen_audio_summary),
                    isActive = pipLockscreenAudio.value,
                    onClick = { viewModel.setPipLockscreenAudio(!pipLockscreenAudio.value) }
                ) else null,
            )
        )

        SettingsSection(
            title = stringResource(R.string.settings_cat_messages),
            icon = Icons.AutoMirrored.Outlined.Message,
            items = listOf(
                SettingsItem(
                    icon = Icons.AutoMirrored.Outlined.Message,
                    title = stringResource(R.string.open_messenger_title),
                    supportingText = stringResource(R.string.open_messenger_desc),
                    isActive = null,
                    onClick = {
                        val ok = openMessenger(context, "https://m.me/", messengerPackage.value)
                        if (!ok) {
                            Toast.makeText(
                                context,
                                messengerRedirectToast,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                ),
                // Default: Messages opens inside the app (desktop site). Turn this on to hand
                // Messages, and what is shared to the app, to an external Messenger app instead.
                SettingsItem(
                    icon = Icons.AutoMirrored.Outlined.Message,
                    title = stringResource(R.string.use_external_messenger_title),
                    supportingText = stringResource(R.string.use_external_messenger_desc),
                    isActive = !messagesDesktop.value,
                    onClick = { viewModel.setMessagesDesktop(!messagesDesktop.value) },
                ),
            ) + if (!messagesDesktop.value) listOf(
                SettingsItem(
                    icon = Icons.AutoMirrored.Outlined.Message,
                    title = stringResource(R.string.messenger_title),
                    supportingText = messengerApps.firstOrNull { it.packageName == messengerPackage.value }?.label
                        ?: messengerPackage.value,
                    isActive = null,
                    onClick = { isMessengerDialog = true },
                )
            ) else emptyList()
        )

        SettingsSection(
            title = stringResource(R.string.settings_cat_privacy),
            icon = Icons.Outlined.Security,
            items = listOfNotNull(
                SettingsItem(
                    icon = Icons.Outlined.Shield,
                    title = stringResource(R.string.remove_ads_title),
                    supportingText = stringResource(R.string.hide_sponsored_ads_from_your_feed),
                    isActive = removeAds.value,
                    onClick = { viewModel.setRemoveAds(!removeAds.value) },
                ),
                SettingsItem(
                    icon = Icons.Outlined.Block,
                    title = stringResource(R.string.brave_blocklist_title),
                    supportingText = stringResource(R.string.brave_blocklist_supporting),
                    isActive = braveBlockList.value,
                    onClick = { viewModel.setBraveBlockList(!braveBlockList.value) },
                ),
                SettingsItem(
                    icon = Icons.Outlined.Lock,
                    title = stringResource(R.string.app_lock_title),
                    supportingText = stringResource(R.string.app_lock_desc),
                    isActive = appLock.value,
                    onClick = {
                        val fragmentActivity = activity as? FragmentActivity
                        when {
                            appLock.value -> viewModel.setAppLock(false)
                            !canUseAppLock(context) || fragmentActivity == null ->
                                Toast.makeText(context, appLockUnavailableToast, Toast.LENGTH_LONG).show()
                            // Turning it on first checks it is really the owner: fingerprint, face or PIN.
                            else -> authenticateUser(fragmentActivity, appLockVerifyTitle) { ok ->
                                if (ok) viewModel.setAppLock(true)
                            }
                        }
                    }
                ),
                SettingsItem(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    title = stringResource(R.string.log_out_title),
                    supportingText = stringResource(R.string.log_out_desc),
                    onClick = { isLogoutDialog = true }
                ),
            )
        )

        SettingsSection(
            title = stringResource(R.string.settings_cat_data),
            icon = Icons.Outlined.Storage,
            items = listOfNotNull(
                SettingsItem(
                    icon = Icons.Outlined.SystemUpdate,
                    title = stringResource(R.string.auto_update_check_title),
                    supportingText = stringResource(R.string.auto_update_check_desc),
                    isActive = autoUpdateCheck.value,
                    onClick = { viewModel.setAutoUpdateCheck(!autoUpdateCheck.value) }
                ),
                SettingsItem(
                    icon = Icons.Outlined.Refresh,
                    title = stringResource(R.string.check_update_now_title),
                    supportingText = stringResource(R.string.check_update_now_desc, BuildConfig.VERSION_NAME),
                    isActive = null,
                    onClick = {
                        scope.launch {
                            update = checkForUpdate()
                            if (update == null) Toast.makeText(context, upToDateToast, Toast.LENGTH_SHORT).show()
                        }
                    }
                ),
                SettingsItem(
                    icon = Icons.Outlined.FileDownload,
                    title = stringResource(R.string.export_settings_title),
                    supportingText = stringResource(R.string.export_settings_desc),
                    onClick = { exportLauncher.launch("faceboost-settings.json") }
                ),
                SettingsItem(
                    icon = Icons.Outlined.FileUpload,
                    title = stringResource(R.string.import_settings_title),
                    supportingText = stringResource(R.string.import_settings_desc),
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) }
                ),
            )
        )

    }

    if (isLogoutDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { isLogoutDialog = false },
            title = { Text(stringResource(R.string.log_out_title)) },
            text = { Text(stringResource(R.string.log_out_confirm)) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { isLogoutDialog = false; onLogout() }) {
                    Text(stringResource(R.string.log_out_title), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { isLogoutDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
    update?.let { UpdateDialog(it) { update = null } }

    if (isPipRatioDialog) {
        PipRatioDialog(
            currentRatio = viewModel.pipPortraitRatio.collectAsState().value,
            onSelect = { ratio ->
                viewModel.setPipPortraitRatio(ratio)
                @Suppress("AssignedValueIsNeverRead")
                isPipRatioDialog = false
            },
            onDismiss = {
                @Suppress("AssignedValueIsNeverRead")
                isPipRatioDialog = false
            }
        )
    }
    if (isMessengerDialog) {
        MessengerAppDialog(
            apps = messengerApps,
            current = messengerPackage.value,
            onDismiss = { isMessengerDialog = false },
            onSelect = {
                viewModel.setMessengerPackage(it)
                isMessengerDialog = false
            },
        )
    }

    // ─── Our golden fpro engine dialogs ───
    if (isFproColorDialog) {
        FproColorDialog(
            current = fproColor.value,
            onDismiss = { isFproColorDialog = false },
            onSelect = {
                viewModel.setFproColor(it)
                isFproColorDialog = false
            },
        )
    }
    if (isFproPackageDialog) {
        FproPackageDialog(
            current = fproPackage.value,
            onDismiss = { isFproPackageDialog = false },
            onSave = {
                viewModel.setFproPackage(it)
                isFproPackageDialog = false
            },
        )
    }
}

/** Our fpro Facebook accent color picker (تلقائي/طبيعي/ذهبي/...). */
@Composable
private fun FproColorDialog(
    current: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.fpro_color_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            FPRO_COLOR_CHOICES.forEach { name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable { onSelect(name) }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(selected = name == current, onClick = { onSelect(name) })
                    Box(
                        Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(FPRO_COLOR_SWATCHES.getValue(name))
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/** Our fpro external downloader package editor (خانة كتابة اسم حزمة التطبيق). */
@Composable
private fun FproPackageDialog(
    current: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var text by rememberSaveable(current) { mutableStateOf(current) }
    val isValid = Regex("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)+").matches(text.trim())
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.fpro_package_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.fpro_package_desc))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.fpro_package_hint)) },
                    isError = text.isNotBlank() && !isValid,
                    supportingText = {
                        if (text.isNotBlank() && !isValid) {
                            Text(stringResource(R.string.fpro_package_invalid))
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = { onSave(text.trim()) }
            ) { Text(stringResource(R.string.share_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.share_cancel)) } },
    )
}

private val FPRO_COLOR_CHOICES = listOf(
    "تلقائي", "طبيعي", "ذهبي", "أحمر", "أخضر", "بنفسجي",
    "برتقالي", "فيروزي", "وردي", "بني", "فضي", "أبيض",
)

private val FPRO_COLOR_SWATCHES = mapOf(
    "تلقائي" to androidx.compose.ui.graphics.Color(0xFF1877F2),
    "طبيعي" to androidx.compose.ui.graphics.Color(0xFF1877F2),
    "ذهبي" to androidx.compose.ui.graphics.Color(0xFFD4AF37),
    "أحمر" to androidx.compose.ui.graphics.Color(0xFFC62828),
    "أخضر" to androidx.compose.ui.graphics.Color(0xFF17833E),
    "بنفسجي" to androidx.compose.ui.graphics.Color(0xFF8643C5),
    "برتقالي" to androidx.compose.ui.graphics.Color(0xFFED701B),
    "فيروزي" to androidx.compose.ui.graphics.Color(0xFF008C95),
    "وردي" to androidx.compose.ui.graphics.Color(0xFFD34489),
    "بني" to androidx.compose.ui.graphics.Color(0xFF805539),
    "فضي" to androidx.compose.ui.graphics.Color(0xFF9BA6B0),
    "أبيض" to androidx.compose.ui.graphics.Color(0xFFFFFFFF),
)

/** A select list of the installed Messenger apps compatible with Facebook's links. */
@Composable
private fun MessengerAppDialog(
    apps: List<com.almekhlafi.materialbook.utils.MessengerApp>,
    current: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.messenger_title)) },
        text = {
            if (apps.isEmpty()) {
                Text(stringResource(R.string.messenger_none_installed))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    apps.forEach { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelect(app.packageName) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            androidx.compose.material3.RadioButton(selected = app.packageName == current, onClick = { onSelect(app.packageName) })
                            androidx.compose.foundation.Image(
                                bitmap = app.icon.toBitmap(96, 96).asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                            )
                            Column {
                                Text(app.label, style = MaterialTheme.typography.bodyLarge)
                                Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.share_cancel)) } },
    )
}

private val PIP_RATIO_PRESETS = listOf(
    "4:7",
    "2:3",
    "3:4",
    "9:16",
)

@Composable
private fun PipRatioDialog(
    currentRatio: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.pip_ratio_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            PIP_RATIO_PRESETS.forEach { ratio ->
                val label = when (ratio) {
                    "4:7"  -> stringResource(R.string.pip_ratio_4_7)
                    "2:3"  -> stringResource(R.string.pip_ratio_2_3)
                    "3:4"  -> stringResource(R.string.pip_ratio_3_4)
                    "9:16" -> stringResource(R.string.pip_ratio_9_16)
                    else   -> ratio
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable { onSelect(ratio) }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = ratio == currentRatio,
                        onClick = { onSelect(ratio) }
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
private fun isBatteryFree(context: android.content.Context) =
    (context.getSystemService(android.content.Context.POWER_SERVICE) as PowerManager)
        .isIgnoringBatteryOptimizations(context.packageName)
