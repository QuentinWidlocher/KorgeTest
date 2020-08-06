import RistarAnimation.Walking
import com.soywiz.klock.seconds
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korge.view.Stage
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.GlobalScope

class Ristar(parent: Stage): MovableObject<Sprite>(parent) {

    private val ANIMATION_FILES: HashMap<RistarAnimation, String> = HashMap()

    private val animations: HashMap<RistarAnimation, SpriteAnimation> = HashMap()

    override fun init() {
        sprite = Sprite();
        ANIMATION_FILES.apply {
            this[Walking] = "RistarWalking.png";
        }
        GlobalScope.launch(::initSprite);
    }

    private suspend fun initSprite() {

        for (anim in RistarAnimation.values()) {
            val sprite = resourcesVfs[ ANIMATION_FILES[anim]!! ].readBitmap()
            animations[anim] = SpriteAnimation(
                    spriteMap = sprite,
                    spriteWidth = 44,
                    spriteHeight = 40,
                    rows = 1,
                    columns = 8
            )
        }

//        sprite.playAnimationLooped(animations[Walking], 0.15.seconds);
    }
}