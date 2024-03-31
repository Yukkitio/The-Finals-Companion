package com.thefinalscompanion

import LeaderboardEntry
import LeaderboardResponse
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // Initialisation des composants de l'UI et des variables nécessaires
    private lateinit var searchEditText: EditText
    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var leaderboardRecyclerView: RecyclerView
    private var searchJob: Job? = null
    private val TAG = "MainActivity"
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            displayFavoriteIfNeeded()// Favori potentiellement modifié, rafraîchir l'affichage
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
        displayFavoriteIfNeeded()
        initSearch()
        fetchLeaderboard()
    }
    private fun setupUI() {
        searchEditText = findViewById(R.id.searchEditText)
        favoriteRecyclerView = findViewById(R.id.favoriteRecyclerView)
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)

        favoriteRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
    }
    // Initialisation de la fonctionnalité de recherche
    private fun initSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel() // Annulation de la recherche précédente si l'utilisateur continue de taper
                searchJob = lifecycleScope.launch {
                    delay(500)// Delai pour limiter les requêtes
                    fetchLeaderboard(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Récupération des données du leaderboard
    private fun fetchLeaderboard(query: String = "") {
        val apiUrl = buildApiUrl(query)
        lifecycleScope.launch {
            try {
                val dataJson = NetworkService().fetchLeaderboardData(apiUrl)
                val response = Gson().fromJson(dataJson, LeaderboardResponse::class.java)
                updateRecyclerView(leaderboardRecyclerView, response.data)
            } catch (e: Exception) {
                Log.e(TAG,"Error fetching leaderboard : ", e)
            }
        }
    }

    // Construction de l'URL de l'API en fonction de la demande
    private fun buildApiUrl(query: String): String {
        val baseUrl = "https://api.the-finals-leaderboard.com/v1/leaderboard/s2/crossplay?raw=true"
        return if (query.isNotEmpty()) "$baseUrl&name=$query" else baseUrl
    }

    // Affichage du favori s'il existe
    private fun displayFavoriteIfNeeded() {
        val favoriteName = getSharedPreferences("Favorites", MODE_PRIVATE).getString("favoriteName", null)
        if (!favoriteName.isNullOrEmpty()) {
            fetchFavoriteDetails(favoriteName)
        } else {
            favoriteRecyclerView.adapter = null
        }
    }

    // Récupération des détails du favori
    private fun fetchFavoriteDetails(favoriteName: String) {
        val apiUrl = buildApiUrl(favoriteName)
        lifecycleScope.launch {
            try {
                val dataJson = NetworkService().fetchLeaderboardData(apiUrl)
                val response = Gson().fromJson(dataJson, LeaderboardResponse::class.java)
                if (response.data.isNotEmpty()) {
                    updateRecyclerView(favoriteRecyclerView, listOf(response.data.first()))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching favorite details", e)
            }
        }
    }

    // Mise à jour du tableau
    private fun updateRecyclerView(recyclerView: RecyclerView, data: List<LeaderboardEntry>) {
        val adapter = LeaderboardAdapter(data) { entry ->
            val intent = Intent(this, PlayerDetailsActivity::class.java).apply {
                putExtra("playerDetails", entry)
            }
            startForResult.launch(intent)
        }
        recyclerView.adapter = adapter
    }
}
