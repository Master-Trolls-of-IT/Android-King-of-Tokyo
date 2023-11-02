data class PlayerCharacter(
    val id: Int,
    val name: String,
    val characterImageResId: Int
)

data class Player(
    val name: String,
    val selectedCharacter: PlayerCharacter
)

data class PlayerModel(
    val name: String,
    val id: Int,
    val characterImageResId: Int,
    val victoryPoints: Int,
    val healthPoints: Int
)