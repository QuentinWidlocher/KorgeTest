import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korge.view.sprite
import com.soywiz.korge.view.xy
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

suspend fun main() = Korge(width = 512, height = 512) {
	val spriteMap = resourcesVfs["RistarWalking.png"].readBitmap()
	val ristarWalking = SpriteAnimation(
			spriteMap = spriteMap,
			spriteWidth = 44,
			spriteHeight = 40,
			rows = 1,
			columns = 8
	)
	sprite(ristarWalking).xy(width/2, height/2).playAnimationLooped(spriteDisplayTime = 0.15.seconds)
}