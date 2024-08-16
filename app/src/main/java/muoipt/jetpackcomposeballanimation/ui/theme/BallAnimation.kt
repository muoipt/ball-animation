package muoipt.jetpackcomposeballanimation.ui.theme

import android.graphics.Point
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.random.Random


@Composable
fun BallAnimation(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val ballState = remember { BallState() }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                ballState.update(it)
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .onSizeChanged {
                with(density) {
                    ballState.updateSize(
                        it.width.toDp().value.toInt(), it.height.toDp().value.toInt()
                    )
                }
            }) {
            Ball(ballState.ballData)
        }
    }
}

@Composable
fun Ball(data: BallData) {
    val randomColor = remember {
        Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }
    Box(
        Modifier
            .offset(data.location.x.dp, data.location.y.dp)
            .size(data.size.dp)
            .clip(CircleShape)
            .background(randomColor)
    )
}

class BallState(
) {
    var width = 0
    var height = 0
    val ballData = BallData()

    private var prevTime = 0L

    fun update(time: Long) {

        if (prevTime == 0L) {
            prevTime = time
        }

        val delta = time - prevTime
        prevTime = time
        val deltaRatio = delta / 16f

        updateBall(deltaRatio)
    }

    fun updateSize(width: Int, height: Int) {
        this.width = width
        this.height = height

        // Pick a random start location for the ball
        ballData.location = Point(
            Random.nextInt(0, width - ballData.size), Random.nextInt(0, height - ballData.size)
        )
    }

    private fun updateBall(deltaRatio: Float) {
        updateBallLocation(deltaRatio)
        updateBallVelocity()
    }

    private fun updateBallLocation(deltaRatio: Float) {
        var x = (ballData.location.x + deltaRatio * ballData.velocity.x).toInt()
        if (x < 0) {
            x = 0
        } else if (x > width - ballData.size) {
            x = width - ballData.size
        }

        var y = (ballData.location.y + deltaRatio * ballData.velocity.y).toInt()
        if (y < 0) {
            y = 0
        } else if (y > height - ballData.size) {
            y = height - ballData.size
        }

        ballData.location = Point(x, y)
    }

    private fun updateBallVelocity() {
        val location = ballData.location
        val velocity = ballData.velocity
        val size = ballData.size

        // Change direction the ball hit the wall
        when {
            location.x >= width - size || location.x <= 0 -> {
                ballData.velocity = Point(-velocity.x, velocity.y)
            }

            location.y >= height - size || location.y <= 0 -> {
                ballData.velocity = Point(velocity.x, -velocity.y)
            }
        }
    }
}

class BallData {
    var location by mutableStateOf(Point(0, 0))
    var velocity: Point = Point(BALL_VELOCITY_X, BALL_VELOCITY_Y)
    var size: Int = BALL_SIZE
}

private const val BALL_VELOCITY_X = 2
private const val BALL_VELOCITY_Y = 4
private const val BALL_SIZE = 16