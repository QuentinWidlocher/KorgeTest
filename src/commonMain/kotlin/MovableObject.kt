import com.soywiz.klock.TimeSpan
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Stage
import com.soywiz.korma.geom.Point

abstract class MovableObject(view: Stage): GameObject(view) {
    var speed = 0.0

    private var oldPos = newPos
    private val newPos get() = Point(sprite.globalX, sprite.globalY)

    val velocity get() = newPos - oldPos

    override fun update(dt: TimeSpan) {
        oldPos = newPos
    }
}