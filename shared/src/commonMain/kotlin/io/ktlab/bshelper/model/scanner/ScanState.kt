package io.ktlab.bshelper.model.scanner

import kotlinx.coroutines.flow.MutableStateFlow

enum class ScanStateEventEnum(val human: String) {
    NOT_START("not start"),
    SCANNING("scanning"),
    SCAN_ERROR("error"),
    SCAN_COMPLETE("complete"),
}

// or 支持重新扫描单个子歌单？
// 扫描改为一次性扫描，不使用并行扫描

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
