package com.thefinalscompanion

import LeaderboardEntry
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.lang.StrictMath.abs

class LeaderboardAdapter(private val entries: List<LeaderboardEntry>, private val onItemClick: (LeaderboardEntry) -> Unit) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWorldRank: TextView = view.findViewById(R.id.tvWorldRank)
        val tvRankChange: TextView = view.findViewById(R.id.tvRankChange)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val ivRankedRank: ImageView = view.findViewById(R.id.ivRankedRank)
        val imageViewRankChange: ImageView = view.findViewById(R.id.ivRankChange)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_entry, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        val displayName = when {
            entry.steam.isNotEmpty() -> entry.steam
            entry.psn.isNotEmpty() -> entry.psn
            entry.xbox.isNotEmpty() -> entry.xbox
            else -> entry.name
        }

        holder.tvWorldRank.text = entry.r.toString()
        holder.tvName.text = displayName
        holder.ivRankedRank.setImageResource(getRankedIcon(entry.ri))
        holder.itemView.setOnClickListener {
            onItemClick(entry) // Appel du callback quand un élément est cliqué
        }

        val rankChange = entry.or - entry.r // Calculer la variation de rang
        when {
            rankChange > 0 -> {
                holder.imageViewRankChange.setImageResource(R.drawable.rank_up)
                holder.tvRankChange.text = rankChange.toString()
            }
            rankChange < 0 -> {
                holder.imageViewRankChange.setImageResource(R.drawable.rank_down)
                holder.tvRankChange.text = abs(rankChange).toString() // Pour suppr le - devant le nombre
            }
            else -> {
                holder.imageViewRankChange.setImageResource(R.drawable.rank_null)
                holder.tvRankChange.text = ""
            }
        }
    }

    override fun getItemCount() = entries.size

    private fun getRankedIcon(ri: Int): Int {
        return when (ri) {
            0 -> R.drawable.unranked
            1 -> R.drawable.b4
            2 -> R.drawable.b3
            3 -> R.drawable.b2
            4 -> R.drawable.b1
            5 -> R.drawable.s4
            6 -> R.drawable.s3
            7 -> R.drawable.s2
            8 -> R.drawable.s1
            9 -> R.drawable.g4
            10 -> R.drawable.g3
            11 -> R.drawable.g2
            12 -> R.drawable.g1
            13 -> R.drawable.p4
            14 -> R.drawable.p3
            15 -> R.drawable.p2
            16 -> R.drawable.p1
            17 -> R.drawable.d4
            18 -> R.drawable.d3
            19 -> R.drawable.d2
            20 -> R.drawable.d1
            else -> R.drawable.unranked
        }
    }

}

