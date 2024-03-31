import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class LeaderboardResponse(
    val meta: MetaData,
    val count: Int,
    val data: List<LeaderboardEntry>
)

data class MetaData(
    val leaderboardVersion: String,
    val leaderboardPlatform: String,
    val returnRawData: Boolean,
    val returnCountOnly: Boolean
)

@Parcelize
data class LeaderboardEntry(
    val r: Int, // Rank
    val name: String, // Nom du joueur
    val ri: Int, // Rank Index, utilisé pour déterminer l'icône de rang
    val p: Int, // Pas clair dans la description, mais inclus si nécessaire
    val ori: Int, // Old Rank Index, pour calculer la variation de rang
    val or: Int, // Old Rank, également pour la variation
    val op: Int, // Semblable à `p`, la signification exacte n'est pas claire
    val c: Int, // Potentiellement pour "cashouts", mais non utilisé dans l'interface
    val steam: String, // Nom d'utilisateur Steam
    val xbox: String, // Nom d'utilisateur Xbox, inclus si nécessaire
    val psn: String // Nom d'utilisateur PSN, inclus si nécessaire
) : Parcelable
