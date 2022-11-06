package co.tiagoaguiar.netflixremake.util

import android.os.Looper
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Banner
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import javax.net.ssl.HttpsURLConnection
import java.net.URL
import java.util.concurrent.Executors

class CategoryTask (private val callback:Callback){

    private val handler = android.os.Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback{
        fun onPreExecute()
        fun onResult(categories:List<Category>)
        fun onFailure(message:String)
    }

    fun execute(url:String){
        callback.onPreExecute()
        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            try {
                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection
                urlConnection.readTimeout =  2000
                urlConnection.connectTimeout = 2000

                val statusCode: Int = urlConnection.responseCode
                if(statusCode > 400){
                    throw  IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream
                val jsonAsString = stream.bufferedReader().use { it.readText() }

                handler.post{
                    callback.onResult(toCategories(jsonAsString))
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

    private fun toCategories(jsonAsString:String) : List<Category>{
        val categories = mutableListOf<Category>()
        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")
        val jsonCategoriesSize = jsonCategories.length()
        for (i in 0 until jsonCategoriesSize){
            val jsonCategory = jsonCategories.getJSONObject(i)
            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")
            val banners = mutableListOf<Banner>()

            val jsonMovieSize = jsonMovies.length()
            for(j in 0 until jsonMovieSize){
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                banners.add(Banner(id,coverUrl))
            }
            categories.add(Category(title,banners))
        }
        return categories
    }
}