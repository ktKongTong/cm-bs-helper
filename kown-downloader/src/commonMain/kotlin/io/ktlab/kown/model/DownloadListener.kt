package io.ktlab.kown.model

import io.ktlab.kown.model.DownloadTaskBO

data class DownloadListener(
    var onStart: (DownloadTaskBO) -> Unit = {},
    var onProgress: (progress:Float) -> Unit = {},
    var onCompleted: (DownloadTaskBO) -> Unit = {},
    var onPaused: (DownloadTaskBO) -> Unit = {},
    var onResumed: (DownloadTaskBO) -> Unit = {},
    var onFailed: (DownloadTaskBO) -> Unit = {},
    var onCancelled: (DownloadTaskBO) -> Unit = {},
    var onError: (DownloadTaskBO, Exception) -> Unit = { _, _ ->}
)