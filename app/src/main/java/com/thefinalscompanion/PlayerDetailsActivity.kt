package com.thefinalscompanion

import LeaderboardEntry
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity

class PlayerDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_details)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish() // Termine l'activité courante, revenant ainsi à l'activité précédente dans la pile
        }

        val playerDetails: LeaderboardEntry? = intent.getParcelableExtra("playerDetails")

        findViewById<TextView>(R.id.worldRankValue).text = "${playerDetails?.r ?: "N/A"}"

        val rankChange = (playerDetails?.or ?: 0) - (playerDetails?.r ?: 0)
        val rankChangeText = when {
            rankChange > 0 -> "+$rankChange"
            rankChange < 0 -> "$rankChange"
            else -> "0"
        }
        findViewById<TextView>(R.id.rankChange).text = rankChangeText

        findViewById<TextView>(R.id.embarkPseudo).text = playerDetails?.name ?: "N/A"

        findViewById<TextView>(R.id.steamName).text = playerDetails?.steam ?: "-"
        findViewById<TextView>(R.id.xboxName).text = playerDetails?.xbox ?: "-"
        findViewById<TextView>(R.id.psnName).text = playerDetails?.psn ?: "-"

        findViewById<ImageView>(R.id.rankIcon).setImageResource(getRankedIcon((playerDetails?.ri ?: "0") as Int))

    }

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

