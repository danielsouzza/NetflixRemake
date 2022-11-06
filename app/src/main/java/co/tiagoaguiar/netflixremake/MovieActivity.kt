package co.tiagoaguiar.netflixremake

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Banner
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import co.tiagoaguiar.netflixremake.util.DownloadImageTask
import co.tiagoaguiar.netflixremake.util.MovieTask

class MovieActivity : AppCompatActivity(),MovieTask.Callback {
    private lateinit var  txtDesc: TextView
    private lateinit var txtCast: TextView
    private lateinit var txtTitle: TextView
    private val banners = mutableListOf<Banner>()
    private lateinit var process: ProgressBar
    private lateinit var adapter: MovieAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val id = intent.extras?.getInt("id") ?: throw IllegalStateException("ID não foi encontrda!")
        process = findViewById(R.id.progress_movie)

        val url = "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=6e243708-9b5a-479c-8713-a47e5a996abf"
        MovieTask(this).execute(url)


        txtTitle = findViewById(R.id.movie_txt_title)
        txtDesc= findViewById(R.id.movie_txt_desc)
        txtCast = findViewById(R.id.movie_txt_cast)
        process = findViewById(R.id.progress_movie)
        val rv:RecyclerView = findViewById(R.id.movie_rv_similar)

        adapter = MovieAdapter(banners,R.layout.move_item_similar){id ->
            val intent = Intent(this@MovieActivity, MovieActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }
        rv.layoutManager = GridLayoutManager(this,3)
        rv.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResult(movie: Movie) {
        process.visibility = View.GONE
        txtTitle.text = movie.title
        txtDesc.text = movie.desc
        txtCast.text = getString(R.string.cast,movie.cast)
        banners.clear()
        banners.addAll(movie.banners)
        adapter.notifyDataSetChanged()

        DownloadImageTask(object :DownloadImageTask.Callback{
            override fun onResult(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity,R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources,bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable,movieCover)
                val coverImg :ImageView = findViewById(R.id.movie_img)
                coverImg.setImageDrawable(layerDrawable)
            }
        }).execute(movie.cover_url)

    }
    override fun onFailure(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    override fun onPreExecute() {
        process.visibility = View.VISIBLE
    }
}