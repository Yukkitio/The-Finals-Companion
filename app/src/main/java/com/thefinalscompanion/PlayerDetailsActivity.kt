package com.thefinalscompanion

import LeaderboardEntry
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.edit
import kotlin.math.absoluteValue

class PlayerDetailsActivity : ComponentActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_details)

        prefs = getSharedPreferences("Favorites", MODE_PRIVATE)
        val favoriteName = prefs.getString("favoriteName", null)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish() // Termine l'activité courante
        }

        val playerDetails: LeaderboardEntry? = intent.getParcelableExtra("playerDetails")
        setupUI(playerDetails, favoriteName)
    }

    private fun setupUI(playerDetails: LeaderboardEntry?, favoriteName: String?) {
        playerDetails?.let { details ->
            val favoriteCheckbox = findViewById<CheckBox>(R.id.favoriteCheckbox)
            favoriteCheckbox.isChecked = details.name == favoriteName

            findViewById<TextView>(R.id.worldRankValue).text = details.r.toString()

            val rankChange = details.or - details.r
            findViewById<TextView>(R.id.rankChange).text = when {
                rankChange > 0 -> "+ $rankChange"
                rankChange < 0 -> "- ${rankChange.absoluteValue}"
                else -> "-"
            }

            findViewById<TextView>(R.id.embarkPseudo).text = details.name.ifEmpty { "N/A" }
            findViewById<TextView>(R.id.steamName).text = details.steam.ifEmpty { "-" }
            findViewById<TextView>(R.id.xboxName).text = details.xbox.ifEmpty { "-" }
            findViewById<TextView>(R.id.psnName).text = details.psn.ifEmpty { "-" }
            findViewById<ImageView>(R.id.rankIcon).setImageResource(getRankedIcon(details.ri))

            favoriteCheckbox.setOnCheckedChangeListener { _, isChecked ->
                prefs.edit {
                    if (isChecked) putString("favoriteName", details.name)
                    else remove("favoriteName")
                    setResult(RESULT_OK)
                }
            }
        }
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
