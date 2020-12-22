import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors

suspend fun main() = Korge {
	solidRect(this.width, 32.0, Colors.WHITE) {
		xy(0.0, this@Korge.height - height)
	}

	solidRect(64.0, 32.0, Colors.WHITE) {
		xy(0.0, this@Korge.height - 128.0)
	}

	solidRect(64.0, 32.0, Colors.WHITE) {
		xy(128.0, this@Korge.height - 200.0)
	}

	solidRect(64.0, 32.0, Colors.WHITE) {
		xy(256.0, this@Korge.height - 256.0)
	}

	solidRect(32.0, 128.0, Colors.WHITE) {
		xy(256.0, this@Korge.height - height)
	}

	solidRect(32.0, 128.0, Colors.WHITE) {
		xy(350.0, this@Korge.height - height - 73.0)
	}

	ristar(40.0, height / 2)

}