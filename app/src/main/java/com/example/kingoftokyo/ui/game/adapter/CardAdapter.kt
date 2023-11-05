
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.model.Card

class CardAdapter (private var cardList: List<Card>,  private val cardClickListener: OnCardClickListener?) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    interface OnCardClickListener {
        fun onCardClicked(cardId: Int)
    }
    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImageView: ImageView = itemView.findViewById(R.id.cardImageView)
        val textCostCard: TextView = itemView.findViewById(R.id.TextCostCard)
        val textDescriptionCard: TextView = itemView.findViewById(R.id.TextDescriptionCard)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        
        holder.cardImageView.setImageResource(card.imageResId)
        holder.textCostCard.text = card.cost.toString()
        holder.textDescriptionCard.text = card.description

        holder.cardImageView.setOnClickListener{
            cardClickListener?.onCardClicked(card.id)
        }
    }

    override fun getItemCount() = cardList.size

    fun updateCards(newCardList: List<Card>) {
        cardList = newCardList
        notifyDataSetChanged()
    }

}