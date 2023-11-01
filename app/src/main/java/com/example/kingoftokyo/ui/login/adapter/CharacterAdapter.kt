import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R

class CharacterAdapter(private var characters: List<PlayerCharacter>) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characters[position]
        holder.characterNameTextView.text = character.name
        holder.characterImageView.setImageResource(character.characterImageResId)
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    inner class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val characterNameTextView: TextView = itemView.findViewById(R.id.characterName)
        val characterImageView: ImageView = itemView.findViewById(R.id.characterImage)
    }

    fun updateCharacter(newCharacters: List<PlayerCharacter>) {
        characters = newCharacters
    }
}
