package tw.wesely.scoreranking

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tw.wesely.scoreranking.ui.theme.ScoreRankingTheme

class PatternLockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoreRankingTheme {
                PatternLockScreen()
            }
        }
    }
}

@Composable
fun PatternLockScreen() {
    val selectedNumbers = remember { mutableStateOf("") }
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            SelectedNumbersDisplay(selectedNumbers = selectedNumbers.value)
            PatternLock { list ->
                vibrate(context = context)
                selectedNumbers.value = list.toString()
            }
        }
    }
}

@Composable
fun SelectedNumbersDisplay(selectedNumbers: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 50.dp),
        textAlign = TextAlign.Center,
        fontSize = 30.sp,
        text = selectedNumbers
    )
}


@Composable
fun PatternLock(modifier: Modifier = Modifier, onUpdates: (List<Int>?) -> Unit) {
    val pattern = mutableListOf<Int>()
    val numbersMap: MutableMap<Offset, Int> = mutableMapOf()

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    // if gesture close to a point, it would be saved as a segmentation
    val segmentations = remember { mutableStateListOf<Pair<Offset, Offset>>() }
    // startOffset would be the latest segmentation's start point
    var startOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    // currentOffset would always be the current touch point
    var currentOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    // the 9 dot's position
    val interestedPoints = remember { mutableSetOf<Offset>() }
    // a pattern contains unique dots, so we needs to save the used dots
    val usedPoints = remember { mutableSetOf<Offset>() }
    // the distance that we consider this dot is selected
    val threshold = 60f

    fun handleDragStart(touch: Offset) {
        startOffset = touch
        currentOffset = touch
        offsetX = 0f
        offsetY = 0f
        segmentations.clear()
        usedPoints.clear()
        pattern.clear()
    }

    fun handleDragChange(change: PointerInputChange, dragAmount: Offset) {
        change.consume()
        offsetX += dragAmount.x
        offsetY += dragAmount.y
        currentOffset += Offset(dragAmount.x, dragAmount.y)

        val validPoints = interestedPoints - usedPoints
        validPoints.forEach { interestedPoint ->
            if ((interestedPoint - currentOffset).getDistance() < threshold) {
                if (startOffset in interestedPoints || currentOffset in interestedPoints) {
                    segmentations.add(interestedPoint to startOffset)
                }
                numbersMap[interestedPoint]?.let { selectedInt ->
                    pattern.add(selectedInt)
                    onUpdates(pattern)
                }
                usedPoints.add(interestedPoint)
                startOffset = interestedPoint
                offsetX = 0f
                offsetY = 0f
            }
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = ::handleDragStart,
                onDrag = ::handleDragChange,
            )
        }) {

        // calculate a square pad in the middle of the canvas, no matter it's vertical or horizontal
        val padWidth = size.width
        val padHeight = size.height
        val edgeLength = padHeight.coerceAtMost(padWidth)
        val top = (padHeight - edgeLength) / 2
        val start = (padWidth - edgeLength) / 2

        /** This is the calculated pad **/
        drawRect(
            topLeft = Offset(start, top),
            size = Size(edgeLength, edgeLength),
            color = Color(0x33FF22FF)
        )

        // If there are any stored(selected) pattern, draw them with solid color
        segmentations.forEach { (start, end) ->
            drawLine(
                color = Color(0xFF000000),
                strokeWidth = 10f,
                start = start,
                end = end,
            )
        }

        // The current segment is still dragging
        // it it's not starts with any interestedPoints, we ignore it
        if (segmentations.size in 0..7 && startOffset in interestedPoints) {
            Log.d("pattern", "size=${segmentations.size}")
            drawLine(
                color = Color(0x33000000),
                strokeWidth = 10f,
                start = startOffset,
                end = currentOffset,
            )
        }

        // calculate the 9 dot's position.
        // doesn't has to be completed here
        if (interestedPoints.isEmpty()) {
            var curr = 1
            for (y in 1..3) {
                for (x in 1..3) {
                    val axisX = (edgeLength / 4) * x + start
                    val axisY = (edgeLength / 4) * y + top
                    val off = Offset(axisX, axisY)
                    interestedPoints.add(off)
                    numbersMap[off] = curr
                    curr++
                }
            }
        }

        // For these part, they are the points that you can still use
        (interestedPoints - usedPoints ).forEach {(axisX, axisY) ->
            drawCircle(
                color = Color(0x44CC22FF),
                center = Offset(axisX, axisY),
                radius = threshold
            )
        }

        // draw a solid center for all the dots
        interestedPoints.forEach { (axisX, axisY) ->
            drawCircle(
                color = Color(0xFF441199),
                center = Offset(axisX, axisY),
                radius = edgeLength / 60
            )
        }

    }

}


fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        val vibrationEffect = VibrationEffect.createOneShot(
            50, // Milliseconds
            VibrationEffect.DEFAULT_AMPLITUDE // Amplitude
        )
        vibrator.vibrate(vibrationEffect)
    }
}

@Preview(showBackground = true)
@Composable
fun PatternLockPreview() {
    ScoreRankingTheme {
        PatternLockScreen()
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun PatternLockPreviewForAutomotive1024p() {
    ScoreRankingTheme {
        PatternLockScreen()
    }
}
