package io.ktlab.bshelper.model.vo

import kotlinx.coroutines.flow.MutableStateFlow

enum class GlobalScanStateEnum(val human:String) {
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
class ScanStateMachine {

}

enum class ScanStateEnum {
    NOT_START,
    SCANNING,
    SCAN_COMPLETE,
    SCAN_ERROR,
}

data class ScanState(
    val state: GlobalScanStateEnum,
    val playlistStates : List<MutableStateFlow<PlaylistScanState>>,
    val error: Error? = null,
){
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
){
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
){
    companion object {
        fun getDefaultInstance(): PlaylistScanState {
            return PlaylistScanState(
                state = PlaylistScanStateEnum.UNSELECTED,
                mapScanStates = mutableListOf(),
            )
        }
    }
}
