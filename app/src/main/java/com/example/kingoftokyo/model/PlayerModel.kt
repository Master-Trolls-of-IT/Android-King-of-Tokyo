import android.os.Parcel
import android.os.Parcelable

data class PlayerCharacter(
    val id: Int,
    val name: String,
    val characterImageResId: Int
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(characterImageResId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayerCharacter> {
        override fun createFromParcel(parcel: Parcel): PlayerCharacter {
            return PlayerCharacter(parcel)
        }

        override fun newArray(size: Int): Array<PlayerCharacter?> {
            return arrayOfNulls(size)
        }
    }
}

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