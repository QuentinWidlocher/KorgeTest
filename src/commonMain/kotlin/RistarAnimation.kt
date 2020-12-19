enum class RistarAnimation(
    val file: String,
    val width: Int,
    val height: Int,
    val marginTop: Int,
    val marginLeft: Int,
    val columns: Int,
    val rows: Int
) {
    Idle("RistarIdle.png",          26, 39, 0, 0, 1, 1),
    Walking("RistarWalking.png",    44, 40, 0, 0, 8, 1),
    Jumping("RistarJumping.png",    40, 48, 0, 0, 1, 1),
    Falling("RistarFalling.png",    40, 48, 0, 0, 3, 1),
}