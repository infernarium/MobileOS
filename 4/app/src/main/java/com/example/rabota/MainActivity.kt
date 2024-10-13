package com.example.rabota

import Photo
import PhotoAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Environment
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var createPhotoButton: Button
    private lateinit var photoAdapter: PhotoAdapter
    private var photos: MutableList<Photo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        createPhotoButton = findViewById(R.id.createPhotoButton)

        // Инициализация адаптера с пустым списком фотографий
        photoAdapter = PhotoAdapter(photos)
        recyclerView.adapter = photoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Обработчик кнопки для создания новой фотографии
        createPhotoButton.setOnClickListener {
            val intent = Intent(this, CreatePhotoActivity::class.java)
            startActivity(intent)
        }

        // Обработчик поиска
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPhotos(newText)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Перезагрузка фотографий при возврате в активность
        loadPhotos()
    }

    // Метод для загрузки фотографий
    private fun loadPhotos() {
        val metadataFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo_metadata.json")
        photos = Jsoner.loadPhotosFromFile(this) // Загрузка фотографий из JSON файла

        // Обновляем данные в адаптере
        photoAdapter.updatePhotos(photos)
    }

    // Метод для фильтрации фотографий по названию или тегам
    private fun filterPhotos(query: String?) {
        val filteredPhotos = if (query.isNullOrEmpty()) {
            photos
        } else {
            photos.filter { photo ->
                photo.title.contains(query, true) || photo.tags.any { tag -> tag.contains(query, true) }
            }
        }
        photoAdapter.updatePhotos(filteredPhotos)
    }
}
