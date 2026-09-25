package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AirPodsConnectionPopup
import com.example.ui.screens.AirPodsControlsScreen
import com.example.ui.screens.AudioToolsScreen
import com.example.ui.screens.BatterySettingsScreen
import com.example.ui.screens.EqualizerScreen
import com.example.ui.screens.HubScreen
import com.example.ui.theme.DarkGrayTrack
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

sealed class ScreenTab(val title: String, val icon: ImageVector, val tag: String) {
    object Hub : ScreenTab("AirPods", Icons.Default.Headphones, "nav_tab_hub")
    object Equalizer : ScreenTab("Equalizer", Icons.Default.GraphicEq, "nav_tab_equalizer")
    object Controls : ScreenTab("Controls", Icons.Default.Tune, "nav_tab_controls")
    object Tools : ScreenTab("Audio Lab", Icons.Default.Hearing, "nav_tab_tools")
    object Settings : ScreenTab("Settings", Icons.Default.Settings, "nav_tab_settings")
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = listOf(
        ScreenTab.Hub,
        ScreenTab.Equalizer,
        ScreenTab.Controls,
        ScreenTab.Tools,
        ScreenTab.Settings
    )

    // Collect UI state from ViewModel
    val activePreset by viewModel.activePreset.collectAsStateWithLifecycle()
    val allPresets by viewModel.allPresets.collectAsStateWithLifecycle()
    val isEqEnabled by viewModel.isEqEnabled.collectAsStateWithLifecycle()
    val ancMode by viewModel.ancMode.collectAsStateWithLifecycle()
    val spatialMode by viewModel.spatialMode.collectAsStateWithLifecycle()
    val batteryState by viewModel.batteryState.collectAsStateWithLifecycle()

    val isBgServiceEnabled by viewModel.isBgServiceEnabled.collectAsStateWithLifecycle()
    val isAutoConnectEnabled by viewModel.isAutoConnectEnabled.collectAsStateWithLifecycle()
    val isBattery80Guard by viewModel.isBattery80Guard.collectAsStateWithLifecycle()
    val isEarDetection by viewModel.isEarDetection.collectAsStateWithLifecycle()
    val isGamingMode by viewModel.isGamingMode.collectAsStateWithLifecycle()
    val isConversational by viewModel.isConversational.collectAsStateWithLifecycle()
    val isWindNoiseReduction by viewModel.isWindNoiseReduction.collectAsStateWithLifecycle()
    val stemHoldAction by viewModel.stemHoldAction.collectAsStateWithLifecycle()
    val channelBalance by viewModel.channelBalance.collectAsStateWithLifecycle()
    val hardwareInfo by viewModel.hardwareInfo.collectAsStateWithLifecycle()

    val hearingState by viewModel.hearingState.collectAsStateWithLifecycle()
    val earTipState by viewModel.earTipState.collectAsStateWithLifecycle()
    val currentDecibels by viewModel.currentDecibels.collectAsStateWithLifecycle()
    val findMyActiveEar by viewModel.findMyActiveEar.collectAsStateWithLifecycle()
    val proximityRssi by viewModel.proximityRssiPercent.collectAsStateWithLifecycle()
    val sleepTimer by viewModel.sleepTimer.collectAsStateWithLifecycle()
    val showPopup by viewModel.showAirPodsPopup.collectAsStateWithLifecycle()
    val isUpdatingFirmware by viewModel.isUpdatingFirmware.collectAsStateWithLifecycle()
    val firmwareProgress by viewModel.firmwareUpdateProgress.collectAsStateWithLifecycle()
    val connectedDevices by viewModel.connectedDevices.collectAsStateWithLifecycle()

    // Request permissions for Notifications (Android 13+) and Audio Recording
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle result gracefully
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = StudioDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = StudioCardSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding().testTag("main_navigation_bar")
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedTab == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0B0E14),
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HubScreen(
                    battery = batteryState,
                    activePreset = activePreset,
                    ancMode = ancMode,
                    spatialMode = spatialMode,
                    isEqEnabled = isEqEnabled,
                    is80GuardActive = isBattery80Guard,
                    isBgServiceActive = isBgServiceEnabled,
                    onSelectAncMode = viewModel::setAncMode,
                    onSelectSpatialMode = viewModel::setSpatialAudioMode,
                    onToggleEq = viewModel::toggleEqEnabled,
                    onNavigateToEqualizer = { selectedTab = 1 },
                    onShowConnectionPopup = { viewModel.triggerAirPodsPopup(true) }
                )
                1 -> EqualizerScreen(
                    activePreset = activePreset,
                    allPresets = allPresets,
                    isEqEnabled = isEqEnabled,
                    onSelectPreset = viewModel::selectPreset,
                    onBandChange = viewModel::updateBandLevel,
                    onBassChange = viewModel::updateBassBoost,
                    onVirtualizerChange = viewModel::updateVirtualizer,
                    onLoudnessChange = viewModel::updateLoudnessGain,
                    onToggleEq = viewModel::toggleEqEnabled,
                    onSaveNewPreset = viewModel::saveAsNewCustomPreset,
                    onDeletePreset = viewModel::deleteCustomPreset
                )
                2 -> AirPodsControlsScreen(
                    ancMode = ancMode,
                    isEarDetection = isEarDetection,
                    isGamingMode = isGamingMode,
                    isConversational = isConversational,
                    isWindNoiseReduction = isWindNoiseReduction,
                    stemHoldAction = stemHoldAction,
                    channelBalance = channelBalance,
                    onSelectAncMode = viewModel::setAncMode,
                    onToggleEarDetection = viewModel::toggleEarDetection,
                    onToggleGamingMode = viewModel::toggleGamingMode,
                    onToggleConversational = viewModel::toggleConversational,
                    onToggleWindNoiseReduction = viewModel::toggleWindNoiseReduction,
                    onSelectStemHoldAction = viewModel::setStemHoldAction,
                    onChannelBalanceChange = viewModel::setChannelBalance
                )
                3 -> AudioToolsScreen(
                    hearingState = hearingState,
                    testFrequencies = viewModel.testFrequencies,
                    earTipState = earTipState,
                    currentDecibels = currentDecibels,
                    findMyActiveEar = findMyActiveEar,
                    proximityRssi = proximityRssi,
                    onStartHearingTest = viewModel::startHearingTest,
                    onRecordHearingThreshold = viewModel::recordHearingThreshold,
                    onCancelHearingTest = viewModel::cancelHearingTest,
                    onStartEarTipFitTest = viewModel::startEarTipFitTest,
                    onTriggerFindMyChirp = viewModel::triggerFindMyChirp,
                    onStopFindMyChirp = viewModel::stopFindMyChirp
                )
                4 -> BatterySettingsScreen(
                    isBgServiceEnabled = isBgServiceEnabled,
                    isAutoConnectEnabled = isAutoConnectEnabled,
                    isBattery80GuardEnabled = isBattery80Guard,
                    hardwareInfo = hardwareInfo,
                    isUpdatingFirmware = isUpdatingFirmware,
                    firmwareProgress = firmwareProgress,
                    sleepTimer = sleepTimer,
                    connectedDevices = connectedDevices,
                    onToggleBgService = viewModel::toggleBackgroundService,
                    onToggleAutoConnect = viewModel::toggleAutoConnect,
                    onToggleBattery80Guard = viewModel::toggleBattery80Guard,
                    onSetSleepTimer = viewModel::setSleepTimer,
                    onCancelSleepTimer = viewModel::cancelSleepTimer,
                    onCheckFirmwareUpdate = viewModel::checkForFirmwareUpdate,
                    onRefreshDevices = viewModel::refreshConnectedDevices
                )
            }

            // AirPods Dynamic Connection Popup Overlay
            AirPodsConnectionPopup(
                visible = showPopup,
                deviceName = hardwareInfo.modelName,
                battery = batteryState,
                onDismiss = { viewModel.triggerAirPodsPopup(false) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
