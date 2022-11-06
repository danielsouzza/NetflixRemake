package co.tiagoaguiar.netflixremake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Banner
import com.squareup.picasso.Picasso

class MovieAdapter(
    private val banners:List<Banner>,
    @LayoutRes private val layout:Int,
    private val onItemClickListener: ((Int) -> Unit)? = null
    ): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = banners[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return banners.size;
    }

    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(banner:Banner){
            val imgCover: ImageView = itemView.findViewById(R.id.move_img)
            imgCover.setOnClickListener{
                onItemClickListener?.invoke(banner.id)
            }
            Picasso.get().load(banner.coverUrl).into(imgCover)
//            DownloadImageTask(object :DownloadImageTask.Callback{
//                override fun onResult(bitmap: Bitmap){
//                    imgCover.setImageBitmap(bitmap)
//                }
//            }).execute(movie.coverUrl)
        }
    }
}