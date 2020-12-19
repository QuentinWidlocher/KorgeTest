import com.soywiz.klock.hr.timeSpan
import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.box2d.BoxShape
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

suspend fun main() = Korge(width = 512, height = 512) {
	solidRect(this.width, 32.0, Colors.WHITE) {
		xy(0.0, this@Korge.height - height)
	}

	val r = Ristar(this).apply {
		sprite.xy(width / 2, height / 2)
	}
}