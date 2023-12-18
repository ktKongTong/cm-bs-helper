package io.ktlab.bshelper.ui.event

import io.ktlab.bshelper.ui.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.ui.viewmodel.BeatSaverViewModel
import io.ktlab.bshelper.ui.viewmodel.GlobalUIEvent
import io.ktlab.bshelper.ui.viewmodel.GlobalViewModel
import io.ktlab.bshelper.ui.viewmodel.HomeUIEvent
import io.ktlab.bshelper.ui.viewmodel.HomeViewModel
import io.ktlab.bshelper.ui.viewmodel.ToolboxUIEvent
import io.ktlab.bshelper.ui.viewmodel.ToolboxViewModel

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
