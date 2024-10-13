package com.example.rabota

import Photo
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class CreatePhotoActivity : AppCompatActivity() {

    private lateinit var takePhotoButton: Button
    private lateinit var selectPhotoButton: Button
    private lateinit var savePhotoButton: Button
    private lateinit var photoImageView: ImageView
    private lateinit var photoNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var tagsEditText: EditText

    private var photoUri: Uri? = null
    private var photoPath: String? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_photo)

        // Инициализация UI элементов
        takePhotoButton = findViewById(R.id.takePhotoButton)
        selectPhotoButton = findViewById(R.id.selectPhotoButton)
        savePhotoButton = findViewById(R.id.savePhotoButton)
        photoImageView = findViewById(R.id.photoImageView)
        photoNameEditText = findViewById(R.id.photoNameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        tagsEditText = findViewById(R.id.tagsEditText)

        // Обработчик кнопки для съемки фотографии
        takePhotoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Обработчик кнопки для выбора фотографии из устройства
        selectPhotoButton.setOnClickListener {
            dispatchSelectPictureIntent()
        }

        // Обработчик кнопки для сохранения фотографии
        savePhotoButton.setOnClickListener {
            savePhotoWithTags()
        }
    }

    // Метод для запуска камеры
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Создаем файл для сохранения фотографии
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Ошибка при создании файла
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show()
                null
            }

            // Если файл создан успешно, запускаем камеру
            photoFile?.also {
                photoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.rabota.fileprovider",  // Должно соответствовать манифесту
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    // Метод для выбора фотографии из галереи
    private fun dispatchSelectPictureIntent() {
        val selectPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectPictureIntent.type = "image/*"
        startActivityForResult(selectPictureIntent, REQUEST_IMAGE_PICK)
    }

    // Создание файла для сохранения фотографии
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            photoPath = absolutePath
        }
    }

    // Обработка результата камеры или выбора фотографии
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Отображение фотографии с камеры
                    photoUri?.let {
                        photoImageView.setImageURI(it)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    // Получение выбранной фотографии из галереи
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        photoUri = selectedImageUri
                        saveSelectedImageToStorage(selectedImageUri)
                        photoImageView.setImageURI(photoUri)
                    }
                }
            }
        }
    }

    // Сохранение выбранного изображения в локальное хранилище
    private fun saveSelectedImageToStorage(imageUri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(storageDir, UUID.randomUUID().toString() + ".jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            photoPath = file.absolutePath
            Toast.makeText(this, "Photo selected and saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving selected photo!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePhotoWithTags() {
        val title = photoNameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val tags = tagsEditText.text.toString().split(",").map { it.trim() }.toMutableList()

        if (title.isNotEmpty() && photoPath != null) {
            // Создаем объект Photo
            val photo = Photo(
                id = UUID.randomUUID().toString(),
                photoPath = photoPath!!,
                title = title,
                description = description,
                tags = tags
            )

            // Сохранение метаданных в JSON файл
            Jsoner.savePhotoMetadataToFile(this, photo)

            Toast.makeText(this, "Photo with tags saved!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Please select or take a photo and enter a title.", Toast.LENGTH_SHORT).show()
        }
    }
}
