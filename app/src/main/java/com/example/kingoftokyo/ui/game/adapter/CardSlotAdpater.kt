package com.example.kingoftokyo.ui.game.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.model.Card

class CardSlotAdpater (private var cardList: List<Card>, private val cardSlotClickListener: CardSlotClickListener) : RecyclerView.Adapter<CardSlotAdpater.CardSlotViewHolder>() {

    var isAttackState: Boolean = false

    class CardSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardContainer : ConstraintLayout = itemView.findViewById(R.id.characterCardView)
        val cardImageView: ImageView = itemView.findViewById(R.id.cardImageView)
        val textCostCard: TextView = itemView.findViewById(R.id.TextCostCard)
        val textDescriptionCard: TextView = itemView.findViewById(R.id.TextDescriptionCard)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardSlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_card_item, parent, false)
        return CardSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardSlotViewHolder, position: Int) {
        val card = cardList[position]

        holder.cardImageView.setImageResource(card.imageResId)
        holder.textCostCard.text = card.cost.toString()
        holder.textDescriptionCard.text = card.description

        holder.cardContainer.isClickable = isAttackState

        if (isAttackState) {
            holder.cardContainer.setOnClickListener {
                cardSlotClickListener.onSlotCardClick(position)
            }
        }
    }

    override fun getItemCount() = cardList.size

    fun updateCards(newCardList: List<Card>) {
        cardList = newCardList
        notifyDataSetChanged()
    }

    fun updateIsAttackState(newState: Boolean) {
        isAttackState = newState
        notifyDataSetChanged()
    }

    interface CardSlotClickListener {
        fun onSlotCardClick(cardPosition: Int)
    }
}