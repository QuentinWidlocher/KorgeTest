import com.soywiz.klock.TimeSpan
import com.soywiz.korge.view.Stage
import com.soywiz.korma.geom.Point

abstract class MovableObject(view: Stage) : GameObject(view) {
    private var oldPos = newPos
    private val newPos get() = Point(bounds.globalX, bounds.globalY)

    val velocity get() = newPos - oldPos

    override fun update(dt: TimeSpan) {
        oldPos = newPos
    }
}