package com.thefinalscompanion

import LeaderboardEntry
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(viewGroup.context).inflate(R.layout.item_leaderboard_entry, viewGroup, false)
    ) {
        val tvWorldRank: TextView = itemView.findViewById(R.id.tvWorldRank)
        val tvRankChange: TextView = itemView.findViewById(R.id.tvRankChange)
        val tvSteamName: TextView = itemView.findViewById(R.id.tvSteamName)
        val tvRankedRank: TextView = itemView.findViewById(R.id.tvRankedRank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.tvWorldRank.text = "World Rank: ${entry.worldRank}"
        holder.tvRankChange.text = entry.rankChange + " Last 24h"
        holder.tvSteamName.text = "Steam Name: ${entry.steamName}"
        holder.tvRankedRank.text = entry.rankedRank
    }

    override fun getItemCount(): Int = entries.size
}
