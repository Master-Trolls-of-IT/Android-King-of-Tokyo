import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R

class CharacterAdapter : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    private val characters: List<PlayerCharacter> = listOf(/* Liste des personnages */)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characters[position]
        // Associez les données du personnage à la vue (ex. image, nom, etc.)
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    inner class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Références aux éléments de la vue à l'intérieur de chaque élément de la liste
    }
}
