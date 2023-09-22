package io.ktlab.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v3.html#color-notes">color notes</a>
 */
@Serializable
data class ColorNotes(
    @SerialName("b") val beat: Double,
//    An integer number, from 0 to 3,
//    which represents the column where this note is located.
//    The far left column is located at index 0, and increases to the far right column located at index 3.
    @SerialName("x") val x: Int,
//    An integer number, from 0 to 2,
//    which represents the layer where this note is located.
//    The bottommost layer is located at layer 0, and increases to the topmost layer located at index 2.
    @SerialName("y") val y: Int,
    // 0 red or 1 blue
    @SerialName("c") val color: Int,
    // 0 up,1down,2 left,3 right,4 upLeft,5 upright,6 downLeft,7 downright 8 dot note
    @SerialName("d") val direction: Int,
//  An integer number which represents the additional counter-clockwise angle offset applied to the note's cut direction in degrees.
//  This has no effect on angles created due to snapping (e.g. dot stack, slanted windows).
    @SerialName("a") val angleOffset: Int,
)