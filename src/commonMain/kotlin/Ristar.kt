import RistarAnimation.Walking
import com.soywiz.klock.seconds
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korge.view.Stage
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.async
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.GlobalScope

class Ristar(parent: Stage): MovableObject<Sprite>(parent) {

    private var animations: HashMap<RistarAnimation, SpriteAnimation> = HashMap()

    override fun init() {
        sprite = Sprite()
        GlobalScope.launch(::initSprite)
    }

    private suspend fun initSprite() {
        animations = HashMap()

        fun loadSpriteAsync(sprite: String) = GlobalScope.async { resourcesVfs[sprite].readBitmap() }

        loadSpriteAsync("RistarWalking.png").await().also {
            animations[Walking] = SpriteAnimation(it,44, 40, 0, 0, 8, 1)
        }

        loadSpriteAsync("RistarIdle.png").await().also {
            animations[Walking] = SpriteAnimation(it,26, 39, 0, 0, 1, 1)
        }

        sprite.playAnimationLooped(animations[Walking], 0.15.seconds)
    }
}