package com.thefinalscompanion

import LeaderboardEntry
import LeaderboardResponse
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var refreshButton: Button
    private lateinit var favoriteProgressBar: ProgressBar
    private lateinit var leaderboardProgressBar: ProgressBar
    private lateinit var nextUpdateTimeTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val updateInterval: Long = 20 * 60 * 1000 // 20 minutes en millisecondes
    private val TAG = "MainActivity"
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            displayFavoriteIfNeeded()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        startTimer()
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
        nextUpdateTimeTextView = findViewById(R.id.nextUpdateTimeTextView)

        favoriteRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch {
                    delay(500)
                    fetchLeaderboard(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun startTimer() {
        val currentTime = Calendar.getInstance().timeInMillis
        val elapsedTime = currentTime % updateInterval
        val timeRemaining = updateInterval - elapsedTime
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                nextUpdateTimeTextView.text = formatTime(millisUntilFinished)
            }
            override fun onFinish() {
                displayFavoriteIfNeeded()
                fetchLeaderboard()
                startTimer()
            }
        }.start()
    }

    private fun formatTime(millis: Long): String {
        val formatter = SimpleDateFormat("00 : mm : ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(millis)
    }

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

    private fun buildApiUrl(query: String): String {
        val baseUrl = "https://api.the-finals-leaderboard.com/v1/leaderboard/s2/crossplay?raw=true"
        return if (query.isNotEmpty()) "$baseUrl&name=$query" else baseUrl
    }

    private fun displayFavoriteIfNeeded() {
        val sharedPreferences = getSharedPreferences("Favorites", MODE_PRIVATE)
        var favoriteName = sharedPreferences.getString("favoriteName", null)
        Log.d(TAG, "Favorite name from SharedPreferences: $favoriteName")

        favoriteName = favoriteName?.replace("#", "%23")
        if (!favoriteName.isNullOrEmpty()) {
            fetchFavoriteDetails(favoriteName)
        } else {
            Log.d(TAG, "No favorite name found in SharedPreferences.")
            favoriteRecyclerView.adapter = null
        }
    }


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

    private fun updateRecyclerView(recyclerView: RecyclerView, data: List<LeaderboardEntry>) {
        val adapter = LeaderboardAdapter(data) { entry ->
            val intent = Intent(this@MainActivity, PlayerDetailsActivity::class.java).apply {
                putExtra("playerDetails", entry)
            }
            startForResult.launch(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun setRecyclerViewLoading(recyclerView: RecyclerView, progressBar: ProgressBar, isLoading: Boolean) {
        refreshButton.isEnabled = !isLoading

        if (isLoading) {
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            refreshButton.alpha = 0.5f
        } else {
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            refreshButton.alpha = 1.0f
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
