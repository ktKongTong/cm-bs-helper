package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StepsProgressBar(modifier: Modifier = Modifier, numberOfSteps: Int, currentStep: Int) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..numberOfSteps) {
            Step(
                modifier = Modifier.weight(1F),
                isCompete = step < currentStep,
                isCurrent = step == currentStep,
                isFirstStep = step == 1,
                isLastStep = currentStep >= numberOfSteps
            )
        }
    }
}

@Composable
fun Step(
    modifier: Modifier = Modifier,
    isCompete: Boolean,
    isCurrent: Boolean,
    isFirstStep:Boolean,
    isLastStep:Boolean
) {
    val color = if (isCompete || isCurrent&&!isFirstStep) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary
    val innerCircleColor = if (isCompete ||isLastStep) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary
    Box(modifier = modifier) {

        Divider(
            modifier = Modifier.align(Alignment.CenterStart),
            color = color,
            thickness = 2.dp
        )
        if(isFirstStep) {
            Canvas(modifier = Modifier
                .size(15.dp)
                .align(Alignment.CenterStart)
                .border(
                    shape = CircleShape,
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                ),
                onDraw = {
                    drawCircle(color = innerCircleColor)
                }
            )
        }
        Canvas(modifier = Modifier
            .size(15.dp)
            .align(Alignment.CenterEnd)
            .border(
                shape = CircleShape,
                width = 2.dp,
                color = color
            ),
            onDraw = {
                drawCircle(color = innerCircleColor)
            }
        )
    }
}

//@Preview
@Composable
fun StepsProgressBarPreview() {
    val currentStep = remember { mutableStateOf(1) }
    StepsProgressBar(modifier = Modifier.fillMaxWidth(), numberOfSteps = 5, currentStep = currentStep.value)
}