data class LeaderboardEntry(
    val worldRank: Int,
    val rankChange: String, // Peut être "+1", "-1", etc.
    val steamName: String,
    val rankedRank: String // "Gold", "Platine", "Diamond", etc.
)
