import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.model.DiceModel

class DiceAdapter(public var diceList: List<DiceModel>, private val diceClickListener: DiceClickListener) : RecyclerView.Adapter<DiceAdapter.DiceViewHolder>() {
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

        if (!dice.isRollable) {
            val borderDrawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.border)
            val layers = arrayOf(holder.diceImageView.drawable, borderDrawable)
            val layerDrawable = LayerDrawable(layers)
            holder.diceImageView.setImageDrawable(layerDrawable)
        }

        holder.diceImageView.setOnClickListener{
            diceClickListener.onDiceClicked(position)
        }
    }

    override fun getItemCount() = diceList.size

    fun updateDice(newDiceList: List<DiceModel>) {
        diceList = newDiceList
        notifyDataSetChanged()
    }

    fun toggleRollability(diceId: Int) {
        updateDice (diceList.map {
            if (it.id == diceId && it.name != "LoNoSe") {
                DiceModel(it.id, it.name, it.imageResId, it.value, !it.isRollable)
            }
            else it
         })
    }

    interface DiceClickListener {
        fun onDiceClicked(diceId: Int)
    }

}
