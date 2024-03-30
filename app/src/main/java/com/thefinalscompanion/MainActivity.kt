package com.thefinalscompanion

import LeaderboardEntry
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Simuler des données pour le leaderboard
        val leaderboardEntries = listOf(
            LeaderboardEntry(worldRank = 1, rankChange = "+1", steamName = "PlayerOne", rankedRank = "Diamond"),
            LeaderboardEntry(worldRank = 2, rankChange = "-1", steamName = "PlayerTwo", rankedRank = "Platinum"),
            // Ajoutez plus d'entrées ici...
        )

        // Trouver le RecyclerView dans votre layout
        val recyclerView = findViewById<RecyclerView>(R.id.leaderboardRecyclerView).apply {
            // Définir un LinearLayoutManager pour votre RecyclerView
            layoutManager = LinearLayoutManager(this@MainActivity)
            // Initialiser l'adaptateur du RecyclerView avec les données du leaderboard
            adapter = LeaderboardAdapter(leaderboardEntries)
        }
    }
}
