package com.thefinalscompanion

import LeaderboardEntry
import LeaderboardResponse
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private var searchJob: Job? = null // Job pour gérer la recherche différée
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchEditText: EditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel() // Annule la recherche précédente si l'utilisateur continue de taper
                searchJob = lifecycleScope.launch {
                    delay(500) // Délai pour réduire le nombre de requêtes API
                    fetchLeaderboard(s.toString().takeIf { it.isNotEmpty() }) // Pass null pour une recherche globale si vide
                }
            }
        })
        fetchLeaderboard(null)
    }

    private fun fetchLeaderboard(query: String?) {
        val networkService = NetworkService()
        val baseUrl = "https://api.the-finals-leaderboard.com/v1/leaderboard/s2/crossplay?raw=true"

        // Construire l'URL de la requête en fonction de la présence ou non d'une requête de recherche
        val apiUrl = if (query.isNullOrEmpty()) {
            baseUrl // Recherche globale si le query est vide ou null
        } else {
            "$baseUrl&name=$query" // Ajouter le paramètre de recherche si query contient du texte
        }

        // Lancer une coroutine pour effectuer une opération de réseau
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val dataJson = networkService.fetchLeaderboardData(apiUrl)
                val response: LeaderboardResponse = Gson().fromJson(dataJson, LeaderboardResponse::class.java)
                val leaderboardEntries = response.data // Maintenant vous avez la liste
                setupRecyclerView(leaderboardEntries)
            } catch (e: Exception) {
                e.printStackTrace()
                // Gérer l'erreur, par exemple, afficher un message à l'utilisateur
            }
        }
    }


    private fun setupRecyclerView(leaderboardEntries: List<LeaderboardEntry>) {
        val adapter = LeaderboardAdapter(leaderboardEntries)
        val recyclerView: RecyclerView = findViewById(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
