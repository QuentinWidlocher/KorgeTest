import com.soywiz.klock.TimeSpan
import com.soywiz.korge.component.Component
import com.soywiz.korge.view.*

abstract class GameObject<T : RectBase>(override val view: Stage): Component {

    var sprite: T = RectBase() as T

    open val touchScreenVerticalEdge: Boolean
        get() = touchScreenTopEdge || touchScreenBottomEdge

    open val touchScreenTopEdge: Boolean
        get() = sprite.globalBounds.top <= view.globalBounds.top

    open val touchScreenBottomEdge: Boolean
        get() = sprite.globalBounds.bottom >= view.globalBounds.bottom

    open val touchScreenHorizontalEdge: Boolean
        get() = touchScreenRightEdge || touchScreenLeftEdge

    open val touchScreenRightEdge: Boolean
        get() = sprite.globalBounds.right >= view.globalBounds.right

    open val touchScreenLeftEdge: Boolean
        get() = sprite.globalBounds.left <= view.globalBounds.left

    init {
        privateInit()
    }

    open fun init() {}

    private fun privateInit() {
        init()

        sprite.addProp("parent", this)
        sprite.onCollision(callback = {privateCollision(it)}, filter = { it != view })
        view.addChild(sprite)
        view.onNextFrame { sprite.addUpdater{ privateUpdate(it) } }
    }

    open fun update(dt: TimeSpan) {}

    private fun privateUpdate(dt: TimeSpan) {
        update(dt)
    }

    open fun collision(otherSprite: View, other: Any?) {}

    private fun privateCollision(otherSprite: View) {
        val other = otherSprite.props["parent"]
        collision(otherSprite, other)
    }
}