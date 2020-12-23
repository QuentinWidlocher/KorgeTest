import com.soywiz.korge.*
import com.soywiz.korge.view.ktree.KTreeRoot
import com.soywiz.korge.view.ktree.KTreeSerializer
import com.soywiz.korge.view.ktree.readKTree
import com.soywiz.korio.file.std.resourcesVfs

suspend fun main() = Korge(width = 512, height = 512) {

	val file = resourcesVfs["TestLevel.ktree"]
	addChild(file.readKTree(views))

	ristar(32.0, 464.0)

	// Idk why but removing the CameraContainer prevent unwanted collisions
	// Because the type shifts to Container and we cannot filter it later
	removeChild(firstChild)
}