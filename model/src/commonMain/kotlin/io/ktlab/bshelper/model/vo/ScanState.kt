package io.ktlab.bshelper.model.vo

import io.ktlab.bshelper.model.scanner.ScannerException
import kotlinx.coroutines.flow.MutableStateFlow

enum class GlobalScanStateEnum(val human: String) {
    NOT_START("not start"),
    SCANNING_PLAYLISTS("scanning playlist"),
    SCAN_PLAYLISTS_COMPLETE("playlist scanning"),
    SCANNING_MAPS("map scanning"),
    SCAN_COMPLETE("complete"),
    SCAN_ERROR("error"),
}

enum class ScanStateMachineStateEnum {
    INIT,
    SCANNING_PLAYLISTS,
    WAITING_TRIGGER_SCAN_MAPS,
    SCANNING_MAPS,
    SCAN_COMPLETE,
    SCAN_ERROR,
}

class ScanStateMachine

enum class ScanStateEnum {
    NOT_START,
    SCANNING,
    SCAN_COMPLETE,
    SCAN_ERROR,
}

data class ScanState(
    val state: GlobalScanStateEnum,
    val playlistStates: List<MutableStateFlow<PlaylistScanState>>,
    val error: Error? = null,
) {
    companion object {
        fun getDefaultInstance(): ScanState {
            return ScanState(
                state = GlobalScanStateEnum.NOT_START,
                playlistStates = mutableListOf(),
            )
        }
    }
}

data class MapScanState(
    val state: ScanStateEnum,
    val mapName: String = "",
    val mapPath: String = "",
    val mapId: String = "",
    val mapVersion: String = "",
    val mapHash: String = "",
    val error: Error? = null,
) {
    companion object {
        fun getDefaultInstance(): MapScanState {
            return MapScanState(
                state = ScanStateEnum.NOT_START,
            )
        }
    }
}

enum class PlaylistScanStateEnum {
    UNSELECTED,
    SELECTED_BUT_NOT_START,
    SCANNING,
    SCAN_COMPLETE,
    SCAN_ERROR,
}

data class PlaylistScanState(
    var state: PlaylistScanStateEnum,
    var playlistName: String = "",
    var playlistPath: String = "",
    var playlistId: String = "",
    var possibleMapAmount: Int = 0,
    var mapScanStates: List<MapScanState>,
    val error: Error? = null,
) {
    companion object {
        fun getDefaultInstance(): PlaylistScanState {
            return PlaylistScanState(
                state = PlaylistScanStateEnum.UNSELECTED,
                mapScanStates = mutableListOf(),
            )
        }
    }
}

enum class ScanStateEventEnum(val human: String) {
    NOT_START("not start"),
    SCANNING("scanning"),
    SCAN_ERROR("error"),
    SCAN_COMPLETE("complete"),
}
// or 支持重新扫描单个子歌单？
// 扫描改为一次性扫描，不使用并行扫描

data class MapScanError(
    val title: String = "",
    val description: String = "",
)

data class PlaylistScanStateV2(
    val playlistName: String = "",
    val playlistPath: String = "",
    val currentMapDir: String = "",
    val fileAmount: Int = 0,
    val scannedFileAmount: Int = 0,
    val errorStates: List<ScannerException>,
)

data class ScanStateV2(
    val state: ScanStateEventEnum = ScanStateEventEnum.NOT_START,
    val currentPlaylistDir: String = "",
    val currentMapDir: String = "",
    // subdir
    val totalDirCount: Int = 0,
    val scannedDirCount: Int = 0,
    val scannedMapCount: Int = 0,
    val message: String = "",
    val playlistScanList: List<MutableStateFlow<PlaylistScanStateV2>> = mutableListOf(),
    val errorStates: List<ScannerException> = emptyList(),
) {
    companion object {
        fun getDefaultInstance(): ScanStateV2 {
            return ScanStateV2(
                state = ScanStateEventEnum.NOT_START,
            )
        }
    }
}
