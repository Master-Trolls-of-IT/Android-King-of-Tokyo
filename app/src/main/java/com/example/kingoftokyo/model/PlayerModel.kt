data class PlayerCharacter(
    val id: Int,
    val name: String,
    val characterImageResId: Int
)

data class Player(
    val name: String,
    val selectedCharacter: PlayerCharacter
)
