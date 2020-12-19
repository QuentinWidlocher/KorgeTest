import RistarAnimation.*
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korev.Key
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.async
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.VectorPath
import kotlinx.coroutines.GlobalScope
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Ristar(parent: Stage): MovableObject<Sprite>(parent) {

    private var animations: HashMap<RistarAnimation, SpriteAnimation> = HashMap()

    private var isGrounded = false
    private val acceleration = Point(0,0)

    private val jumpForce = 3.5
    private val accelerationSpeed = 0.15
    private val decelerationSpeed = 0.3
    private val maxSpeed = 3.0

    private var currentAnimation = Idle

    override fun init() {
        super.init()
        sprite = Sprite().apply {
            anchorX = width/2
            anchorY = height/2
        }

        GlobalScope.launch(::initSprite)

        speed = 1.0
    }

    override fun update(dt: TimeSpan) {
        super.update(dt)
        move()
        draw()
    }

    override fun collision(otherSprite: View, other: Any?) {
        super.collision(otherSprite, other)

        isGrounded = true
        sprite.y = otherSprite.y - otherSprite.height
    }

    private fun draw() {

        if (isGrounded) {
            if (velocity.x > 0) {
                sprite.scaleX = abs(sprite.scaleX)
                animate(Walking, true)
            } else if (velocity.x < 0) {
                sprite.scaleX = -abs(sprite.scaleX)
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
                isGrounded = false
            }
        } else {
            acceleration.y += Constants.gravity
        }

        sprite.x += acceleration.x
        sprite.y += acceleration.y
    }

    private fun animate(anim: RistarAnimation, loop: Boolean = false) {
        if (!loop && currentAnimation != anim) {
            println("${currentAnimation.name} -> ${anim.name}")
            sprite.playAnimation(1, animations[anim], 166.milliseconds)
            sprite.onAnimationCompleted {
                println("${anim.name} finished")
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
            loadSpriteAsync(anim.file).await().also {
                animations[anim] = SpriteAnimation(it, anim.width, anim.height, anim.marginTop, anim.marginLeft, anim.columns, anim.rows)
            }
        }

        sprite.playAnimationLooped(animations[Idle], 0.15.seconds)
    }
}