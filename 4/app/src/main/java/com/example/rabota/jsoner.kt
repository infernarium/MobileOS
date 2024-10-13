import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type
import java.util.UUID

data class Photo(
    val id: String,
    val photoPath: String,
    val title: String,
    val description: String,
    val tags: List<String>
)

object Jsoner {
    private val gson = Gson()

    // Метод для сохранения метаданных о фотографиях в JSON файл
    fun savePhotoMetadataToFile(context: Context, photo: Photo) {
        val metadataFile = File(context.getExternalFilesDir(null), "photo_metadata.json")

        // Считывание существующих данных
        val existingPhotos = loadPhotosFromFile(context)

        // Добавление новой фотографии
        existingPhotos.add(photo)

        // Запись обновленных данных в файл
        FileWriter(metadataFile).use { writer ->
            gson.toJson(existingPhotos, writer)
        }
    }

    // Метод для загрузки метаданных о фотографиях из JSON файла
    fun loadPhotosFromFile(context: Context): MutableList<Photo> {
        val metadataFile = File(context.getExternalFilesDir(null), "photo_metadata.json")

        if (!metadataFile.exists()) {
            return mutableListOf()
        }

        FileReader(metadataFile).use { reader ->
            val type: Type = object : TypeToken<MutableList<Photo>>() {}.type
            return gson.fromJson(reader, type) ?: mutableListOf()
        }
    }
}
