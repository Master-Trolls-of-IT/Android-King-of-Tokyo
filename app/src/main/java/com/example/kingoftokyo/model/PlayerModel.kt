data class PlayerCharacter(
    val id: Int,
    val name: String
)

data class Player(
    val name: String,
    val selectedCharacter: PlayerCharacter
)
