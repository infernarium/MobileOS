import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabota.R

class PhotoAdapter(private var photos: List<Photo>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() { // Изменено на var


    // Метод для обновления списка фотографий
    fun updatePhotos(newPhotos: List<Photo>) {
        this.photos = newPhotos // Теперь это корректно
        notifyDataSetChanged() // Уведомляем адаптер об изменениях
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.photoImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.titleTextView.text = photo.title
        holder.descriptionTextView.text = photo.description
        holder.imageView.setImageURI(Uri.parse(photo.photoPath)) // Здесь вы можете использовать Glide или другую библиотеку для загрузки изображений
    }

    override fun getItemCount() = photos.size
}
