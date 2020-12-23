import RistarAnimation.*
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.milliseconds
import com.soywiz.korev.Key
import com.soywiz.korge.scene.SceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korge.view.camera.CameraContainer
import com.soywiz.korge.view.camera.CameraContainerOld
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.async
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.GlobalScope
import kotlin.collections.set
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Ristar(parent: Stage) : MovableObject(parent) {

    private var animations: HashMap<RistarAnimation, SpriteAnimation> = HashMap()

    private var isGrounded = false
    private val acceleration = Point(0, 0)

    private val jumpForce = 4.5
    private val accelerationSpeed = 0.15
    private val decelerationSpeed = 0.3
    private val maxSpeed = 3.0

    private var currentAnimation = Falling

    override fun init() {
        super.init()

        sprite.apply {
            anchorX = width / 2
            anchorY = height / 2
        }

        bounds.apply {
            width = 20.0
            height = 40.0
        }

        GlobalScope.launch(::initSprite)
    }

    override fun update(dt: TimeSpan) {
        super.update(dt)
        move()
        draw()
    }

    override fun onCollisionEnter(other: View, otherGameObject: GameObject?) {
        super.onCollisionEnter(other, otherGameObject)

        val otherIsHigherThanFeet = other.top < bounds.bottom - velocity.y
        val otherIsUp = other.bottom <= bounds.top - velocity.y
        val otherIsDown = other.top >= bounds.bottom - velocity.y
        val otherIsLeft = bounds.left <= other.right && bounds.right > other.right
        val otherIsRight = bounds.right >= other.left && bounds.left < other.left

        if (otherIsUp) {
            // Upward collision
            // Stop going further up
            acceleration.y = 0.0
            bounds.top = other.bottom
        } else if (otherIsHigherThanFeet && (otherIsRight || otherIsLeft)) {
            // X axis collisions
            acceleration.x = 0.0
            if (otherIsLeft) {
                bounds.left = other.right
            } else if (otherIsRight) {
                bounds.right = other.left
            }
        } else if (otherIsDown) {
            // Bottom collision
            // Stop going further down
            acceleration.y = 0.0
            bounds.bottom = other.top
            isGrounded = true
        }

    }

    override fun onCollisionExit(other: View, otherGameObject: GameObject?) {
        super.onCollisionExit(other, otherGameObject)

        if (other.top >= bounds.bottom - velocity.y) {
            isGrounded = false
        }
    }

    private fun draw() {

        if (isGrounded) {
            if (abs(velocity.x) > 0) {
                animate(Walking, true)
            } else {
                animate(Idle, true)
            }
        } else {
            if (velocity.y < 0) {
                animate(Jumping)
            } else {
                animate(Falling)
            }
        }

        if (velocity.x > 0) {
            sprite.scaleX = abs(sprite.scaleX)
        } else if (velocity.x < 0) {
            sprite.scaleX = -abs(sprite.scaleX)
        }
    }

    private fun move() {
        acceleration.x = when {
            view.views.input.keys[Key.RIGHT]    -> min(acceleration.x + accelerationSpeed, maxSpeed)
            view.views.input.keys[Key.LEFT]     -> max(acceleration.x - accelerationSpeed, -maxSpeed)
            acceleration.x > decelerationSpeed  -> acceleration.x - decelerationSpeed
            acceleration.x < -decelerationSpeed -> acceleration.x + decelerationSpeed
            else                                -> 0.0
        }

        if (isGrounded) {
            acceleration.y = 0.0
            if (view.views.input.keys.justPressed(Key.SPACE)) {
                acceleration.y -= jumpForce
            }
        } else {
            acceleration.y += Constants.gravity
        }

        bounds.x += acceleration.x
        bounds.y += acceleration.y
    }

    private fun animate(anim: RistarAnimation, loop: Boolean = false) {
        if (!loop && currentAnimation != anim) {
            sprite.playAnimation(1, animations[anim], 166.milliseconds)
            sprite.onAnimationCompleted {
                sprite.setFrame(it.size + 1)
            }
        } else if (loop) {
            sprite.playAnimationLooped(animations[anim], 166.milliseconds)
        }

        currentAnimation = anim
    }

    private suspend fun initSprite() {
        animations = HashMap()

        fun loadSpriteAsync(sprite: String) = GlobalScope.async { resourcesVfs[sprite].readBitmap() }

        enumValues<RistarAnimation>().forEach { anim ->
            with(anim) {
                loadSpriteAsync(file).await().also { bitmap ->
                    animations[anim] = SpriteAnimation(bitmap, width, height, marginTop, marginLeft, columns, rows)
                }
            }
        }

        sprite.playAnimation(1, animations[Falling], 166.milliseconds)
    }
}

fun Stage.ristar(x: Double = 0.0, y: Double = 0.0) = Ristar(this).apply { bounds.xy(x, y) }