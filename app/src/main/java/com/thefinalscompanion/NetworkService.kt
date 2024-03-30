package com.thefinalscompanion

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class NetworkService {
    suspend fun fetchLeaderboardData(apiUrl: String): String = withContext(Dispatchers.IO) {
        val url = URL(apiUrl)
        (url.openConnection() as HttpURLConnection).run {
            requestMethod = "GET"
            connect()
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream.bufferedReader().use { it.readText() }
            } else {
                "Erreur : Impossible de récupérer les données"
            }
        }
    }
}
