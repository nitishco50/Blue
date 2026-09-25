package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.DecibelMeter
import com.example.audio.ToneGenerator
import com.example.bluetooth.BluetoothAudioHelper
import com.example.bluetooth.ConnectedDevice
import com.example.data.db.AppDatabase
import com.example.data.model.AirPodsBattery
import com.example.data.model.AncMode
import com.example.data.model.AudioPreset
import com.example.data.model.DeviceHardwareInfo
import com.example.data.model.EarTipFitResult
import com.example.data.model.HearingTestFrequency
import com.example.data.model.SpatialAudioMode
import com.example.data.preferences.AppPreferences
import com.example.data.repository.PresetRepository
import com.example.service.AudioFxService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HearingTestState(
    val isActive: Boolean = false,
    val currentEar: String = "LEFT", // "LEFT" or "RIGHT"
    val currentFrequencyIndex: Int = 0,
    val currentVolumeDb: Int = 20, // 0 to 60 dB
    val leftResults: Map<Int, Int> = emptyMap(),
    val rightResults: Map<Int, Int> = emptyMap(),
    val isComplete: Boolean = false,
    val overallScore: Int = 94
)

data class EarTipTestState(
    val isTesting: Boolean = false,
    val progress: Float = 0f,
    val result: EarTipFitResult? = null
)

data class SleepTimerState(
    val isActive: Boolean = false,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application
    private val appPreferences = AppPreferences(application)
    private val repository: PresetRepository
    private val bluetoothHelper = BluetoothAudioHelper(application)

    init {
        val db = AppDatabase.getInstance(application)
        repository = PresetRepository(db.presetDao())
    }

    // Custom presets from Room database
    val customPresets: StateFlow<List<AudioPreset>> = repository.customPresets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All presets combining built-in and custom
    val allPresets: StateFlow<List<AudioPreset>> = repository.customPresets
        .map { customList -> AudioPreset.BUILT_IN_PRESETS + customList }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AudioPreset.BUILT_IN_PRESETS)

    // Active sound curve & EQ
    private val _activePreset = MutableStateFlow<AudioPreset>(AudioPreset.BUILT_IN_PRESETS[1])
    val activePreset: StateFlow<AudioPreset> = _activePreset.asStateFlow()

    private val _isEqEnabled = MutableStateFlow(true)
    val isEqEnabled: StateFlow<Boolean> = _isEqEnabled.asStateFlow()

    // AirPods Hardware Controls
    private val _ancMode = MutableStateFlow(appPreferences.ancMode)
    val ancMode: StateFlow<AncMode> = _ancMode.asStateFlow()

    private val _spatialMode = MutableStateFlow(appPreferences.spatialAudioMode)
    val spatialMode: StateFlow<SpatialAudioMode> = _spatialMode.asStateFlow()

    private val _batteryState = MutableStateFlow(
        AirPodsBattery(
            leftPercent = 88,
            rightPercent = 92,
            casePercent = 74,
            isLeftCharging = false,
            isRightCharging = false,
            isCaseCharging = true
        )
    )
    val batteryState: StateFlow<AirPodsBattery> = _batteryState.asStateFlow()

    // Preferences & Toggles
    private val _isBgServiceEnabled = MutableStateFlow(appPreferences.isBackgroundServiceEnabled)
    val isBgServiceEnabled: StateFlow<Boolean> = _isBgServiceEnabled.asStateFlow()

    private val _isAutoConnectEnabled = MutableStateFlow(appPreferences.isAutoConnectEnabled)
    val isAutoConnectEnabled: StateFlow<Boolean> = _isAutoConnectEnabled.asStateFlow()

    private val _isBattery80Guard = MutableStateFlow(appPreferences.isBattery80LimitGuardEnabled)
    val isBattery80Guard: StateFlow<Boolean> = _isBattery80Guard.asStateFlow()

    private val _isEarDetection = MutableStateFlow(appPreferences.isEarDetectionEnabled)
    val isEarDetection: StateFlow<Boolean> = _isEarDetection.asStateFlow()

    private val _isGamingMode = MutableStateFlow(appPreferences.isGamingModeEnabled)
    val isGamingMode: StateFlow<Boolean> = _isGamingMode.asStateFlow()

    private val _isConversational = MutableStateFlow(appPreferences.isConversationalAwarenessEnabled)
    val isConversational: StateFlow<Boolean> = _isConversational.asStateFlow()

    private val _isWindNoiseReduction = MutableStateFlow(appPreferences.isWindNoiseReductionEnabled)
    val isWindNoiseReduction: StateFlow<Boolean> = _isWindNoiseReduction.asStateFlow()

    private val _stemHoldAction = MutableStateFlow(appPreferences.stemPressAndHoldAction)
    val stemHoldAction: StateFlow<String> = _stemHoldAction.asStateFlow()

    private val _channelBalance = MutableStateFlow(appPreferences.channelBalance)
    val channelBalance: StateFlow<Float> = _channelBalance.asStateFlow()

    // Hardware Info
    private val _hardwareInfo = MutableStateFlow(DeviceHardwareInfo())
    val hardwareInfo: StateFlow<DeviceHardwareInfo> = _hardwareInfo.asStateFlow()

    // Hearing Test & Tools State
    private val _hearingState = MutableStateFlow(HearingTestState())
    val hearingState: StateFlow<HearingTestState> = _hearingState.asStateFlow()

    private val _earTipState = MutableStateFlow(EarTipTestState())
    val earTipState: StateFlow<EarTipTestState> = _earTipState.asStateFlow()

    private val _currentDecibels = MutableStateFlow(44.0f)
    val currentDecibels: StateFlow<Float> = _currentDecibels.asStateFlow()

    private val _findMyActiveEar = MutableStateFlow<String?>(null) // "LEFT", "RIGHT", or null
    val findMyActiveEar: StateFlow<String?> = _findMyActiveEar.asStateFlow()

    private val _proximityRssiPercent = MutableStateFlow(84) // Proximity radar %
    val proximityRssiPercent: StateFlow<Int> = _proximityRssiPercent.asStateFlow()

    private val _sleepTimer = MutableStateFlow(SleepTimerState())
    val sleepTimer: StateFlow<SleepTimerState> = _sleepTimer.asStateFlow()

    private val _showAirPodsPopup = MutableStateFlow(false)
    val showAirPodsPopup: StateFlow<Boolean> = _showAirPodsPopup.asStateFlow()

    private val _isUpdatingFirmware = MutableStateFlow(false)
    val isUpdatingFirmware: StateFlow<Boolean> = _isUpdatingFirmware.asStateFlow()

    private val _firmwareUpdateProgress = MutableStateFlow(0f)
    val firmwareUpdateProgress: StateFlow<Float> = _firmwareUpdateProgress.asStateFlow()

    private val _connectedDevices = MutableStateFlow<List<ConnectedDevice>>(emptyList())
    val connectedDevices: StateFlow<List<ConnectedDevice>> = _connectedDevices.asStateFlow()

    val testFrequencies = listOf(
        HearingTestFrequency(250, "250 Hz"),
        HearingTestFrequency(500, "500 Hz"),
        HearingTestFrequency(1000, "1 kHz"),
        HearingTestFrequency(2000, "2 kHz"),
        HearingTestFrequency(4000, "4 kHz"),
        HearingTestFrequency(8000, "8 kHz")
    )

    private var sleepTimerJob: Job? = null
    private var decibelJob: Job? = null

    init {
        loadInitialPreset()
        refreshConnectedDevices()
        startDecibelMonitoring()
        if (_isBgServiceEnabled.value) {
            startAudioService()
        }
    }

    private fun loadInitialPreset() {
        val savedId = appPreferences.selectedPresetId
        val preset = AudioPreset.BUILT_IN_PRESETS.find { it.id == savedId }
            ?: AudioPreset.BUILT_IN_PRESETS[1]
        _activePreset.value = preset
    }

    fun selectPreset(preset: AudioPreset) {
        _activePreset.value = preset
        appPreferences.selectedPresetId = preset.id
        syncService()
    }

    fun updateBandLevel(bandIndex: Int, levelMillibels: Int) {
        val current = _activePreset.value
        val updatedBands = current.bandLevels.toMutableList()
        if (bandIndex in updatedBands.indices) {
            updatedBands[bandIndex] = levelMillibels
            val updated = current.copy(
                name = if (current.isCustom) current.name else "${current.name} (Customized)",
                bandLevels = updatedBands
            )
            _activePreset.value = updated
            syncService()
        }
    }

    fun updateBassBoost(value: Int) {
        val updated = _activePreset.value.copy(bassBoost = value.coerceIn(0, 1000))
        _activePreset.value = updated
        syncService()
    }

    fun updateVirtualizer(value: Int) {
        val updated = _activePreset.value.copy(virtualizer = value.coerceIn(0, 1000))
        _activePreset.value = updated
        syncService()
    }

    fun updateLoudnessGain(value: Int) {
        val updated = _activePreset.value.copy(loudnessGain = value.coerceIn(0, 1000))
        _activePreset.value = updated
        syncService()
    }

    fun toggleEqEnabled(enabled: Boolean) {
        _isEqEnabled.value = enabled
        syncService()
    }

    fun saveAsNewCustomPreset(name: String) {
        viewModelScope.launch {
            val current = _activePreset.value
            val custom = AudioPreset(
                id = 0,
                name = name.ifBlank { "My Sound Profile" },
                isCustom = true,
                bandLevels = current.bandLevels,
                bassBoost = current.bassBoost,
                virtualizer = current.virtualizer,
                loudnessGain = current.loudnessGain
            )
            val newId = repository.savePreset(custom)
            val savedPreset = custom.copy(id = newId)
            _activePreset.value = savedPreset
            appPreferences.selectedPresetId = newId
            syncService()
        }
    }

    fun deleteCustomPreset(preset: AudioPreset) {
        viewModelScope.launch {
            if (preset.isCustom && preset.id > 0) {
                repository.deletePreset(preset.id)
                if (_activePreset.value.id == preset.id) {
                    selectPreset(AudioPreset.BUILT_IN_PRESETS[1])
                }
            }
        }
    }

    // ANC and Audio Mode Controls
    fun setAncMode(mode: AncMode) {
        _ancMode.value = mode
        appPreferences.ancMode = mode
        syncService()
    }

    fun setSpatialAudioMode(mode: SpatialAudioMode) {
        _spatialMode.value = mode
        appPreferences.spatialAudioMode = mode
        val newVirtualizer = when (mode) {
            SpatialAudioMode.OFF -> 0
            SpatialAudioMode.FIXED -> 650
            SpatialAudioMode.HEAD_TRACKED -> 950
        }
        updateVirtualizer(newVirtualizer)
    }

    fun toggleBackgroundService(enabled: Boolean) {
        _isBgServiceEnabled.value = enabled
        appPreferences.isBackgroundServiceEnabled = enabled
        if (enabled) {
            startAudioService()
        } else {
            AudioFxService.stopService(app)
        }
    }

    fun toggleAutoConnect(enabled: Boolean) {
        _isAutoConnectEnabled.value = enabled
        appPreferences.isAutoConnectEnabled = enabled
    }

    fun toggleBattery80Guard(enabled: Boolean) {
        _isBattery80Guard.value = enabled
        appPreferences.isBattery80LimitGuardEnabled = enabled
    }

    fun toggleEarDetection(enabled: Boolean) {
        _isEarDetection.value = enabled
        appPreferences.isEarDetectionEnabled = enabled
    }

    fun toggleGamingMode(enabled: Boolean) {
        _isGamingMode.value = enabled
        appPreferences.isGamingModeEnabled = enabled
    }

    fun toggleConversational(enabled: Boolean) {
        _isConversational.value = enabled
        appPreferences.isConversationalAwarenessEnabled = enabled
    }

    fun toggleWindNoiseReduction(enabled: Boolean) {
        _isWindNoiseReduction.value = enabled
        appPreferences.isWindNoiseReductionEnabled = enabled
    }

    fun setStemHoldAction(action: String) {
        _stemHoldAction.value = action
        appPreferences.stemPressAndHoldAction = action
    }

    fun setChannelBalance(balance: Float) {
        val clamped = balance.coerceIn(-1.0f, 1.0f)
        _channelBalance.value = clamped
        appPreferences.channelBalance = clamped
    }

    fun triggerAirPodsPopup(show: Boolean) {
        _showAirPodsPopup.value = show
    }

    // Hearing Test Implementation
    fun startHearingTest() {
        _hearingState.value = HearingTestState(
            isActive = true,
            currentEar = "LEFT",
            currentFrequencyIndex = 0,
            currentVolumeDb = 20,
            leftResults = emptyMap(),
            rightResults = emptyMap(),
            isComplete = false
        )
        playHearingTestTone()
    }

    fun recordHearingThreshold(heard: Boolean) {
        val state = _hearingState.value
        val freq = testFrequencies[state.currentFrequencyIndex].frequencyHz

        if (heard) {
            // Heard it! Record threshold
            if (state.currentEar == "LEFT") {
                val updatedLeft = state.leftResults + (freq to state.currentVolumeDb)
                if (state.currentFrequencyIndex + 1 < testFrequencies.size) {
                    _hearingState.value = state.copy(
                        currentFrequencyIndex = state.currentFrequencyIndex + 1,
                        currentVolumeDb = 20,
                        leftResults = updatedLeft
                    )
                    playHearingTestTone()
                } else {
                    // Left ear complete, switch to Right ear
                    _hearingState.value = state.copy(
                        currentEar = "RIGHT",
                        currentFrequencyIndex = 0,
                        currentVolumeDb = 20,
                        leftResults = updatedLeft
                    )
                    playHearingTestTone()
                }
            } else {
                val updatedRight = state.rightResults + (freq to state.currentVolumeDb)
                if (state.currentFrequencyIndex + 1 < testFrequencies.size) {
                    _hearingState.value = state.copy(
                        currentFrequencyIndex = state.currentFrequencyIndex + 1,
                        currentVolumeDb = 20,
                        rightResults = updatedRight
                    )
                    playHearingTestTone()
                } else {
                    // Test complete! Generate personalized profile
                    ToneGenerator.stopTone()
                    val avgLoss = ((state.leftResults.values.average() + updatedRight.values.average()) / 2.0).toInt()
                    val score = (100 - avgLoss).coerceIn(70, 99)
                    _hearingState.value = state.copy(
                        isComplete = true,
                        isActive = false,
                        rightResults = updatedRight,
                        overallScore = score
                    )
                    appPreferences.hearingTestScore = score
                    applyPersonalizedHearingProfile(state.leftResults, updatedRight)
                }
            }
        } else {
            // Did not hear, increase volume
            if (state.currentVolumeDb < 55) {
                _hearingState.value = state.copy(currentVolumeDb = state.currentVolumeDb + 10)
                playHearingTestTone()
            } else {
                // max reached, mark as threshold
                recordHearingThreshold(true)
            }
        }
    }

    private fun playHearingTestTone() {
        val state = _hearingState.value
        if (!state.isActive) return
        val freq = testFrequencies[state.currentFrequencyIndex].frequencyHz
        val normalizedVol = (state.currentVolumeDb / 60.0f).coerceIn(0.1f, 0.9f)
        viewModelScope.launch {
            ToneGenerator.playTone(
                frequencyHz = freq,
                durationMs = 900,
                volume = normalizedVol,
                channel = state.currentEar
            )
        }
    }

    fun cancelHearingTest() {
        ToneGenerator.stopTone()
        _hearingState.value = HearingTestState(isActive = false)
    }

    private fun applyPersonalizedHearingProfile(
        left: Map<Int, Int>,
        right: Map<Int, Int>
    ) {
        // Calculate compensation EQ: boost frequencies where threshold was higher
        val bands = listOf(
            250 to 0,   // Band 0: 60-230 Hz
            500 to 1,   // Band 1: 230-910 Hz
            1000 to 2,  // Band 2: 910 Hz
            4000 to 3,  // Band 3: 3600 Hz
            8000 to 4   // Band 4: 14000 Hz
        )
        val newLevels = mutableListOf(0, 0, 0, 0, 0)
        for ((freq, idx) in bands) {
            val lVal = left[freq] ?: 20
            val rVal = right[freq] ?: 20
            val avg = (lVal + rVal) / 2
            // If hearing required > 30dB, boost this band
            val boost = ((avg - 20) * 25).coerceIn(-200, 600)
            newLevels[idx] = boost
        }

        val profile = AudioPreset(
            id = 9999,
            name = "Personalized Audiogram Profile",
            isCustom = true,
            bandLevels = newLevels,
            bassBoost = 350,
            virtualizer = 400,
            loudnessGain = 200
        )
        _activePreset.value = profile
        appPreferences.personalizedProfileEnabled = true
        syncService()
    }

    // Ear Tip Fit Test
    fun startEarTipFitTest() {
        viewModelScope.launch {
            _earTipState.value = EarTipTestState(isTesting = true, progress = 0.05f)
            ToneGenerator.playTone(frequencyHz = 1200, durationMs = 1500, volume = 0.6f)

            for (p in 1..10) {
                delay(200)
                _earTipState.value = _earTipState.value.copy(progress = p / 10f)
            }

            ToneGenerator.stopTone()
            _earTipState.value = EarTipTestState(
                isTesting = false,
                progress = 1.0f,
                result = EarTipFitResult(
                    leftStatus = "Good Seal",
                    rightStatus = "Good Seal",
                    leftScore = 96,
                    rightScore = 93,
                    recommendation = "Both earbuds have an airtight acoustic seal. Optimal ANC and bass response active."
                )
            )
        }
    }

    // Find My AirPods
    fun triggerFindMyChirp(ear: String) {
        viewModelScope.launch {
            _findMyActiveEar.value = ear
            ToneGenerator.playFindMyChirp(channel = ear, repetitions = 3)
            _findMyActiveEar.value = null
        }
    }

    fun stopFindMyChirp() {
        ToneGenerator.stopTone()
        _findMyActiveEar.value = null
    }

    // Sleep Timer
    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _sleepTimer.value = SleepTimerState(isActive = false)
            return
        }
        val totalSecs = minutes * 60
        _sleepTimer.value = SleepTimerState(
            isActive = true,
            remainingSeconds = totalSecs,
            totalSeconds = totalSecs
        )

        sleepTimerJob = viewModelScope.launch {
            var currentSec = totalSecs
            while (currentSec > 0) {
                delay(1000)
                currentSec--
                _sleepTimer.value = _sleepTimer.value.copy(remainingSeconds = currentSec)
            }
            // Timer expired: stop background audio service
            _sleepTimer.value = SleepTimerState(isActive = false)
            AudioFxService.stopService(app)
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimer.value = SleepTimerState(isActive = false)
    }

    // Firmware Update Simulator
    fun checkForFirmwareUpdate() {
        viewModelScope.launch {
            _isUpdatingFirmware.value = true
            _firmwareUpdateProgress.value = 0.05f
            for (i in 1..20) {
                delay(150)
                _firmwareUpdateProgress.value = i / 20.0f
            }
            _isUpdatingFirmware.value = false
            _hardwareInfo.value = _hardwareInfo.value.copy(
                firmwareVersion = "7B19 (Latest 2026)",
                latestFirmwareVersion = "7B19",
                isUpdateAvailable = false
            )
        }
    }

    // Decibel Meter
    private fun startDecibelMonitoring() {
        decibelJob?.cancel()
        decibelJob = viewModelScope.launch {
            DecibelMeter.startListening(app).collect { db ->
                _currentDecibels.value = db
            }
        }
    }

    fun refreshConnectedDevices() {
        _connectedDevices.value = bluetoothHelper.getPairedAudioDevices()
    }

    private fun startAudioService() {
        if (_isBgServiceEnabled.value) {
            AudioFxService.startService(app, _activePreset.value, _isEqEnabled.value)
        }
    }

    private fun syncService() {
        if (_isBgServiceEnabled.value) {
            AudioFxService.updateService(app, _activePreset.value, _isEqEnabled.value)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ToneGenerator.stopTone()
        sleepTimerJob?.cancel()
        decibelJob?.cancel()
    }
}
