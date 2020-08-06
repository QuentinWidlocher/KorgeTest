import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korge.view.sprite
import com.soywiz.korge.view.xy
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

suspend fun main() = Korge(width = 512, height = 512) {
	Ristar(this).sprite.xy(width/2, height/2)
}