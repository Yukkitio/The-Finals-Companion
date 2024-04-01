package com.thefinalscompanion

import LeaderboardEntry
import LeaderboardResponse
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
    private lateinit var refreshButton: Button
    private lateinit var favoriteProgressBar: ProgressBar
    private lateinit var leaderboardProgressBar: ProgressBar
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
        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            displayFavoriteIfNeeded()
            fetchLeaderboard()
        }
        favoriteProgressBar = findViewById(R.id.favoriteProgressBar)
        leaderboardProgressBar = findViewById(R.id.leaderboardProgressBar)

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
        setRecyclerViewLoading(leaderboardRecyclerView, leaderboardProgressBar, true)
        val apiUrl = buildApiUrl(query)
        lifecycleScope.launch {
            try {
                val dataJson = NetworkService().fetchLeaderboardData(apiUrl)
                val response = Gson().fromJson(dataJson, LeaderboardResponse::class.java)
                updateRecyclerView(leaderboardRecyclerView, response.data)
            } catch (e: Exception) {
                Log.e(TAG,"Error fetching leaderboard : ", e)
            } finally {
                setRecyclerViewLoading(leaderboardRecyclerView, leaderboardProgressBar, false)
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
        setRecyclerViewLoading(favoriteRecyclerView, favoriteProgressBar, true)
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
            } finally {
                setRecyclerViewLoading(favoriteRecyclerView, favoriteProgressBar, false)
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

    // Mise a jour UI progressbar
    private fun setRecyclerViewLoading(recyclerView: RecyclerView, progressBar: ProgressBar, isLoading: Boolean) {
        refreshButton.isEnabled = !isLoading

        if (isLoading) {
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            refreshButton.alpha = 0.5f // Rendre le bouton semi-transparent pour indiquer qu'il est désactivé
        } else {
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            refreshButton.alpha = 1.0f // Restaurer la pleine opacité pour indiquer que le bouton est à nouveau actif
        }
    }




}
