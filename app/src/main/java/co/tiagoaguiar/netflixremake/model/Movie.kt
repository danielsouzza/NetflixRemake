package co.tiagoaguiar.netflixremake.model

data class Movie (
    val title:String,
    val desc: String,
    val cast: String,
    val cover_url: String,
    val banners: List<Banner>
    )