import com.soywiz.klock.TimeSpan
import com.soywiz.korge.component.Component
import com.soywiz.korge.view.*
import com.soywiz.korge.view.camera.CameraContainer
import com.soywiz.korim.color.Colors
import com.soywiz.korio.lang.Cancellable

abstract class GameObject(override val view: Stage) : Component {

    var sprite = Sprite()
    val bounds = FixedSizeContainer()

    init {
        privateInit()
    }

    open fun init() = Unit

    private fun privateInit() {

        var debugSquare: SolidRect? = null

        bounds.addProp("GameObject", this)
        sprite.addProp("GameObject", this)
        bounds.onCollision(
            callback = { privateCollisionEnter(it) },
            filter = { it != view && it != debugSquare && it != sprite })
        bounds.onCollisionExit(
            callback = { privateCollisionExit(it) },
            filter = { it != view && it != debugSquare && it != sprite })
        view.onNextFrame { view.addUpdater { privateUpdate(it) } }

        init()

//        debugSquare = view.solidRect(bounds.width, bounds.height, Colors.RED).apply {
//            addUpdater {
//                x = this@GameObject.bounds.x
//                y = this@GameObject.bounds.y
//            }
//        }

        view.addChild(bounds)
        view.addChild(sprite)
    }

    open fun update(dt: TimeSpan) = Unit

    private fun privateUpdate(dt: TimeSpan) {
        sprite.apply {
            x = bounds.x + (bounds.width / 2)
            y = bounds.y + (bounds.height / 2)
        }
        update(dt)
    }

    open fun onCollisionEnter(other: View, otherGameObject: GameObject?) = Unit

    private fun privateCollisionEnter(other: View) {
        val otherGameObject: GameObject? = other.props["GameObject"] as GameObject?
        onCollisionEnter(other, otherGameObject)
    }

    open fun onCollisionExit(other: View, otherGameObject: GameObject?) = Unit

    private fun privateCollisionExit(other: View) {
        val otherGameObject: GameObject? = other.props["GameObject"] as GameObject?
        onCollisionExit(other, otherGameObject)
    }
}

/**
 * Adds a component to [this] that checks each frame for collisions against descendant views of [root] that matches the [filter],
 * when a collision is found, it remembers it and the next time the collision does not occur, [callback] is executed with this view as receiver,
 * and the collision target as first parameter. if no [root] is provided, it computes the root of the view [this] each frame, you can specify a collision [kind]
 */
fun View.onCollisionExit(
    filter: (View) -> Boolean = { true },
    root: View? = null,
    kind: CollisionKind = CollisionKind.GLOBAL_RECT,
    callback: View.(View) -> Unit
): Cancellable {
    val collisionState = mutableMapOf<View, Boolean>()
    return addUpdater {
        (root ?: this.root).foreachDescendant {
            if (this != it && filter(it)) {
                if (this.collidesWith(it, kind)) {
                    collisionState[it] = true
                } else if (collisionState[it] == true) {
                    callback(this, it)
                    collisionState[it] = false
                }
            }
        }
    }
}

fun View.onCollisionShapeExit(filter: (View) -> Boolean = { true }, root: View? = null, callback: View.(View) -> Unit): Cancellable {
    return onCollisionExit(filter, root, kind = CollisionKind.SHAPE, callback = callback)
}

fun List<View>.onCollisionExit(filter: (View) -> Boolean = { true }, root: View? = null, kind: CollisionKind = CollisionKind.GLOBAL_RECT, callback: View.(View) -> Unit): Cancellable =
    Cancellable(this.map { it.onCollisionExit(filter, root, kind, callback) })

/**
 * Adds a component to [this] that checks collisions each frame of descendants views of [root] matching [filterSrc], matching against descendant views matching [filterDst].
 * When a collision is found, it remembers it and the next time the collision does not occur, [callback] is called. It returns a [Cancellable] to remove the component.
 */
fun View.onDescendantCollisionExit(root: View = this, filterSrc: (View) -> Boolean = { true }, filterDst: (View) -> Boolean = { true }, kind: CollisionKind = CollisionKind.GLOBAL_RECT, callback: View.(View) -> Unit): Cancellable {
    val collisionState = mutableMapOf<Pair<View, View>, Boolean>()
    return addUpdater {
        root.foreachDescendant { src ->
            if (filterSrc(src)) {
                root.foreachDescendant { dst ->
                    if (src !== dst && filterDst(dst)) {
                        if (src.collidesWith(dst, kind)) {
                            println("collide")
                            collisionState[Pair(src, dst)] = true
                        } else if (collisionState[Pair(src, dst)] == true) {
                            callback(src, dst)
                            collisionState[Pair(src, dst)] = false
                        }
                    }
                }
            }
        }
    }
}

var View.bottom
    get() = this.y + this.height
    set(b) {
        this.y = b - this.height
    }
var View.top
    get() = this.y
    set(y) {
        this.y = y
    }
var View.right
    get() = this.x + this.width
    set(r) {
        this.x = r - this.width
    }
var View.left
    get() = this.x
    set(x) {
        this.x = x
    }