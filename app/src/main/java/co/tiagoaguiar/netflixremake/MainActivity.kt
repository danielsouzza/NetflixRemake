package co.tiagoaguiar.netflixremake
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.util.CategoryTask


class MainActivity : AppCompatActivity(), CategoryTask.Callback {
    private final val URL = "https://api.tiagoaguiar.co/netflixapp/home?apiKey=6e243708-9b5a-479c-8713-a47e5a996abf"
    private lateinit var process: ProgressBar
    private lateinit var adapter: CategoryAdapter
    private val categories =  mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        process = findViewById(R.id.progress_bar)

        adapter = CategoryAdapter(categories){id ->
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }

        val rv:RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        CategoryTask(this).execute(URL)

    }

    override fun onPreExecute() {
        process.visibility = View.VISIBLE
    }

    override fun onResult(categories: List<Category>) {
        process.visibility = View.GONE
        this.categories.clear()
        this.categories.addAll(categories)
        adapter.notifyDataSetChanged()

    }

    override fun onFailure(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        process.visibility = View.GONE
    }
}