package com.thefinalscompanion

import LeaderboardEntry
import LeaderboardResponse
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class PlayerDetailsActivity : ComponentActivity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var worldRankProgressBar: ProgressBar
    private lateinit var last24hProgressBar: ProgressBar
    private lateinit var rankIconProgressBar: ProgressBar
    private lateinit var worldRankValue: TextView
    private lateinit var rankChange: TextView
    private lateinit var rankIcon: ImageView
    private lateinit var refreshButtonDetails: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_details)

        worldRankProgressBar = findViewById(R.id.worldRankProgressBar)
        last24hProgressBar = findViewById(R.id.last24hProgressBar)
        rankIconProgressBar = findViewById(R.id.rankIconProgressBar)
        worldRankValue = findViewById(R.id.worldRankValue)
        rankChange = findViewById(R.id.rankChange)
        rankIcon = findViewById(R.id.rankIcon)
        refreshButtonDetails = findViewById(R.id.refreshButtonDetails)
        prefs = getSharedPreferences("Favorites", MODE_PRIVATE)
        val favoriteName = prefs.getString("favoriteName", null)
        val playerDetails: LeaderboardEntry? = intent.getParcelableExtra("playerDetails")

        setupUI(playerDetails, favoriteName)

        findViewById<Button>(R.id.refreshButtonDetails).setOnClickListener {
            refreshPlayerDetails(playerDetails?.name)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish() // Termine l'activité courante
        }
    }
    private fun refreshPlayerDetails(playerName: String?) {
        playerName?.let { name ->
            toggleLoadingContent(true, worldRankProgressBar, worldRankValue)
            toggleLoadingContent(true,last24hProgressBar, rankChange)
            toggleLoadingContent(true, rankIconProgressBar, rankIcon)
            val apiUrl = "https://api.the-finals-leaderboard.com/v1/leaderboard/s2/crossplay?raw=true&name=$name"
            lifecycleScope.launch {
                try {
                    val dataJson = NetworkService().fetchLeaderboardData(apiUrl)
                    val response = Gson().fromJson(dataJson, LeaderboardResponse::class.java)
                    if (response.data.isNotEmpty()) {
                        setupUI(response.data.first(), prefs.getString("favoriteName", null))
                    }
                } catch (e: Exception) {
                    Log.e("PlayerDetailsActivity", "Error fetching player details: ", e)
                } finally {
                    toggleLoadingContent(false, worldRankProgressBar, worldRankValue)
                    toggleLoadingContent(false, last24hProgressBar, rankChange)
                    toggleLoadingContent(false, rankIconProgressBar, rankIcon)
                }
            }
        }
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
    private fun toggleLoadingContent(loading: Boolean, progressBar: ProgressBar, content: View) {
        refreshButtonDetails.isEnabled = !loading
        if (loading) {
            progressBar.visibility = View.VISIBLE
            content.visibility = View.INVISIBLE
            refreshButtonDetails.alpha = 0.5f
        } else {
            progressBar.visibility = View.GONE
            content.visibility = View.VISIBLE
            refreshButtonDetails.alpha = 1.0f
        }
    }

}
