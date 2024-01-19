package io.ktlab.bshelper.ui.components.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.ECharacteristic.*

@Composable
fun BSCharIcon(
    characteristic:ECharacteristic,
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    when (characteristic) {
        Lawless -> BSCharLawlessIcon(modifier, tint)
        OneSaber -> BSCharOneSaberIcon(modifier, tint)
        NoArrows -> BSCharNoArrowsIcon(modifier, tint)
        _90_DEGREE -> BSChar90DegreeIcon(modifier, tint)
        _360_DEGREE -> BSChar360DegreeIcon(modifier, tint)
        Lightshow -> BSCharLightshowIcon(modifier, tint)
        Standard -> BSCharStandardIcon(modifier, tint)
        Legacy -> BSCharLegacyIcon(modifier, tint)
    }
}


@Composable
fun BSCharLegacyIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_char_legacy),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}

@Composable
fun BSCharStandardIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_char_standard),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}

@Composable
fun BSCharLawlessIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_char_lawless),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}
@Composable
fun BSCharOneSaberIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_char_onesaber),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}

@Composable
fun BSCharNoArrowsIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_char_noarrows),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}

@Composable
fun BSChar90DegreeIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_char_90degree),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}

@Composable
fun BSChar360DegreeIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_char_360degree),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}

@Composable
fun BSCharLightshowIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_char_lightshow),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}



@Composable
fun BSBPMIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_bpm_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bpm icon",
        tint = tint,
    )
}

@Composable
fun BSDurationIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.HourglassBottom,
        modifier = modifier.size(20.dp),
        contentDescription = "bs duration icon",
        tint = tint,
    )
}
@Composable
fun BSIDIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.Key,
        modifier = modifier.size(20.dp),
        contentDescription = "bs id icon",
        tint = tint,
    )
}

@Composable
fun BSNPSIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_nps_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "bs nps icon",
        tint = tint,
    )
}

@Composable
fun BSNJSIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        Icons.Rounded.Speed,
        modifier = modifier.size(20.dp),
        contentDescription = "bs nps icon",
        tint = tint,
    )
}
@Composable
fun BSNoteIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_notes_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "bs notes icon",
        tint = tint,
    )
}

@Composable
fun BSObstacleIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_walls_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "bs obstacle icon",
        tint = tint,
    )
}

@Composable
fun BSLightEventIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.spotlight_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "bs light event icon",
        tint = tint,
    )
}

@Composable
fun BSBombIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_bomb_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "bs bomb icon",
        tint = tint,
    )
}

@Composable
fun BSRatingIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.Star,
        modifier = modifier.size(20.dp),
        contentDescription = "bs rating icon",
        tint = tint,
    )
}

@Composable
fun BSThumbUpIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.ThumbUp,
        modifier = modifier.size(20.dp),
        contentDescription = "bs download icon",
        tint = tint,
    )
}

@Composable
fun BSThumbDownIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.ThumbDown,
        modifier = modifier.size(20.dp),
        contentDescription = "bs download icon",
        tint = tint,
    )
}

@Composable
fun BSDateIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.Schedule,
        modifier = modifier.size(20.dp),
        contentDescription = "date icon",
        tint = tint,
    )
}

@Composable
fun BSMEIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_me_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "mapping extension icon",
        tint = tint,
    )
}

@Composable
fun BSNEIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.bs_ne_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "noodle extension icon",
        tint = tint,
    )
}

@Composable
fun BSAIIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.SmartToy,
        modifier = modifier.size(20.dp),
        contentDescription = "noodle extension icon",
        tint = tint,
    )
}

@Composable
fun BSRankedIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.EmojiEvents,
        modifier = modifier.size(20.dp),
        contentDescription = "ranked icon",
        tint = tint,
    )
}

@Composable
fun BSCuratedIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.Verified,
        modifier = modifier.size(20.dp),
        contentDescription = "curated icon",
        tint = tint,
    )
}

@Composable
fun BSCinemaIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.cinema_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "bs cinema icon",
        tint = tint,
    )
}

@Composable
fun BSChromaIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        painter = painterResource(MR.images.spotlight_icon),
        modifier = modifier.size(20.dp),
        contentDescription = "bs chroma icon",
        tint = tint,
    )
}

@Composable
fun BSMapAmountIcon(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current,
) {
    Icon(
        imageVector = Icons.Rounded.Map,
        modifier = modifier.size(20.dp),
        contentDescription = "map count icon",
        tint = tint,
    )
}
