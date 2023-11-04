package com.example.kingoftokyo.ui.game.adapter

import PlayerModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R

class OpponentAdapter(private var opponents: List<PlayerModel>) : RecyclerView.Adapter<OpponentAdapter.OpponentViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):OpponentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_board_character_card, parent, false)
        return OpponentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OpponentViewHolder, position: Int) {
        val opponent = opponents[position]
        holder.opponentImageView.setImageResource(opponent.characterImageResId)
        holder.opponentHPView.text = opponent.healthPoints.toString()
        holder.opponentVPView.text = opponent.victoryPoints.toString()
    }

    override fun getItemCount(): Int {
        return opponents.size
    }

    inner class OpponentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val opponentImageView: ImageView = itemView.findViewById(R.id.gameboardCharacterImage)
        val opponentHPView: TextView = itemView.findViewById(R.id.gameboardCharacterCardHP)
        val opponentVPView: TextView = itemView.findViewById(R.id.gameboardCharacterCardVP)
    }

    fun updateOpponents(newOpponents: List<PlayerModel>) {
        opponents = newOpponents
        notifyDataSetChanged()
    }
}