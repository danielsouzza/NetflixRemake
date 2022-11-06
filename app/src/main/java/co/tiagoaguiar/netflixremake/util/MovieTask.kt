package co.tiagoaguiar.netflixremake.util

import android.os.Looper
import co.tiagoaguiar.netflixremake.model.Banner
import co.tiagoaguiar.netflixremake.model.Movie
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: Callback) {
    private val handler = android.os.Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback{
        fun onPreExecute()
        fun onResult(movies: Movie)
        fun onFailure(message: String)
    }

    fun execute(url: String){
        callback.onPreExecute()
        executor.execute{
            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            try {
                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection
                urlConnection.readTimeout =  2000
                urlConnection.connectTimeout = 2000

                val statusCode: Int = urlConnection.responseCode
                var jsonAsString: String
                if(statusCode == 400) {
                    stream = urlConnection.errorStream
                    jsonAsString = stream.bufferedReader().use { it.readText() }
                    val json =  JSONObject(jsonAsString)
                    val message = json.getString("message")
                    throw IOException(message)
                }else if(statusCode > 400){
                    throw  IOException("Erro na comunicação com o servidor!")
                }else{
                    stream = urlConnection.inputStream
                    jsonAsString = stream.bufferedReader().use { it.readText() }
                }

                handler.post{
                    callback.onResult(toMovies(jsonAsString))
                }
            }catch (e:IOException){
                val message = e.message?:"erro desconhecido"
                handler.post{
                    callback.onFailure(message)
                }
            }finally {
                urlConnection?.disconnect()
                stream?.close()
            }
        }
    }

    private fun toMovies(jsonAsString: String): Movie{
        val jsonRoot = JSONObject(jsonAsString)
        val jsonTitle = jsonRoot.getString("title")
        val jsonDesc = jsonRoot.getString("desc")
        val jsonCast = jsonRoot.getString("cast")
        val jsonCoverUrl = jsonRoot.getString("cover_url")

        val moviesSimilar = jsonRoot.getJSONArray("movie")
        val banners = mutableListOf<Banner>()

        val moviesSimilarSize = moviesSimilar.length()
        for (i in 0 until moviesSimilarSize){
            val jsonBanner = moviesSimilar.getJSONObject(i)
            val id = jsonBanner.getInt("id")
            val coverUrl = jsonBanner.getString("cover_url")
            banners.add(Banner(id,coverUrl))
        }

        return Movie(jsonTitle,jsonDesc,jsonCast,jsonCoverUrl,banners)
    }

}