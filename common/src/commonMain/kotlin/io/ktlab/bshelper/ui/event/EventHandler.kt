package io.ktlab.bshelper.ui.event

import io.ktlab.bshelper.viewmodel.*

class EventHandler(
    private val globalViewModel: GlobalViewModel,
    private val beatSaverViewModel: BeatSaverViewModel,
    private val homeViewModel: HomeViewModel,
    private val toolboxViewModel: ToolboxViewModel,
) {
    fun dispatchEvent(event: UIEvent) {
        when (event) {
            is GlobalUIEvent -> {
                globalViewModel.dispatchUiEvents(event)
            }
            is HomeUIEvent -> {
                homeViewModel.dispatchUiEvents(event)
            }
            is BeatSaverUIEvent -> {
                beatSaverViewModel.dispatchUiEvents(event)
            }
            is ToolboxUIEvent -> {
                toolboxViewModel.dispatchUiEvents(event)
            }
        }
    }
}
