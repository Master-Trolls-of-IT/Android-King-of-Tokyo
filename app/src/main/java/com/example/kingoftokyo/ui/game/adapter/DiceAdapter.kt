import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.model.DiceModel

class DiceAdapter(private var diceList: List<DiceModel>) : RecyclerView.Adapter<DiceAdapter.DiceViewHolder>() {

    class DiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diceImageView: ImageView = itemView.findViewById(R.id.diceImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dice_item_layout, parent, false)
        return DiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiceViewHolder, position: Int) {
        val dice = diceList[position]
        holder.diceImageView.setImageResource(dice.imageResId)

    }

    override fun getItemCount() = diceList.size

    fun updateDice(newDiceList: List<DiceModel>) {
        diceList = newDiceList
        notifyDataSetChanged()
    }

}
